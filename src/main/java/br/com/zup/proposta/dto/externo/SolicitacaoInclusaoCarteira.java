package br.com.zup.proposta.dto.externo;

import javax.validation.constraints.NotBlank;

public class SolicitacaoInclusaoCarteira {

    @NotBlank
    private String email;

    private String carteira;

    @Deprecated
    public SolicitacaoInclusaoCarteira() {
    }

    public SolicitacaoInclusaoCarteira(@NotBlank String email, String carteira) {
        this.email = email;
        this.carteira = carteira;
    }

    public String getEmail() {
        return email;
    }

    public String getCarteira() {
        return carteira;
    }
}
