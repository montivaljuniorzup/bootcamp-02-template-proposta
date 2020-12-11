package br.com.zup.proposta.controller;

import br.com.zup.proposta.dto.request.BiometriaRequest;
import br.com.zup.proposta.dto.response.BiometriaResponse;
import br.com.zup.proposta.model.Biometria;
import br.com.zup.proposta.model.Cartao;
import io.opentracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/cartoes")
public class BiometriaController {

    private static Logger logger = LoggerFactory.getLogger(BiometriaController.class);
    private EntityManager manager;
    private Tracer tracer;

    public BiometriaController(EntityManager manager, Tracer tracer) {
        this.manager = manager;
        this.tracer = tracer;
    }

    //    @InitBinder
//    public void init(WebDataBinder binder) {
//        binder.addValidators(new FingerPrintValidator());
//    }


    @PostMapping("/biometria")
    @Transactional
    public ResponseEntity criaBiometria(@RequestParam("id") String idCartao,
                                        @RequestBody @Valid BiometriaRequest biometriaRequest,
                                        UriComponentsBuilder builder) {
        Cartao cartao = manager.find(Cartao.class, idCartao);
        if (Optional.ofNullable(cartao).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Impossível cadastrar biometria, dados inconsistentes");
        }
        Biometria biometria = biometriaRequest.toModel();
        cartao.adcionaNovaBiometria(biometria);
        manager.persist(cartao);
        URI uri = builder.path("/cartoes/{id}/biometria").buildAndExpand(biometria.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/{id}/biometria")
    @Transactional
    public ResponseEntity buscaBiometriaPorId(@PathVariable("id") UUID id) {
        Biometria biometria = manager.find(Biometria.class, id);
        if (Optional.ofNullable(biometria).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Biometria não encontrada");
        }
        return ResponseEntity.ok(new BiometriaResponse(biometria));
    }
}
