package br.com.zup.proposta.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class AvisoViagem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotBlank
    private String destino;

    @NotNull
    private LocalDate terminoViagem;

    @NotBlank
    private String ipCliente;

    @NotBlank
    private String userAgent;

    private LocalDateTime instanteCadastro;

    @Deprecated
    public AvisoViagem() {
    }

    public AvisoViagem(@NotBlank String destino, @NotNull LocalDate terminoViagem, @NotBlank String ipCliente, @NotBlank String userAgent) {
        this.instanteCadastro = LocalDateTime.now();
        this.destino = destino;
        this.terminoViagem = terminoViagem;
        this.ipCliente = ipCliente;
        this.userAgent = userAgent;
    }

    public String getDestino() {
        return destino;
    }

    public LocalDate getTerminoViagem() {
        return terminoViagem;
    }

    public String getIpCliente() {
        return ipCliente;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public LocalDateTime getInstanteCadastro() {
        return instanteCadastro;
    }

    public UUID getId() {
        return id;
    }
}
