package br.com.zup.proposta.dto.response;

import br.com.zup.proposta.model.Proposta;
import br.com.zup.proposta.model.enums.StatusProposta;

public class StatusPropostaResponse {

    private String status;

    public StatusPropostaResponse(StatusProposta status) {
        this.status = status.getDescricao();
    }

    public StatusPropostaResponse(Proposta proposta) {
        this.status = proposta.getStatus();
    }

    public String getStatus() {
        return status;
    }
}
