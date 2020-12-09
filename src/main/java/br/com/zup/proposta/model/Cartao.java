package br.com.zup.proposta.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Cartao {

    /**
     * O Id é fornecido pela api externa
     */
    @Id
    private String id;

    private LocalDateTime emitidoEm;

    @NotBlank
    private String titular;

    @NotNull
    private int limite;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Biometria> biometrias;

    @OneToOne(cascade = CascadeType.ALL)
    private Bloqueio bloqueio;

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

    public void bloquerCartao(Bloqueio bloqueio) {
        this.bloqueio = bloqueio;
    }

    public boolean cartaoEstaBloqueado(){
        return this.bloqueio != null;
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

    public Bloqueio getBloqueio() {
        return bloqueio;
    }

    public void setBiometrias(List<Biometria> biometrias) {
        this.biometrias = biometrias;
    }

    public void adcionaNovaBiometria(Biometria biometria) {
    this.biometrias.add(biometria);
    }
}
