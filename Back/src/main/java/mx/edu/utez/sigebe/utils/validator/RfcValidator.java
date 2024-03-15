package mx.edu.utez.sigebe.utils.validator;

import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RfcValidator {
    private static final String RFC_PATTERN =
            "^([A-ZÑ&]{3,4}) ?(?:- ?)?(\\d{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[12]\\d|3[01])) ?(?:- ?)?([A-Z\\d]{2})([A\\d])$";

    private static final Pattern pattern = Pattern.compile(RFC_PATTERN);

    public boolean isValid(final String rfc) {
        Matcher matcher = pattern.matcher(rfc);
        return matcher.matches();
    }
}
