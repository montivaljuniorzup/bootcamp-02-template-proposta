package br.com.zup.proposta.model.enums;

import br.com.zup.proposta.compartilhado.exception.ApiErrorException;
import org.springframework.http.HttpStatus;

public enum StatusCartao {
    DESBLOQUEADO("Desbloqueado"), BLOQUEADO("Bloqueado");

    private String descricao;

    StatusCartao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static StatusCartao toEnum(String descricao) {
        if (descricao == null) {
            return null;
        }
        for (StatusCartao s : values()) {
            if (descricao.equals(s.getDescricao())) {
                return s;
            }
        }
        throw new ApiErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Tipo de descrição inválido");
    }
}
