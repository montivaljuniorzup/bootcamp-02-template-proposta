package br.com.zup.proposta.compartilhado.validation;

import org.hibernate.validator.internal.constraintvalidators.hv.br.CNPJValidator;
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CpfouCnpjValidator implements ConstraintValidator<CPFouCNPJ, String> {

    private final CPFValidator cpfValidator= new CPFValidator();
    private final CNPJValidator cnpjValidator= new CNPJValidator();

    @Override
    public void initialize(CPFouCNPJ constraintAnnotation) {
        cpfValidator.initialize(null);
        cnpjValidator.initialize(null);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {

        return cpfValidator.isValid(value, constraintValidatorContext) || cnpjValidator.isValid(value, constraintValidatorContext);
    }
}
