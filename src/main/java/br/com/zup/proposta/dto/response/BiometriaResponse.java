package br.com.zup.proposta.dto.response;

import br.com.zup.proposta.model.Biometria;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class BiometriaResponse {

    private LocalDateTime dataCadastro;

    private String fingerPrint;

    @Deprecated
    public BiometriaResponse() {
    }

    public BiometriaResponse (Biometria biometria){
        this.dataCadastro = biometria.getDataCadastro();
        this.fingerPrint = biometria.getFingerPrint();
    }

    public BiometriaResponse(@NotNull String fingerPrint) {
        this.dataCadastro = LocalDateTime.now();
        this.fingerPrint = fingerPrint;
    }

    public String getFingerPrint() {
        return fingerPrint;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }
}
