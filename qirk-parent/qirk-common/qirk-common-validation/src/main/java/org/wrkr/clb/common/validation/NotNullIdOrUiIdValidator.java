package org.wrkr.clb.common.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.wrkr.clb.common.validation.constraints.NotNullIdOrUiId;

public class NotNullIdOrUiIdValidator
        implements ConstraintValidator<NotNullIdOrUiId, IdAndUiIdObject> {

    @Override
    public boolean isValid(IdAndUiIdObject value, @SuppressWarnings("unused") ConstraintValidatorContext context) {
        return (value.getId() != null ^ value.getUiId() != null);
    }
}
