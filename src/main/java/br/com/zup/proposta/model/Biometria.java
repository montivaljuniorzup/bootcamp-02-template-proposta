package br.com.zup.proposta.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Biometria {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime dataCadastro;

    @NotNull
    @Column(nullable = false)
    private String fingerPrint;

    @Deprecated
    public Biometria() {
    }

    public Biometria(@NotNull String fingerPrint) {
        this.dataCadastro = LocalDateTime.now();
        this.fingerPrint = fingerPrint;
    }

    public UUID getId() {
        return id;
    }

    public String getFingerPrint() {
        return fingerPrint;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }
}
