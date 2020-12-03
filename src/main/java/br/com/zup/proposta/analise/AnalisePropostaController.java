package br.com.zup.proposta.analise;

import br.com.zup.proposta.compartilhado.exception.ApiErrorException;
import br.com.zup.proposta.proposta.Proposta;
import br.com.zup.proposta.proposta.PropostaResponse;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;

@RestController
@RequestMapping("/consultas")
public class AnalisePropostaController {

    private static Logger logger = LoggerFactory.getLogger(AnalisePropostaController.class);

    private AnaliseClient analiseClient;

    private TransactionTemplate transactionTemplate;

    private EntityManager manager;

    public AnalisePropostaController(AnaliseClient analiseClient,
                                     TransactionTemplate transactionTemplate,
                                     EntityManager manager) {
        this.analiseClient = analiseClient;
        this.transactionTemplate = transactionTemplate;
        this.manager = manager;
    }

    @GetMapping("/{id}")
    public ResponseEntity consultaDadosCliente(@PathVariable("id") Long id) {
        Proposta proposta = transactionTemplate.execute(transactionStatus ->
                manager.find(Proposta.class, id)
        );

        SolicitacaoAnalise solicitacaoAnalise = new SolicitacaoAnalise(proposta);
        try {
            ResultadoAnalise resultado = analiseClient.resultado(solicitacaoAnalise);
            proposta.setEstado(resultado.getResultadoSolicitacao());
            logger.info("Solicitação buscada com sucesso");
        } catch (FeignException e) {
            logger.error("Erro " + e.getCause() + " ao buscar solicitação");
            throw new ApiErrorException(HttpStatus.valueOf(e.status()), e.getMessage());
        }

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                manager.persist(proposta);
                logger.info("Proposta atualizada e persistida");
            }
        });
        return ResponseEntity.ok(new PropostaResponse(proposta));
    }
}

