package br.com.zup.proposta.cartoes;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(url = "${proposta.cartao.host}", name = "contas")
public interface CartaoClient {

    @GetMapping("/api/cartoes/{id}")
    ResponseEntity<Cartao> buscaCartaoPorId(@PathVariable("id") String id);

    @GetMapping("/api/cartoes")
    ResponseEntity<Cartao> buscaCartao(@RequestParam("id") String id);

    @PostMapping("/api/cartoes")
    ResponseEntity<Cartao> criaoOvoCartao(NovoCartao cartao);

}