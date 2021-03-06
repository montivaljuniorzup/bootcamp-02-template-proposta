package br.com.zup.proposta.controller;

import br.com.zup.proposta.dto.externo.SolicitacaoBloqueio;
import br.com.zup.proposta.dto.externo.StatusCartaoResponseExterno;
import br.com.zup.proposta.dto.request.AvisoViagemRequest;
import br.com.zup.proposta.feign.CartaoClient;
import br.com.zup.proposta.model.AvisoViagem;
import br.com.zup.proposta.model.Bloqueio;
import br.com.zup.proposta.model.Cartao;
import feign.FeignException;
import io.opentracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;

@RestController
@RequestMapping("/cartoes")
public class CartaoController {

    private static Logger logger = LoggerFactory.getLogger(CartaoController.class);
    private CartaoClient cartaoClient;
    private EntityManager manager;
    private Tracer tracer;

    public CartaoController(CartaoClient cartaoClient, EntityManager manager, Tracer tracer) {
        this.cartaoClient = cartaoClient;
        this.manager = manager;
        this.tracer = tracer;
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
            logger.error("Não foi possível bloquear o cartao. Erro {}, {}", e.status(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Impossível bloquear cartão, dados inconsistentes");
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/viagem")
    @Transactional
    public ResponseEntity comunicaViagem(@RequestParam(value = "id", required = true) String id,
                                         @RequestBody @Valid AvisoViagemRequest avisoViagemRequest,
                                         HttpServletRequest client) {
        Cartao cartao = manager.find(Cartao.class, id);
        if (cartao == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Impossível cadastrar viagem, dados inconsistentes");
        }
        try {
            br.com.zup.proposta.dto.externo.ResultadoAvisoViagem resultadoAvisoViagem = cartaoClient.avisaViagem(id, avisoViagemRequest);
            if (resultadoAvisoViagem.estaCriado()) {
                AvisoViagem avisoViagem = avisoViagemRequest.toModel(client);
                manager.persist(avisoViagem);
                logger.info("Aviso gerado. aviso {}", avisoViagem.getId());
                cartao.adcionaNovaViagem(avisoViagem);
                manager.persist(cartao);
                logger.info("Cartao atualizado");
            }
        } catch (FeignException.FeignServerException e) {
            logger.error("Não foi possível cadastrar aviso viagem. Erro {}, {}", e.status(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Impossível cadastrar viagem no momento, tente novamente mais tarde");
        } catch (FeignException.FeignClientException e) {
            logger.error("Impossível cadastrar viagem. Erro {}, {}", e.status(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Impossível cadastrar viagem, dados inconsistentes");
        }
        return ResponseEntity.ok().build();
    }

}
