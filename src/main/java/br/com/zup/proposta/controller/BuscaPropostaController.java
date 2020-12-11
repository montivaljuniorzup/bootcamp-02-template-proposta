package br.com.zup.proposta.controller;

import br.com.zup.proposta.dto.response.PropostaResponse;
import br.com.zup.proposta.dto.response.StatusPropostaResponse;
import br.com.zup.proposta.feign.AnaliseClient;
import br.com.zup.proposta.model.Proposta;
import io.opentracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping("/propostas")
public class BuscaPropostaController {

    private static Logger logger = LoggerFactory.getLogger(BuscaPropostaController.class);
    private Tracer tracer;
    private AnaliseClient analiseClient;
    private final EntityManager manager;

    public BuscaPropostaController(Tracer tracer, AnaliseClient analiseClient, EntityManager manager) {
        this.tracer = tracer;
        this.analiseClient = analiseClient;
        this.manager = manager;
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

    @GetMapping("/{id}/status")
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
    
}
