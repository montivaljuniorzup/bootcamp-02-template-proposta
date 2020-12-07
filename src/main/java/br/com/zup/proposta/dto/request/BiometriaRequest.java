package br.com.zup.proposta.dto.request;

import br.com.zup.proposta.model.Biometria;

import javax.validation.constraints.NotBlank;

public class BiometriaRequest {

    @NotBlank
    private String fingerPrint;

    @Deprecated
    public BiometriaRequest() {
    }

    public BiometriaRequest(String fingerPrint) {
        this.fingerPrint = fingerPrint;
    }


    public Biometria toModel(){
        return new Biometria(fingerPrint);
    }

    public String getFingerPrint() {
        return fingerPrint;
    }
}
