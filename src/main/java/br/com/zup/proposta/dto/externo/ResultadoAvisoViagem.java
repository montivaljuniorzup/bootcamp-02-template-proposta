package br.com.zup.proposta.dto.externo;

import br.com.zup.proposta.model.enums.Resultado;

public class ResultadoAvisoViagem {

    private Resultado resultado;

    @Deprecated
    public ResultadoAvisoViagem() {
    }

    public ResultadoAvisoViagem(Resultado resultado) {
        this.resultado = resultado;
    }

    public Resultado getResultado() {
        return resultado;
    }
}
