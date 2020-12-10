package br.com.zup.proposta.model.enums;

import br.com.zup.proposta.compartilhado.exception.ApiErrorException;
import org.springframework.http.HttpStatus;

public enum TipoCarteira {
    SAMSUNGPAY("samsung pay"),PAYPAL("paypal");

    private String descrição;

    TipoCarteira(String descrição) {
        this.descrição = descrição;
    }

    public String getDescrição() {
        return descrição;
    }

    public static TipoCarteira toEnum(String descricao){
        if(descricao == null){
            throw new ApiErrorException(HttpStatus.BAD_REQUEST, "Tipo de descrição inválido");
        }
        for(TipoCarteira t : TipoCarteira.values()){
            if(descricao.equalsIgnoreCase(t.getDescrição())){
                return t;
            }
        }
        throw new ApiErrorException(HttpStatus.BAD_REQUEST, "Tipo de descrição inválido");
    }
}
