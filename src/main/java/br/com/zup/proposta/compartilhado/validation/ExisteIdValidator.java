package br.com.zup.proposta.compartilhado.validation;

import javax.persistence.EntityManager;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.UUID;

public class ExisteIdValidator implements ConstraintValidator<ExisteId, UUID> {

    private Class<?> classe;

    private String atributo;

    private EntityManager manager;

    public ExisteIdValidator(EntityManager manager) {
        this.manager = manager;
    }

    @Override
    public void initialize(ExisteId constraintAnnotation) {
        this.atributo = constraintAnnotation.atributo();
        this.classe = constraintAnnotation.classe();
    }

    @Override
    public boolean isValid(UUID uuid, ConstraintValidatorContext constraintValidatorContext) {
        return !manager
                .createQuery("Select 1 from " + classe.getSimpleName() + "where " + atributo + " =:id")
                .setParameter("id", uuid)
                .getResultList()
                .isEmpty();

    }
}
