package br.com.zup.proposta.dto.request;

import br.com.zup.proposta.compartilhado.validation.CPFouCNPJ;
import br.com.zup.proposta.compartilhado.validation.UniqueValue;
import br.com.zup.proposta.model.Proposta;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

public class NovaPropostaRequest {


    @NotBlank
    private String nome;

    @NotBlank
    @CPFouCNPJ(message = "Por favor digite um CPF ou um CNPJ válido")
    @UniqueValue(classe = Proposta.class, atributo = "documento", message = "Ja possui registro no nosso banco")
    private String documento;

    @Email
    @UniqueValue(classe = Proposta.class, atributo = "email", message = "Ja possui registro no nosso banco")
    private String email;

    @NotBlank
    private String endereco;

    @Positive
    @NotNull
    private BigDecimal salario;


    public NovaPropostaRequest(@NotBlank String nome, @NotBlank String documento, @Email String email, @NotBlank String endereco, @Positive @NotNull BigDecimal salario) {
        this.nome = nome;
        this.documento = documento;
        this.email = email;
        this.endereco = endereco;
        this.salario = salario;
    }

    public Proposta toModel() {
        return new Proposta(this.nome, this.documento, this.email, this.endereco, this.salario);
    }

    public String getNome() {
        return nome;
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
