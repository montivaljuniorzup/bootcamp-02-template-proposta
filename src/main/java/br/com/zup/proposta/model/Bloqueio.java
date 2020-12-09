package br.com.zup.proposta.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Bloqueio {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private LocalDateTime instante;

    private String ipClienteSolicitante;

    private String userAgentClienteSolicitante;

    public Bloqueio(String ipClienteSolicitante, String userAgentClienteSolicitante) {
        this.instante = LocalDateTime.now();
        this.ipClienteSolicitante = ipClienteSolicitante;
        this.userAgentClienteSolicitante = userAgentClienteSolicitante;
    }

    @Deprecated
    public Bloqueio() {
    }

    public LocalDateTime getInstante() {
        return instante;
    }

    public String getIpClienteSolicitante() {
        return ipClienteSolicitante;
    }

    public String getUserAgentClienteSolicitante() {
        return userAgentClienteSolicitante;
    }
}
