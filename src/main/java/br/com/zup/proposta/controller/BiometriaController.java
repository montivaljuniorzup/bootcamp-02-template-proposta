package br.com.zup.proposta.controller;

import br.com.zup.proposta.dto.request.BiometriaRequest;
import br.com.zup.proposta.model.Biometria;
import br.com.zup.proposta.model.Cartao;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/v1/biometrias")
public class BiometriaController {

    private EntityManager manager;

    public BiometriaController(EntityManager manager) {
        this.manager = manager;
    }

    @PostMapping
    @Transactional
    public ResponseEntity criaBiometria(@RequestParam("id")String idCartao, @RequestBody BiometriaRequest biometriaRequest, UriComponentsBuilder builder){
        Cartao cartao = manager.find(Cartao.class, idCartao);
        if(Optional.ofNullable(cartao).isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Impossível cadastrar biometria, dados incosistentes");
            }
        Biometria biometria = biometriaRequest.toModel();
        cartao.adcionaNovaBiometria(biometria);
            manager.persist(cartao);
        URI uri = builder.path("/v1/biometrias/{id}").buildAndExpand(biometria.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

}
