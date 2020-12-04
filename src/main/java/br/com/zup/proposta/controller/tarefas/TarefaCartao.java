package br.com.zup.proposta.controller.tarefas;

import br.com.zup.proposta.compartilhado.exception.ApiErrorException;
import br.com.zup.proposta.dto.externo.CartaoPropostaResponseExterno;
import br.com.zup.proposta.feign.CartaoClient;
import br.com.zup.proposta.model.Proposta;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import java.util.List;

@Component
public class TarefaCartao {

    private CartaoClient cartaoClient;
    private EntityManager manager;
    private TransactionTemplate transactionTemplate;
    private static Logger logger = LoggerFactory.getLogger(TarefaCartao.class);

    public TarefaCartao(CartaoClient cartaoClient, EntityManager manager, TransactionTemplate transactionTemplate) {
        this.cartaoClient = cartaoClient;
        this.manager = manager;
        this.transactionTemplate = transactionTemplate;
    }

    @Scheduled(fixedDelayString = "${periodicidade.executa-operacao}")
    public void executaOperacao() {
        List<Proposta> propostas = transactionTemplate.execute(status ->
                manager.createQuery("Select p from Proposta p").getResultList());

        if (propostas.isEmpty()) {
            logger.info("Não existem propostas no banco");
            return;
        }

        logger.info("Existem {} propostas no banco", propostas.size());

        propostas.stream()
                .filter(Proposta::naoTemCartao)
                .filter(Proposta::naoTemRestricao).forEach(p -> {
            logger.info("verificando cartão da proposta {}", p.getId());
            String id = String.valueOf(p.getId());
            try {
                logger.info("Fazendo chamada externa");
                ResponseEntity<CartaoPropostaResponseExterno> cartao = cartaoClient.buscaCartaoPorId(id);
                if (cartao.getStatusCode().equals(200)) {
                    logger.info("Encontrado cartão {} da proposta {}", cartao.getBody().getId(), p);
                    transactionTemplate
                            .execute(new TransactionCallbackWithoutResult() {
                                @Override
                                protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                                    Proposta proposta = manager.find(Proposta.class, p.getId());
                                    proposta.setCartao(cartao.getBody().toModel());
                                    manager.merge(proposta);
                                    logger.info("Proposta {} atualizada no banco com o cartao {}", proposta.getId(), cartao.getBody().getId());
                                }
                            });
                }

            } catch (FeignException.FeignClientException e) {
                if (e.status() == HttpStatus.BAD_REQUEST.value()) {
                    logger.error("Cartão para a proposta {} ainda não foi gerado", p.getId());
                }
                logger.error("Cartão para a proposta {} ainda não foi gerado, erro {}", p.getId(), e.status());
            } catch (FeignException.FeignServerException e) {
                if (e.status() == HttpStatus.BAD_REQUEST.value()) {
                    logger.error("Cartão para a proposta {} ainda não foi gerado", p.getId());
                    throw new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "erro interno " + e.getMessage());
                }
            }
        });

        logger.info("Executou a tarefa");
    }
}
