package br.com.zup.proposta.model;

import br.com.zup.proposta.model.enums.EstadoProposta;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
public class Proposta {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotBlank
    @Column(nullable = false)
    private String nome;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String documento;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String endereco;

    @Positive
    @NotNull
    @Column(nullable = false)
    private BigDecimal salario;

    @Enumerated(EnumType.STRING)
    private EstadoProposta estado;

    @OneToOne
    private Cartao cartao;

    @Deprecated
    public Proposta() {
    }

    public Proposta(@NotBlank String nome,
                    @NotBlank String documento,
                    @Email String email,
                    @NotBlank String endereco,
                    @Positive @NotNull BigDecimal salario,
                    @NotNull String estado, Cartao cartao) {
        this.nome = nome;
        this.documento = documento;
        this.email = email;
        this.endereco = endereco;
        this.salario = salario;
        this.estado.toEnum(estado);
        this.cartao = cartao;
    }

    public Proposta(@NotBlank String nome,
                    @NotBlank String documento,
                    @Email String email,
                    @NotBlank String endereco,
                    @Positive @NotNull BigDecimal salario) {
        this.nome = nome;
        this.documento = documento;
        this.email = email;
        this.endereco = endereco;
        this.salario = salario;
    }

    public UUID getId() {
        return id;
    }

    public String getDocumento() {
        return documento;
    }

    public String getEmail() {
        return email;
    }

    public String getEndereco() {
        return endereco;
    }

    public BigDecimal getSalario() {
        return salario;
    }

    public String getEstado() {
            return estado.getDescricao();
    }

    public String getNome() {
        return nome;
    }

    public void setEstado(String estado) {
        this.estado = EstadoProposta.toEnum(estado);
    }

    public boolean temEstado() {
        return this.estado != null;
    }

    public Cartao getCartao() {
        return cartao;
    }

    public void setCartao(Cartao cartao) {
        if(estado.equals(EstadoProposta.ELEGIVEL)){
        this.cartao = cartao;
        }
    }

    public boolean naoTemCartao() {
        return this.cartao == null;
    }

    public boolean naoTemRestricao() {
       return this.estado.equals(EstadoProposta.ELEGIVEL);
    }

    @Override
    public String toString() {
        return "Proposta{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", documento='" + documento + '\'' +
                ", email='" + email + '\'' +
                ", endereco='" + endereco + '\'' +
                ", salario=" + salario +
                ", estado=" + estado +
                ", cartao=" + cartao +
                '}';
    }
}
