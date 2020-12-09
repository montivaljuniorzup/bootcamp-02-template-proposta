package br.com.zup.proposta.dto.externo;

public class SolicitacaoBloqueio {

    private String sistemaResponsavel;

    public SolicitacaoBloqueio(String sistemaResponsavel) {
        this.sistemaResponsavel = sistemaResponsavel;
    }

    public SolicitacaoBloqueio() {
    }

    public String getSistemaResponsavel() {
        return sistemaResponsavel;
    }
}
