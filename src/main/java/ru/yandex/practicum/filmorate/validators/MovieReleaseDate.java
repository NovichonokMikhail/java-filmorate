package ru.yandex.practicum.filmorate.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MovieReleaseDateValidator.class)
@Documented
public @interface MovieReleaseDate {
    String message() default "{CapitalLetter.invalid}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default { };
}