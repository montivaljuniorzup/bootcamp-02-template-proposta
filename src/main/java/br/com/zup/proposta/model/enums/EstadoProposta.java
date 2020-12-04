package br.com.zup.proposta.model.enums;

import br.com.zup.proposta.compartilhado.exception.ApiErrorException;
import org.springframework.http.HttpStatus;

public enum EstadoProposta {

    ELEGIVEL("SEM_RESTRICAO"), NAO_ELEGIVEL("COM_RESTRICAO");

    private String descricao;

    EstadoProposta(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static EstadoProposta toEnum(String estadoAnalise) {
        if(estadoAnalise == null){
            return null;
        }
        for (EstadoProposta e : EstadoProposta.values()) {
            if (estadoAnalise.equals(e.getDescricao())) {
                return e;
            }
        }
       throw new ApiErrorException(HttpStatus.BAD_REQUEST, "Tipo de descrição inválido");
    }
}
