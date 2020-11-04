package org.wrkr.clb.common.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.wrkr.clb.common.validation.constraints.IsLetterOrDigitOrWhitespace;

public class IsLetterOrDigitOrWhitespaceValidator
        implements ConstraintValidator<IsLetterOrDigitOrWhitespace, String> {

    @Override
    public boolean isValid(String value, @SuppressWarnings("unused") ConstraintValidatorContext context) {
        for (int i = 0; i < value.length(); i++) {
            if (!Character.isLetterOrDigit(value.charAt(i)) && !Character.isWhitespace(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
