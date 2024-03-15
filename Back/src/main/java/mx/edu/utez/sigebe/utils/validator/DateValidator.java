package mx.edu.utez.sigebe.utils.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DateValidator implements ConstraintValidator<CheckDateFormat, String> {
    private String dateFormat;

    @Override
    public void initialize(CheckDateFormat constraintAnnotation) {
        dateFormat = constraintAnnotation.pattern();
    }

    @Override
    public boolean isValid(String strDate, ConstraintValidatorContext context) {
        if (strDate == null) {
            return false;
        }
        try {
            if(strDate.length()!=10){
                return false;
            }
            DateFormat date = new SimpleDateFormat(this.dateFormat);
            date.setLenient(false);
            date.parse(strDate);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
