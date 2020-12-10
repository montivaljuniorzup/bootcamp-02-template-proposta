package br.com.zup.proposta.feign;

import br.com.zup.proposta.dto.externo.CartaoPropostaResponseExterno;
import br.com.zup.proposta.dto.externo.SolicitacaoBloqueio;
import br.com.zup.proposta.dto.externo.ResultadoAvisoViagem;
import br.com.zup.proposta.dto.externo.StatusCartaoResponseExterno;
import br.com.zup.proposta.dto.request.AvisoViagemRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@Component
@FeignClient(url = "${proposta.cartao.host}", name = "contas")
public interface CartaoClient {

    @GetMapping(value = "/api/cartoes")
    CartaoPropostaResponseExterno buscaCartaoPorId(@RequestParam("idProposta") String id);

    @PostMapping(value = "/api/cartoes/{id}/bloqueios")
    StatusCartaoResponseExterno bloqueiaCartao(@PathVariable("id") String id, @RequestBody SolicitacaoBloqueio solicitacaoBloqueio);

    @PostMapping(value = "/api/cartoes/{id}/avisos")
    ResultadoAvisoViagem avisaViagem(@PathVariable("id") String id, @RequestBody AvisoViagemRequest avisoViagemRequest);

}