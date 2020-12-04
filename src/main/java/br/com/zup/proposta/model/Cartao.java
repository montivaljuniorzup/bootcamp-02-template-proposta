package br.com.zup.proposta.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Cartao {

    @Id
    private String id;

    private LocalDateTime emitidoEm;

    private String titular;

    private int limite;

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
