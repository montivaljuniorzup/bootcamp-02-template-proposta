package br.com.zup.proposta.cartoes;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;

@RestController
@RequestMapping("/v1/cartoes")
public class CartaoController {

    private CartaoClient cartaoClient;
    private EntityManager manager;
    private TransactionTemplate txtemplate;

    public CartaoController(CartaoClient cartaoClient,
                            EntityManager manager,
                            TransactionTemplate txtemplate) {
        this.cartaoClient = cartaoClient;
        this.manager = manager;
        this.txtemplate = txtemplate;
    }

    @GetMapping("/{id}")
    public ResponseEntity buscaCartao(@PathVariable("id") String id){
        ResponseEntity<Cartao> cartao = cartaoClient.buscaCartao(id);

        return ResponseEntity.ok(cartao.getBody());
    }
}
