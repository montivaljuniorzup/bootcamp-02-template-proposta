package br.com.zup.proposta.proposta;

import java.math.BigDecimal;

public class PropostaResponse {

    private Long id;

    private String documento;

    private String email;

    private String endereco;

    private BigDecimal salario;

    @Deprecated
    public PropostaResponse() {
    }

    public PropostaResponse(Long id, String documento, String email, String endereco, BigDecimal salario) {
        this.id = id;
        this.documento = documento;
        this.email = email;
        this.endereco = endereco;
        this.salario = salario;
    }

    public PropostaResponse(Proposta proposta) {
        this.id = proposta.getId();
        this.documento = proposta.getDocumento();
        this.email = proposta.getEmail();
        this.endereco = proposta.getEndereco();
        this.salario = proposta.getSalario();
    }


    public Long getId() {
        return id;
    }

    public String getDocumento() {
        return documento;
    }

    public String getEmail() {
        return email;
    }

    public String getEndereco() {
        return endereco;
    }

    public BigDecimal getSalario() {
        return salario;
    }
}
