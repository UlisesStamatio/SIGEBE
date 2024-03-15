package mx.edu.utez.sigebe.utils.validator;

import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PhoneValidator {
    private static final String PHONE_PATTERN =
            "^((((\\+\\(52[\\s\\-]?1\\)[\\s\\-]?)?)|(((\\+\\(52\\)|\\+52)[\\s\\-]?)((\\(1\\)|1)[\\s\\-]?)?))?)((((\\([\\d]{2}\\)|[\\d]{2})[\\s\\-]?[\\d]{4}[\\s\\-]?)|((\\([\\d]{3}\\)|[\\d]{3})[\\s\\-]?[\\d]{3}[\\s\\-]?))[\\d]{4})$";

    private static final Pattern pattern = Pattern.compile(PHONE_PATTERN);

    public boolean isValid(final String phone) {
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }
}
