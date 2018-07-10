package io.lenar.easy.log.annotations;

import static io.lenar.easy.log.Level.INFO;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.lenar.easy.log.Level;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface LogIt {

    String label() default "";

    Level level() default INFO;

    String[] ignoreParameters() default {};

    String[] maskFields() default {};

    boolean prettyPrint() default true;

    boolean logNulls() default true;

}