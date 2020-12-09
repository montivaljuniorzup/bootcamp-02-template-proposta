package br.com.zup.proposta.model;

import br.com.zup.proposta.compartilhado.exception.ApiErrorException;
import org.springframework.http.HttpStatus;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Bloqueio {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull
    private LocalDateTime instante;

    @NotBlank
    private String ipClienteSolicitante;

    @NotBlank
    private String userAgentClienteSolicitante;

    public Bloqueio(String ipClienteSolicitante, String userAgentClienteSolicitante) {
        if(ipClienteSolicitante.isBlank() || userAgentClienteSolicitante.isBlank()){
            throw new ApiErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Não foi possível bloquear o cartão, dados de requisição inválidos");
        }
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
