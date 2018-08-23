package io.lenar.easy.log.annotations;

import static io.lenar.easy.log.annotations.Level.INFO;
import static io.lenar.easy.log.annotations.Style.PRETTY_PRINT_WITH_NULLS;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface LogIt {

    String label() default "";

    Level level() default INFO;

    String[] ignoreParameters() default {};

    String[] maskFields() default {};

    Style style() default PRETTY_PRINT_WITH_NULLS;

    int retryAttempts() default 1; // only one re-try by default

    long retryDelay() default 0;

    Class<? extends Throwable>[] retryExceptions() default {};
}