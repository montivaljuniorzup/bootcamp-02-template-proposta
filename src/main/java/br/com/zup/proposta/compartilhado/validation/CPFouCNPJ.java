package br.com.zup.proposta.compartilhado.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {CpfouCnpjValidator.class})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CPFouCNPJ {

    String message() default "{br.com.zup.proposta.beanvalidation.cpfoucnpj}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
