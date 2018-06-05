package io.lenar.easy.log.annotations;

import static io.lenar.easy.log.Level.INFO;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.lenar.easy.log.Level;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LogMethod {

    Level level() default INFO;

}