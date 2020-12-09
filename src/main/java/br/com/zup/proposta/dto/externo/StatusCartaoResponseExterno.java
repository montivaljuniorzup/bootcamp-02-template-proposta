package br.com.zup.proposta.dto.externo;

import javax.validation.constraints.NotBlank;

public class StatusCartaoResponseExterno {

    @NotBlank
    private String status;

    @Deprecated
    public StatusCartaoResponseExterno() {
    }

    public StatusCartaoResponseExterno(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
