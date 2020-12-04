package br.com.zup.proposta.cartoes;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Cartao {

    @Id
    private String id;

    private LocalDateTime emitidoEm; // unique Itens

    private String titular;
//    private List<Bloqueio> bloqueios; // unique Itens
//    private List<AvisoViagem> avisos;    // unique Itens
//    private List<CarteiraDigital> carteiras; // unique Itens
//    private List<Parcela> parcelas;  // unique Itens
    private int limite;
//    private Renegociacao renegocicao;
//    private Vencimento vencimeanto;
//    @OneToOne
//    @MapsId
//    @JoinColumn(name = "proposta_id")
//    private Proposta proposta;

@Deprecated
    public Cartao() {
    }

    public Cartao(String id,
                  LocalDateTime emitidoEm,
                  String titular,
                  int limite) {
        this.id = id;
        this.emitidoEm = emitidoEm;
        this.titular = titular;
        this.limite = limite;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getEmitidoEm() {
        return emitidoEm;
    }

    public String getTitular() {
        return titular;
    }

    public int getLimite() {
        return limite;
    }
}
