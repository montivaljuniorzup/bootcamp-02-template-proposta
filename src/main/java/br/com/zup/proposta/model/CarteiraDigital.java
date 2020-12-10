package br.com.zup.proposta.model;

import br.com.zup.proposta.model.enums.TipoCarteira;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Entity
public class CarteiraDigital {

    //Enviado de serviço externo
    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    private TipoCarteira carteira;

    @Deprecated
    public CarteiraDigital() {
    }

    public CarteiraDigital(String id, TipoCarteira carteira) {
        this.id = id;
        this.carteira = carteira;
    }

    public String getId() {
        return id;
    }

    public TipoCarteira getCarteira() {
        return carteira;
    }
}
