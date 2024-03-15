package mx.edu.utez.sigebe.utils.validator;

import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CurpValidator {
    private static final String CURP_PATTERN =
            "[A-Z]{1}[AEIOU]{1}[A-Z]{2}[0-9]{2}(0[1-9]|1[0-2])(0[1-9]|1[0-9]|2[0-9]|3[0-1])[HM]{1}(AS|BC|BS|CC|CS|CH|CL|CM|DF|DG|GT|GR|HG|JC|MC|MN|MS|NT|NL|OC|PL|QT|QR|SP|SL|SR|TC|TS|TL|VZ|YN|ZS|NE)[B-DF-HJ-NP-TV-Z]{3}[0-9A-Z]{1}[0-9]{1}";

    private static final Pattern pattern = Pattern.compile(CURP_PATTERN);

    public boolean isValid(final String curp) {
        Matcher matcher = pattern.matcher(curp);
        return matcher.matches();
    }
}
