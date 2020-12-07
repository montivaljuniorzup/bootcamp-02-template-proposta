package br.com.zup.proposta.dto.response;

import br.com.zup.proposta.model.Cartao;
import br.com.zup.proposta.model.Proposta;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.UUID;

public class PropostaResponse {

    private UUID id;

    private String documento;

    private String email;

    private String endereco;

    private BigDecimal salario;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String estado;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Cartao cartao;

    @Deprecated
    public PropostaResponse() {
    }

    public PropostaResponse(UUID id, String documento, String email, String endereco, BigDecimal salario) {
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
        if(proposta.temEstado()){
        this.estado = proposta.getStatus();
        this.cartao = proposta.getCartao();
        }
    }


    public UUID getId() {
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

    public String getEstado() {
        return estado;
    }

    public Cartao getCartao() {
        return cartao;
    }
}
