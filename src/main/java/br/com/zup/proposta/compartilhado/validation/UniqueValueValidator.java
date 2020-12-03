package br.com.zup.proposta.compartilhado.validation;

import javax.persistence.EntityManager;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueValueValidator implements ConstraintValidator<UniqueValue, Object> {

    private Class<?> classe;
    private String atributo;

    private final EntityManager manager;

    public UniqueValueValidator(EntityManager manager) {
        this.manager = manager;
    }

    @Override
    public void initialize(UniqueValue constraintAnnotation) {
    this.classe = constraintAnnotation.classe();
    this.atributo = constraintAnnotation.atributo();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        if(value == null){
            return true;
        }

        return manager
                .createQuery("Select p from "+ this.classe.getSimpleName() + " p where "+ this.atributo + "=:value")
                .setParameter("value",value)
                .getResultList()
                .isEmpty();
    }
}
