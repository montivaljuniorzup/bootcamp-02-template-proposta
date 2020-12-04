package br.com.zup.proposta.feign;

import br.com.zup.proposta.dto.externo.AnalisePropostaResponseExterno;
import br.com.zup.proposta.dto.request.AnalisePropostaRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@Component
@FeignClient(url = "${proposta.analise.host}", name = "analise")
public interface AnaliseClient {

    @PostMapping(value = "/api/solicitacao")
    AnalisePropostaResponseExterno resultado(@RequestBody AnalisePropostaRequest request);
}
