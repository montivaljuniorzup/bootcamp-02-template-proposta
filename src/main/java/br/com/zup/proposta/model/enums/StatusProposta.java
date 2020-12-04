package br.com.zup.proposta.model.enums;

import br.com.zup.proposta.compartilhado.exception.ApiErrorException;
import org.springframework.http.HttpStatus;

public enum StatusProposta {

    ELEGIVEL("SEM_RESTRICAO"), NAO_ELEGIVEL("COM_RESTRICAO");

    private String descricao;

    StatusProposta(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static StatusProposta toEnum(String estadoAnalise) {
        if(estadoAnalise == null){
            return null;
        }
        for (StatusProposta e : StatusProposta.values()) {
            if (estadoAnalise.equals(e.getDescricao())) {
                return e;
            }
        }
       throw new ApiErrorException(HttpStatus.BAD_REQUEST, "Tipo de descrição inválido");
    }
}
