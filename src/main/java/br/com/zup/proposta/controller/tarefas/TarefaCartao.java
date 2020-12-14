package br.com.zup.proposta.controller.tarefas;

import br.com.zup.proposta.compartilhado.exception.ApiErrorException;
import br.com.zup.proposta.dto.externo.CartaoPropostaResponseExterno;
import br.com.zup.proposta.feign.CartaoClient;
import br.com.zup.proposta.model.Proposta;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
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
    @Transactional
    public void executaOperacao() {
        List<Proposta> propostas = manager.createQuery("Select p from Proposta p").getResultList();

        if (propostas.isEmpty()) {
            logger.info("Não existem propostas no banco");
            return;
        }
    
        logger.info("Existem {} propostas no banco", propostas.size());

        propostas.stream()
                .filter(Proposta::naoTemCartao)
                .filter(Proposta::naoTemRestricao).forEach(p -> {
            logger.info("verificando cartão da proposta {}", p.getId());

            try {
                logger.info("Fazendo chamada externa");
                CartaoPropostaResponseExterno cartao = cartaoClient.buscaCartaoPorId(String.valueOf(p.getId()));

                logger.info("Encontrado cartão {} da proposta {}", cartao.getId(), p.getId());

                Proposta proposta = manager.find(Proposta.class, p.getId());
                p.setCartao(cartao.toModel());
                manager.persist(p);
                logger.info("Proposta {} atualizada no banco com o cartao {}", p.getId(), cartao.getId());

            } catch (FeignException.BadRequest e) {
                    logger.error("Cartão para a proposta {} ainda não foi gerado", p.getId());
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
