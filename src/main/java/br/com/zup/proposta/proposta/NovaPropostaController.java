package br.com.zup.proposta.proposta;

import br.com.zup.proposta.analise.AnaliseClient;
import br.com.zup.proposta.compartilhado.exception.ApiErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/propostas")
public class NovaPropostaController {

    Logger logger = LoggerFactory.getLogger(NovaPropostaController.class);
    @Autowired
    private AnaliseClient feign;

    private final EntityManager manager;

    public NovaPropostaController(EntityManager manager) {
        this.manager = manager;
    }

    @PostMapping
    @Transactional
    public ResponseEntity criaNovaProposta(@Valid @RequestBody NovaPropostaRequest novaPropostaRequest, UriComponentsBuilder builder) {
        List resultList = manager
                .createQuery("Select p from Proposta p where p.documento =:documento")
                .setParameter("documento", novaPropostaRequest.getDocumento()).getResultList();
        if (resultList.size() > 0) {
            logger.error("Proposta documento={} já existe em nosso banco!", novaPropostaRequest.getDocumento());
            throw new ApiErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Dados inconsistentes, impossível proseguir o processamento");
        }
        Proposta proposta = novaPropostaRequest.toModel();
        manager.persist(proposta);

        URI uri = builder.path("/propostas/{id}").buildAndExpand(proposta.getId()).toUri();
        logger.info("Proposta documento={} salário={} criada com sucesso!", proposta.getDocumento(), proposta.getSalario());
        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/{id}")
    @Transactional
    public ResponseEntity buscaPropostaPeloId(@PathVariable("id") Long id) {
        Proposta proposta = manager.find(Proposta.class, id);
        if (Optional.ofNullable(proposta).isEmpty()) {
            logger.error("Proposta id={} não encontrada", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        logger.info("Proposta id={} encontrada com sucesso", id);
        return ResponseEntity.ok(new PropostaResponse(proposta));
    }
}
