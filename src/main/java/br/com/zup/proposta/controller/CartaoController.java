package br.com.zup.proposta.controller;

import br.com.zup.proposta.dto.externo.ResultadoCarteira;
import br.com.zup.proposta.dto.externo.SolicitacaoBloqueio;
import br.com.zup.proposta.dto.externo.SolicitacaoInclusaoCarteira;
import br.com.zup.proposta.dto.externo.StatusCartaoResponseExterno;
import br.com.zup.proposta.dto.request.AvisoViagemRequest;
import br.com.zup.proposta.dto.request.BiometriaRequest;
import br.com.zup.proposta.dto.response.BiometriaResponse;
import br.com.zup.proposta.dto.response.CarteiraDigitalResponse;
import br.com.zup.proposta.feign.CartaoClient;
import br.com.zup.proposta.model.*;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity comunicaViagem(@RequestParam(value = "id", required = true) String id, @RequestBody AvisoViagemRequest avisoViagemRequest, HttpServletRequest client) {
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

    @PostMapping("/carteira")
    @Transactional
    public ResponseEntity deveCadastrarCarteira(@RequestParam(value = "id", required = true) String id,
                                                @Valid @RequestBody SolicitacaoInclusaoCarteira solicitacao,
                                                UriComponentsBuilder builder) {
        Cartao cartao = manager.find(Cartao.class, id);
        if (cartao == null) {
            logger.error("Não foi encontrado cartao {} no banco de dados", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Impossível encontrar cartão, dados inconsistentes");
        }
        if (cartao.temCarteiraDigitalAssociada()) {
            logger.error("Já existe uma conta digital vinculada ao cartao {} no banco de dados", id);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Impossível encontrar cartão, dados inconsistentes");
        }
        try {
            ResultadoCarteira resultadoCarteira = cartaoClient.solicitaInclusaoCarteira(id, solicitacao);
            if (!resultadoCarteira.estaAssociada()) {
            logger.error("Serviço externo não associou a conta digital ao cartao {} no banco de dados", id);
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Impossível cadastrar carteira, tente novamente mais tarde");
            }

            CarteiraDigital carteiraDigital = resultadoCarteira.toModel(solicitacao.getCarteira());
            logger.info("id {} e descricao {}",solicitacao.getEmail(), solicitacao.getCarteira() );
            cartao.setCarteiraDigital(carteiraDigital);
            manager.persist(cartao);
            URI uri = builder.path("/cartoes/carteira/{id}").buildAndExpand(carteiraDigital.getId()).toUri();
            return ResponseEntity.created(uri).build();

        } catch (FeignException.FeignServerException e) {
            logger.error("Não foi possível cadastrara carteira Erro {}, {}", e.status(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Impossível cadastrar carteira no momento, tente novamente mais tarde");

        } catch (FeignException.FeignClientException e) {
            logger.error("Impossível cadastrar carteira. Erro {}, {}", e.status(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Impossível cadastrar carteira, dados inconsistentes");
        }
    }

    @GetMapping("/carteira/{id}")
    public ResponseEntity buscaCarteira(@PathVariable("id") String id){
        CarteiraDigital carteiraDigital = manager.find(CarteiraDigital.class, id);

        return ResponseEntity.ok(new CarteiraDigitalResponse(carteiraDigital));
    }
}
