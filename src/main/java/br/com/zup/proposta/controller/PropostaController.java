package br.com.zup.proposta.controller;

import br.com.zup.proposta.dto.request.NovaPropostaRequest;
import br.com.zup.proposta.dto.response.PropostaResponse;
import br.com.zup.proposta.dto.response.StatusPropostaResponse;
import br.com.zup.proposta.feign.AnaliseClient;
import br.com.zup.proposta.dto.externo.AnalisePropostaResponseExterno;
import br.com.zup.proposta.dto.request.AnalisePropostaRequest;
import br.com.zup.proposta.compartilhado.exception.ApiErrorException;
import br.com.zup.proposta.model.Proposta;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping("/v1/propostas")
public class PropostaController {

    private static Logger logger = LoggerFactory.getLogger(PropostaController.class);

    private AnaliseClient analiseClient;
    private final EntityManager manager;
    private TransactionTemplate txTemplate;

    public PropostaController(AnaliseClient analiseClient, EntityManager manager, TransactionTemplate txTemplate) {
        this.analiseClient = analiseClient;
        this.manager = manager;
        this.txTemplate = txTemplate;
    }

    @PostMapping
    @Transactional
    public ResponseEntity criaNovaProposta(@Valid @RequestBody NovaPropostaRequest novaPropostaRequest, UriComponentsBuilder builder) {

        boolean naoExisteDocumentoNoBanco = manager
                .createQuery("Select p from Proposta p where p.documento =:documento")
                .setParameter("documento", novaPropostaRequest.getDocumento())
                .getResultList()
                .isEmpty();

        if (naoExisteDocumentoNoBanco) {
            Proposta proposta = novaPropostaRequest.toModel();
            manager.persist(proposta);

            AnalisePropostaRequest request = new AnalisePropostaRequest(proposta);
            consultaDadosSolicitante(proposta, request);

            URI uri = builder.path("/v1/propostas/{id}").buildAndExpand(proposta.getId()).toUri();

                logger.info("Proposta documento={} salário={} criada com sucesso!", proposta.getDocumento(), proposta.getSalario());
                return ResponseEntity.created(uri).build();
        }

            logger.error("Proposta para o documento={} já existe em nosso banco!", novaPropostaRequest.getDocumento());
            throw new ApiErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Dados inconsistentes, impossível proseguir o processamento");
    }

    @GetMapping("/{id}")
    @Transactional
    public ResponseEntity buscaPropostaPeloId(@PathVariable("id") UUID id) {
        Proposta proposta = manager.find(Proposta.class, id);
        if (Optional.ofNullable(proposta).isEmpty()) {
            logger.error("Proposta id={} não encontrada", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        logger.info("Proposta id={} encontrada com sucesso", id);
        return ResponseEntity.ok(new PropostaResponse(proposta));
    }

    @GetMapping("/status/{id}")
    @Transactional
    public ResponseEntity buscaStatusPropostaPeloId(@PathVariable("id") UUID id) {
        Proposta proposta = manager.find(Proposta.class, id);
        if (Optional.ofNullable(proposta).isEmpty()) {
            logger.error("Proposta id={} não encontrada", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        logger.info("Proposta id={} encontrada com sucesso", id);
        return ResponseEntity.ok(new StatusPropostaResponse(proposta));
    }

    private void consultaDadosSolicitante(Proposta proposta, AnalisePropostaRequest request) {
        try {
            AnalisePropostaResponseExterno resultado = analiseClient.resultado(request);

            proposta.setStatus(resultado.getResultadoSolicitacao());
            manager.persist(proposta);
            logger.info("Solicitação buscada com sucesso");

        } catch (FeignException.UnprocessableEntity e) {
            if (e.getLocalizedMessage().contains("COM_RESTRICAO")) {
                proposta.setStatus("COM_RESTRICAO");
                manager.persist(proposta);
            }
        }catch (FeignException e ) {
            logger.error("Erro " + e.getMessage() + " ao buscar solicitação");
            throw new ApiErrorException(HttpStatus.valueOf(e.status()), e.getLocalizedMessage());
        }
    }
}
