package br.com.zup.proposta.feign;

import br.com.zup.proposta.dto.externo.CartaoPropostaResponseExterno;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Component
@FeignClient(url = "http://localhost:8888", name = "contas")
public interface CartaoClient {

    @GetMapping(value = "/api/cartoes")
    CartaoPropostaResponseExterno buscaCartaoPorId(@RequestParam("idProposta") UUID id);

}