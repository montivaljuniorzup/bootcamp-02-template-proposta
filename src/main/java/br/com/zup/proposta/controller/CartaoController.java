package br.com.zup.proposta.controller;

import br.com.zup.proposta.compartilhado.validation.FingerPrintValidator;
import br.com.zup.proposta.dto.externo.SolicitacaoBloqueio;
import br.com.zup.proposta.dto.externo.StatusCartaoResponseExterno;
import br.com.zup.proposta.dto.request.BiometriaRequest;
import br.com.zup.proposta.dto.response.BiometriaResponse;
import br.com.zup.proposta.feign.CartaoClient;
import br.com.zup.proposta.model.Biometria;
import br.com.zup.proposta.model.Bloqueio;
import br.com.zup.proposta.model.Cartao;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/cartoes")
public class CartaoController {

    private static Logger logger = LoggerFactory.getLogger(CartaoController.class);
    private CartaoClient cartaoClient;
    private EntityManager manager;

    public CartaoController(CartaoClient cartaoClient, EntityManager manager) {
        this.cartaoClient = cartaoClient;
        this.manager = manager;
    }

    @InitBinder
    public void init(WebDataBinder binder) {
        binder.addValidators(new FingerPrintValidator());
    }

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

    @PostMapping("/{id}/bloqueio")
    @Transactional
    public ResponseEntity bloqueiaCartao(@PathVariable("id") String id, HttpServletRequest client) {
        Cartao cartao = manager.find(Cartao.class, id);

        if (cartao == null) {
            logger.error("Não foi encontrado cartao {} no banco de dados", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Impossível bloquear cartão, dados inconsistentes");
        }
        if (cartao.cartaoEstaBloqueado()) {
            logger.error("O cartao {} ja está bloqueado", id);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Impossível bloquear cartão, dados inconsistentes");
        }
        String ipAddress = client.getRemoteAddr();
        String userAgentStr = client.getHeader("User-Agent");

        Bloqueio bloqueio = new Bloqueio(ipAddress, userAgentStr);
        try {
            StatusCartaoResponseExterno statusCartao = cartaoClient.bloqueiaCartao(id, new SolicitacaoBloqueio("Propostas"));
            cartao.bloquerCartao(bloqueio);
            manager.persist(bloqueio);
            logger.info("O cartao {} foi atualizado com sucesso", id);
        } catch (FeignException e) {
            logger.info("Não foi possível bloquear o cartao. Erro {}, {}", e.status(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Impossível bloquear cartão, dados inconsistentes");
        }

        return ResponseEntity.ok().build();
    }
}
