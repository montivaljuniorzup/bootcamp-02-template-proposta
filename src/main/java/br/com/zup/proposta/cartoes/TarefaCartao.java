package br.com.zup.proposta.cartoes;

import br.com.zup.proposta.proposta.Proposta;
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
                ResponseEntity<Cartao> cartao = cartaoClient.buscaCartaoPorId(id);
                logger.info("Encontrado cartão {} da proposta {}",cartao.getBody().getId(), p);
                transactionTemplate
                        .execute(new TransactionCallbackWithoutResult() {
                            @Override
                            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                                Proposta proposta = manager.find(Proposta.class, p.getId());
                                proposta.setCartao(cartao.getBody());
                                manager.merge(proposta);
                                logger.info("Proposta {} atualizada no banco com o cartao {}", proposta.getId(), cartao.getBody().getId());
                            }
                        });
            } catch (FeignException e) {
                if (e.status() == HttpStatus.BAD_REQUEST.value()) {
                    logger.error("Cartão para a proposta {} ainda não foi gerado", p.getId());
                }
              logger.error("Cartão para a proposta {} ainda não foi gerado, erro {}", p.getId(), e.status());
            }
        });

        logger.info("Executou a tarefa");
    }
}
