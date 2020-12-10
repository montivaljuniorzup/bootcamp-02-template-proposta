package br.com.zup.proposta.dto.request;

import br.com.zup.proposta.model.AvisoViagem;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class AvisoViagemRequest {

    @NotBlank
    private String destino;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate terminoViagem;

    private String ipCliente;

    private String userAgent;

    public AvisoViagem toModel(HttpServletRequest request) {
        this.ipCliente = request.getRemoteAddr();
        this.userAgent = request.getHeader("User-Agent");
        return new AvisoViagem(this.destino, this.terminoViagem, this.ipCliente, this.userAgent);
    }

    @Deprecated
    public AvisoViagemRequest() {
    }

    public AvisoViagemRequest(@NotBlank String destino,
                              @NotNull LocalDate terminoViagem) {
        this.destino = destino;
        this.terminoViagem = terminoViagem;

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
}
