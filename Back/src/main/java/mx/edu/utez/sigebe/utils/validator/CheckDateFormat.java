package mx.edu.utez.sigebe.utils.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DateValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckDateFormat {
    public String message() default "¡fecha invalida!";
    public Class<?>[] groups() default {};
    public Class<? extends Payload>[] payload() default {};
    String pattern();
}
