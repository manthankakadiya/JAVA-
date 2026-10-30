import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface NotBlank {
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface MaxLength {
    int value();
}

class SignupForm {

    @NotBlank
    @MaxLength(10)
    String username;

    @NotBlank
    @MaxLength(20)
    String email;

    SignupForm(String username, String email) {
        this.username = username;
        this.email = email;
    }
}

public class Main {

    public static void main(String[] args) throws Exception {

        SignupForm form = new SignupForm("", "abcdefghijklmnop@gmail.com");

        List<String> errors = new ArrayList<>();

        for (Field field : form.getClass().getDeclaredFields()) {

            field.setAccessible(true);

            String value = (String) field.get(form);

            // Check NotBlank
            if (field.isAnnotationPresent(NotBlank.class)) {
                if (value == null || value.trim().isEmpty()) {
                    errors.add(field.getName() + " cannot be blank");
                }
            }

            // Check MaxLength
            if (field.isAnnotationPresent(MaxLength.class)) {

                MaxLength max = field.getAnnotation(MaxLength.class);

                if (value != null && value.length() > max.value()) {
                    errors.add(field.getName()
                            + " is too long. Maximum "
                            + max.value() + " characters allowed");
                }
            }
        }

        if (errors.isEmpty()) {
            System.out.println("Form is valid");
        } else {
            for (String error : errors) {
                System.out.println(error);
            }
        }
    }
}