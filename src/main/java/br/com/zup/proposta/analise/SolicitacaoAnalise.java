package br.com.zup.proposta.analise;

import br.com.zup.proposta.proposta.Proposta;

public class SolicitacaoAnalise {

    private String documento;

    private String nome;

    private String idProposta;

    public SolicitacaoAnalise(String documento, String nome, String idProposta) {
        this.documento = documento;
        this.nome = nome;
        this.idProposta = idProposta;
    }

    public SolicitacaoAnalise(Proposta proposta){
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
