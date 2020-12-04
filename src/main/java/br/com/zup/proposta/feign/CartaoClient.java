package br.com.zup.proposta.feign;

import br.com.zup.proposta.dto.externo.CartaoPropostaResponseExterno;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(url = "${proposta.cartao.host}", name = "contas")
public interface CartaoClient {

    @GetMapping("/api/cartoes/{id}")
    ResponseEntity<CartaoPropostaResponseExterno> buscaCartaoPorId(@PathVariable("id") String id);

    @GetMapping("/api/cartoes")
    ResponseEntity<CartaoPropostaResponseExterno> buscaCartao(@RequestParam("id") String id);

}