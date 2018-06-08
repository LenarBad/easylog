package io.lenar.easy.log.annotations;

import static io.lenar.easy.log.Level.INFO;
import static io.lenar.easy.log.Type.METHOD;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.lenar.easy.log.Level;
import io.lenar.easy.log.Type;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface LogIt {

    Type type() default METHOD;

    String name() default "";

    Level level() default INFO;

}