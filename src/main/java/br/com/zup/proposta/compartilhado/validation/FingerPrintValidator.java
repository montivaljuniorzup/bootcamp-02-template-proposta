package br.com.zup.proposta.compartilhado.validation;

import br.com.zup.proposta.dto.request.BiometriaRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Base64;

public class FingerPrintValidator implements Validator {

    private static Logger logger = LoggerFactory.getLogger(FingerPrintValidator.class);

    @Override
    public boolean supports(Class<?> aClass) {
        return BiometriaRequest.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object biometriaRequest, Errors errors) {
        if (errors.hasErrors()) {
            logger.error("Saindo da validação de fingerPrint pois já existem erros de validação");
            return;
        }
            BiometriaRequest biometria = (BiometriaRequest) biometriaRequest;
        try {
            Base64.getDecoder().decode(biometria.getFingerPrint());
            logger.info("Valor de fingerPrint pode ser decodificado");
        } catch (IllegalArgumentException e) {
            logger.error("Valor de fingerPrint não pôde ser decodificado");
            errors.rejectValue("fingerPrint", null, "Formato do Finger Print inválido");
        }
    }
}
