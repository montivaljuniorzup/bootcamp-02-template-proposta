package br.com.zup.proposta.dto.externo;

import br.com.zup.proposta.model.enums.StatusAvisoViagem;

public class ResultadoAvisoViagem {

    private StatusAvisoViagem statusAvisoViagem;

    @Deprecated
    public ResultadoAvisoViagem() {
    }

    public ResultadoAvisoViagem(StatusAvisoViagem statusAvisoViagem) {
        this.statusAvisoViagem = statusAvisoViagem;
    }

    public StatusAvisoViagem getResultado() {
        return statusAvisoViagem;
    }

    public boolean estaCriado() {
        return StatusAvisoViagem.CRIADO.equals(this.statusAvisoViagem);
    }
}
