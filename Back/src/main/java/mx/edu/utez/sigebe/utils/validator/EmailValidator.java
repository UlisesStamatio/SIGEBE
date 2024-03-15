package mx.edu.utez.sigebe.utils.validator;

import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EmailValidator {
    private static final String EMAIL_PATTERN =
            "^(?:(?:[\\w`~!#$%^&*\\-=+;:{}'|,?\\/]+(?:(?:\\.(?:\"(?:\\\\?[\\w`~!#$%^&*\\-=+;:{}'|,?\\/\\.()<>\\[\\] @]|\\\\\"|\\\\\\\\)*\"|[\\w`~!#$%^&*\\-=+;:{}'|,?\\/]+))*\\.[\\w`~!#$%^&*\\-=+;:{}'|,?\\/]+)?)|(?:\"(?:\\\\?[\\w`~!#$%^&*\\-=+;:{}'|,?\\/\\.()<>\\[\\] @]|\\\\\"|\\\\\\\\)+\"))@(?:[a-zA-Z\\d\\-]+(?:\\.[a-zA-Z\\d\\-]+)*|\\[\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\])$";

    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    public boolean isValid(final String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
