[![Maven Central](https://img.shields.io/maven-central/v/io.lenar/easy-log.svg)](https://maven-badges.herokuapp.com/maven-central/io.lenar/easy-log)

[https://lenarbad.github.io/easylog/](https://lenarbad.github.io/easylog/)

# EasyLog 

EasyLog is an open source library for logging/debugging in Java projects.

* [EasyLog](#easylog)
  * [How to setup EasyLog](#how-to-setup-easylog)
  * [How to use EasyLog](#how-to-use-easylog)
      * [Extend EasyLogger for Spring Project](#extend-easylogger-for-spring-project)
      * [Extend EasyLoggerNoSpring for Non Spring Project](#extend-easyloggernospring-for-non-spring-project)
      * [@LogIt annotation](#logit-annotation)
      * [Logging Level](#logging-level)
      * [Labels](#labels)
      * [Exclude parameters from logging](#exclude-parameters-from-logging)
      * [Mask fields](#mask-fields)
      * [Logging Styles](#logging-styles)
  * [Examples](#examples)
  * [Warning](#warning)
  * [Issues and suggestions](#issues-and-suggestions)
  * [Contributions](#contributions)


## How to setup EasyLog

EasyLog supports Java project with and without Spring.

See how to setup EasyLog in example projects
- [EasyLog for non-Spring projects - example](https://github.com/LenarBad/EasyLog-no-Spring-Example)
- [EasyLog for Spring projects - example](https://github.com/LenarBad/EasyLog-Spring-Example)

## How to use EasyLog

### Extend EasyLogger for Spring Project

In your project create the class that extends the <code>EasyLogger</code> aspect and add the <code>@Component</code> annotation.

```java
import io.lenar.easy.log.EasyLogger;
import org.springframework.stereotype.Component;

@Component
public class MyLogger extends EasyLogger {
}
```

### Extend EasyLoggerNoSpring for Non Spring Project

```java
@Aspect
public class MyLogger extends EasyLoggerNoSpring {

    @Around(CLASS_LEVEL_LOGIT_POINTCUT)
    public Object classLog(ProceedingJoinPoint jp, LogIt annotation) throws Throwable {
        return logItClassLevel(jp, annotation);
    }

    @Around(METHOD_LEVEL_LOGIT_POINTCUT)
    public Object methodLog(ProceedingJoinPoint jp, LogIt annotation) throws Throwable {
        return logItMethodLevel(jp, annotation);
    }
}
```

Note: In IntelliJ make sure that you use AJC compiler if this wasn't set automatically.

### @LogIt annotation 

#### Method level

Annotate the methods that you want to log with <code>@LogIt</code> annotation 

```java
@LogIt
public User createUser(CreateUserForm form) {
...
}
```
#### Class level

If you need to log all methods of a class you can annotate the class with <code>@LogIt</code> annotation

 ```java
@LogIt
public class ClassWithMethods {
...
}
```

### Logging Level

You can set the logging level by passing the parameter <code>level</code> with ```@LogIt``` annotation.

Available options:  ```DEBUG```, ```INFO```, ```WARN```, ```ERROR```

By default ```level=Level.INFO```

### Labels

Labels help you to simplify a search for specific entries in the logs.
Just pass another annotation parameter ```String label```. 

### Exclude parameters from logging

You can skip some parameters and not log them with ```String[] ignoreParameters```. 

By default there is no ignored parameter.

### Mask fields

```String[] maskFields() default {}``` - allows to replace actual values for field names for the results returned by the method with ```"XXXMASKEDXXX"```.

_Note: We don't modify returned results, just customize how the results look in the logs._

By default there is no masked field.

Might be used for: 
 - masking any sensitive information that shouldn't be logged
 - decreasing the amount of logged info. For example we can replace huge lists/arrays (in returned results) that are not important in terms of logging with ```"XXXMASKEDXXX"```
 
### Logging Styles

Available values: ```PRETTY_PRINT_WITH_NULLS```, ```PRETTY_PRINT_NO_NULLS```, ```COMPACT_WITH_NULLS```, ```MINIMAL```, ```AS_IS```.

By default it's ```PRETTY_PRINT_WITH_NULLS```

Here PRETTY_PRINT means _"pretty printed JSON"_.

```COMPACT_WITH_NULLS``` and ```MINIMAL``` also mean _JSON_ but not _"pretty printed"_

See the difference

```COMPACT_WITH_NULLS``` and ```MINIMAL```
```
{"user":{"zipcode":"12345","firstName":"John","lastName":"Smith","password":"passasdfasdf","email":"XXXMASKEDXXX"}}
```

 ```PRETTY_PRINT_WITH_NULLS``` and ```PRETTY_PRINT_NO_NULLS```
```
{  
   "user":{  
      "zipcode":"12345",
      "firstName":"John",
      "lastName":"Smith",
      "password":"passasdfasdf",
      "email":"XXXMASKEDXXX"
   }
}
```

Use ```PRETTY_PRINT_WITH_NULLS``` and ```COMPACT_WITH_NULLS``` if you want to log (serialize ```null```s)

Use ```PRETTY_PRINT_NO_NULLS``` and ```MINIMAL``` if you want to exclude nulls from logging.

```AS_IS``` is used if you want to serialize the parameters and returned result with the ```toString``` method. In this case ```maskFields``` will be ignored

## Examples

 ```java
@LogIt(label="DEBUGGING ISSUE 1234", level=DEBUG)
public class ClassWithMethods {
...
}
```

 ```java
@LogIt(label="USER SERVICE CALL")
public class ClassWithMethods {
...
}
```

 ```java
@LogIt(label="USER CARDS", maskFields={"cardNumber", "pin"})
public UserCardsInfo getUserCards(..) {
...
}
```

 ```java
@LogIt(label="USER SERVICE CALL", maskFields={"password"})
public User login(String userName, String password) {
...
}
```

Working example projects
- [EasyLog for non-Spring projects - example](https://github.com/LenarBad/EasyLog-no-Spring-Example)
- [EasyLog for Spring projects - example](https://github.com/LenarBad/EasyLog-Spring-Example)

## Warning

Don't use the ```maskFields``` parameter for complex objects in highly loaded applications where the performance is the most importance thing.

It walks through all object's fields recursively to find all the fields that should be masked.

## Issues and suggestions

Report your issues or suggestions [here](https://github.com/LenarBad/EasyLog/issues)

## Contributions

This is an opensource project - feel free to send your pull requests

Hot issues to work on:

 - [#15 Add JavaDocs](https://github.com/LenarBad/EasyLog/issues/15)
 - [#16 Add Unit Tests](https://github.com/LenarBad/EasyLog/issues/16)
