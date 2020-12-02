package br.com.zup.proposta.proposta;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Entity
public class Proposta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Deprecated
    public Proposta() {
    }

    public Proposta(@NotBlank String nome,
                    @NotBlank String documento,
                    @Email String email,
                    @NotBlank String endereco,
                    @Positive @NotNull BigDecimal salario,
                    @NotNull String estado) {
        this.nome = nome;
        this.documento = documento;
        this.email = email;
        this.endereco = endereco;
        this.salario = salario;
        this.estado.toEnum(estado);
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

    public Long getId() {
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
}
