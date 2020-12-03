package br.com.zup.proposta.analise;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@Component
@FeignClient(url = "${proposta.analise.host}", name = "analise")
public interface AnaliseClient {

    @PostMapping(value = "/api/solicitacao")
    ResultadoAnaliseProposta resultado(@RequestBody SolicitacaoAnalisePropostaRequest request);
}
