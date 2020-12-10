package br.com.zup.proposta.dto.response;

import br.com.zup.proposta.model.CarteiraDigital;
import br.com.zup.proposta.model.enums.TipoCarteira;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Entity
public class CarteiraDigitalResponse {

    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    private TipoCarteira carteira;

    public CarteiraDigitalResponse(CarteiraDigital carteiraDigital) {
        this.id = carteiraDigital.getId();
        this.carteira = carteiraDigital.getCarteira();
    }

    @Deprecated
    public CarteiraDigitalResponse() {
    }

    public String getId() {
        return id;
    }

    public TipoCarteira getCarteira() {
        return carteira;
    }
}
