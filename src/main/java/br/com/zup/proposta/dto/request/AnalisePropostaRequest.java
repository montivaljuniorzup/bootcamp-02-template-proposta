package br.com.zup.proposta.dto.request;

import br.com.zup.proposta.model.Proposta;

import javax.validation.constraints.NotBlank;

public class AnalisePropostaRequest {

    @NotBlank
    private String documento;

    @NotBlank
    private String nome;

    @NotBlank
    private String idProposta;

    public AnalisePropostaRequest(String documento, String nome, String idProposta) {
        this.documento = documento;
        this.nome = nome;
        this.idProposta = idProposta;
    }

    public AnalisePropostaRequest(Proposta proposta) {
        this.documento = proposta.getDocumento();
        this.nome = proposta.getNome();
        this.idProposta = String.valueOf(proposta.getId());
    }

    public String getDocumento() {
        return documento;
    }

    public String getNome() {
        return nome;
    }

    public String getIdProposta() {
        return idProposta;
    }
}
