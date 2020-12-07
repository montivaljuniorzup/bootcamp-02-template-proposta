package br.com.zup.proposta.dto.externo;

import br.com.zup.proposta.model.Cartao;

import java.time.LocalDateTime;
import java.util.UUID;

public class CartaoPropostaResponseExterno {

    private String id;

    private LocalDateTime emitidoEm;

    private String titular;

    private int limite;

    @Deprecated
    public CartaoPropostaResponseExterno() {
    }

    public Cartao toModel(){
        return new Cartao(this.id,
                this.emitidoEm,
                this.titular,
        this.limite);
    }

    public CartaoPropostaResponseExterno(UUID id,
                                         LocalDateTime emitidoEm,
                                         String titular,
                                         int limite) {
        this.id = String.valueOf(this.id);
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
