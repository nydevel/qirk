package org.wrkr.clb.common.validation.constraints;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.wrkr.clb.common.validation.NotNullIdOrUiIdValidator;


@Target(TYPE)
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = { NotNullIdOrUiIdValidator.class })
public @interface IsLetterOrDigitOrWhitespace {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
