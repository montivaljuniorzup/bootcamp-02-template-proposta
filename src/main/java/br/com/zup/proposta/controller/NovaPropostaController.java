package br.com.zup.proposta.controller;

import br.com.zup.proposta.compartilhado.exception.ApiErrorException;
import br.com.zup.proposta.dto.externo.AnalisePropostaResponseExterno;
import br.com.zup.proposta.dto.request.AnalisePropostaRequest;
import br.com.zup.proposta.dto.request.NovaPropostaRequest;
import br.com.zup.proposta.feign.AnaliseClient;
import br.com.zup.proposta.model.Proposta;
import feign.FeignException;
import io.opentracing.Span;
import io.opentracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;


@RestController
@RequestMapping("/propostas")
public class NovaPropostaController {

    private static Logger logger = LoggerFactory.getLogger(NovaPropostaController.class);
    private Tracer tracer;
    private AnaliseClient analiseClient;
    private final EntityManager manager;

    public NovaPropostaController(Tracer tracer, AnaliseClient analiseClient, EntityManager manager) {
        this.tracer = tracer;
        this.analiseClient = analiseClient;
        this.manager = manager;
    }

    @PostMapping
    @Transactional
    public ResponseEntity criaNovaProposta(@Valid @RequestBody NovaPropostaRequest novaPropostaRequest, UriComponentsBuilder builder) {

        Span activeSpan = tracer.activeSpan();
        activeSpan.setTag("user.email", novaPropostaRequest.getEmail());
        activeSpan.setBaggageItem("user.email", novaPropostaRequest.getEmail());

        if (verificaSeNaoExisteDocumentoNoBanco(novaPropostaRequest)) {
            Proposta proposta = novaPropostaRequest.toModel();
            manager.persist(proposta);

            ataulizaStatusProposta(proposta, new AnalisePropostaRequest(proposta));

            URI uri = builder.path("/propostas/{id}").buildAndExpand(proposta.getId()).toUri();

            logger.info("Proposta documento={} salário={} criada com sucesso!", proposta.getDocumento(), proposta.getSalario());
            return ResponseEntity.created(uri).build();
        }

        logger.error("Proposta para o documento={} já existe em nosso banco!", novaPropostaRequest.getDocumento());
        throw new ApiErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Dados inconsistentes, impossível proseguir o processamento");
    }

    private boolean verificaSeNaoExisteDocumentoNoBanco(NovaPropostaRequest novaPropostaRequest) {
        return manager
                .createQuery("Select p from Proposta p where p.documento =:documento")
                .setParameter("documento", novaPropostaRequest.getDocumento())
                .getResultList()
                .isEmpty();
    }

    private void ataulizaStatusProposta(Proposta proposta, AnalisePropostaRequest request) {
        try {
            adicionaStatusSemRestricaoEAtualizaProposta(proposta, request);
        } catch (FeignException.UnprocessableEntity e) {
            adicionaStatusComRestricaoEAtualizaProposta(proposta, e);
        } catch (FeignException e) {
            logger.error("Erro " + e.getMessage() + " ao buscar solicitação");
            throw new ApiErrorException(HttpStatus.valueOf(e.status()), e.getLocalizedMessage());
        }
    }

    private void adicionaStatusComRestricaoEAtualizaProposta(Proposta proposta, FeignException.UnprocessableEntity e) {
        if (e.getLocalizedMessage().contains("COM_RESTRICAO")) {
            proposta.setStatus("COM_RESTRICAO");
            logger.info("Adicionado status de Com Retrição a proposta");
            manager.persist(proposta);
        }
    }

    private void adicionaStatusSemRestricaoEAtualizaProposta(Proposta proposta, AnalisePropostaRequest request) {
        AnalisePropostaResponseExterno resultadoAnalise = buscaResultadoAnaliseProposta(request);

        proposta.setStatus(resultadoAnalise.getResultadoSolicitacao());
        manager.persist(proposta);
        logger.info("Adicionado status de Sem Retrição a proposta");
    }

    private AnalisePropostaResponseExterno buscaResultadoAnaliseProposta(AnalisePropostaRequest request) {
        return analiseClient.resultado(request);
    }
}
