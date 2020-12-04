package br.com.zup.proposta.cartoes;

import br.com.zup.proposta.compartilhado.exception.ApiErrorException;
import br.com.zup.proposta.proposta.Proposta;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Optional;

public class CartaoRecebidoDTO {

    private String id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime emitidoEm;

    private String titular;

    private String idProposta;

    private int limite;

    public Cartao toModel(EntityManager manager) {
        Proposta proposta = manager.find(Proposta.class, idProposta);
        if(Optional.ofNullable(proposta).isEmpty()){
            throw new ApiErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Dados de requisição inconsistentes");
        }
       return new Cartao(this.id,this.emitidoEm,this.titular,this.limite);
    }

    public CartaoRecebidoDTO(String id, LocalDateTime emitidoEm, String titular, String idProposta, int limite) {
        this.id = id;
        this.emitidoEm = emitidoEm;
        this.titular = titular;
        this.idProposta = idProposta;
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

    public String getIdProposta() {
        return idProposta;
    }


}
