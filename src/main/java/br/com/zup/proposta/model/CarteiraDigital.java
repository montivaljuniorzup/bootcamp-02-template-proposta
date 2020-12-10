package br.com.zup.proposta.model;

import br.com.zup.proposta.model.enums.TipoCarteira;

import javax.persistence.*;

@Entity
public class CarteiraDigital {

    /**
     * O Id é fornecido pela api externa
     */
    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
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
