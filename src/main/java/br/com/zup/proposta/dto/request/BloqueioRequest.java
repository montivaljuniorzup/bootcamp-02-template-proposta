package br.com.zup.proposta.dto.request;

import br.com.zup.proposta.model.Bloqueio;

public class BloqueioRequest {


    private String ipClienteSolicitante;

    private String userAgentClienteSolicitante;

    public BloqueioRequest(String ipClienteSolicitante, String userAgentClienteSolicitante) {
        this.ipClienteSolicitante = ipClienteSolicitante;
        this.userAgentClienteSolicitante = userAgentClienteSolicitante;
    }

    public Bloqueio toModel(){
    return new Bloqueio(this.ipClienteSolicitante,this.userAgentClienteSolicitante);
    }

    @Deprecated
    public BloqueioRequest() {
    }

    public String getIpClienteSolicitante() {
        return ipClienteSolicitante;
    }

    public String getUserAgentClienteSolicitante() {
        return userAgentClienteSolicitante;
    }
}
