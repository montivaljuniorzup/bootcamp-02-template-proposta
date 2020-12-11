package br.com.zup.proposta.controller;

import br.com.zup.proposta.dto.externo.ResultadoCarteira;
import br.com.zup.proposta.dto.externo.SolicitacaoInclusaoCarteira;
import br.com.zup.proposta.dto.response.CarteiraDigitalResponse;
import br.com.zup.proposta.feign.CartaoClient;
import br.com.zup.proposta.model.Cartao;
import br.com.zup.proposta.model.CarteiraDigital;
import feign.FeignException;
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

@RestController
@RequestMapping("/cartoes/carteira")
public class CarteiraDigitalController {

    private static Logger logger = LoggerFactory.getLogger(CarteiraDigitalController.class);
    private CartaoClient cartaoClient;
    private EntityManager manager;
    private Tracer tracer;

    public CarteiraDigitalController(CartaoClient cartaoClient, EntityManager manager, Tracer tracer) {
        this.cartaoClient = cartaoClient;
        this.manager = manager;
        this.tracer = tracer;
    }

    @PostMapping
    @Transactional
    public ResponseEntity cadastraCarteiraDigital(@RequestParam(value = "id", required = true) String id,
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
            logger.info("id {} e descricao {}", solicitacao.getEmail(), solicitacao.getCarteira());
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

    @GetMapping("/{id}")
    public ResponseEntity buscaCarteiraDigital(@PathVariable("id") String id) {
        CarteiraDigital carteiraDigital = manager.find(CarteiraDigital.class, id);

        return ResponseEntity.ok(new CarteiraDigitalResponse(carteiraDigital));
    }
}
