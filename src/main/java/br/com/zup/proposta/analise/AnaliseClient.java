package br.com.zup.proposta.analise;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@Component
@FeignClient(name = "analise", url = "localhost:9999", path = "/api")
public interface AnaliseClient {

    @PostMapping(value = "/solicitacao")
    ResultadoAnalise resultado(@RequestBody SolicitacaoAnalise solicitacaoAnalise);
}
