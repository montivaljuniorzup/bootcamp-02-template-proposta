package br.com.zup.proposta.dto.externo;

import br.com.zup.proposta.model.CarteiraDigital;
import br.com.zup.proposta.model.enums.StatusCarteira;
import br.com.zup.proposta.model.enums.TipoCarteira;

public class ResultadoCarteira {

    private StatusCarteira resultado;

    private String id;

    public CarteiraDigital toModel(String tipoCarteira) {
        TipoCarteira tipo = TipoCarteira.toEnum(tipoCarteira);
        return new CarteiraDigital(this.id, tipo);
    }

    @Deprecated
    public ResultadoCarteira() {
    }

    public ResultadoCarteira(StatusCarteira resultado, String id) {
        this.resultado = resultado;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public StatusCarteira getResultado() {
        return resultado;
    }

    public boolean estaAssociada() {
        return this.getResultado().equals(StatusCarteira.ASSOCIADA);
    }
}
