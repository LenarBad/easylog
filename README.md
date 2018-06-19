[![Maven Central](https://img.shields.io/maven-central/v/io.lenar/easy-log.svg)](https://maven-badges.herokuapp.com/maven-central/io.lenar/easy-log)

# EasyLog 

EasyLog is an open source library for logging in Java projects.

Currently from the box EasyLog supports Spring projects only.

## How to use EasyLog

### 1. Add Maven dependency

```xml
<dependency>    
    <groupId>io.lenar</groupId>
    <artifactId>easy-log</artifactId>
    <version>0.9.2</version>
</dependency>
```

### 2. Extend EasyLogger

In your project create the class that extends the <code>EasyLogger</code> aspect and add the <code>@Component</code> annotation.

```java
import io.lenar.easy.log.EasyLogger;
import org.springframework.stereotype.Component;

@Component
public class MyLogger extends EasyLogger {
}
```

### 2. @LogIt annotation 

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

### 3. Logging Level

You can set the logging level by passing the parameter <code>level</code> with ```@LogIt``` annotation.

Available options:  ```DEBUG```, ```INFO```, ```WARN```, ```ERROR```

By default ```level=Level.INFO```

### 4. Labels

Labels help you to simplify a search for specific entries in the logs.
Just pass another annotation parameter ```String label```. 

### 5. Exclude parameters from logging

You can skip some parameters and not log them with ```String[] ignoreParameters```. 

By default there is no ignored parameter.

### 6. Mask fields in response/return

```String[] maskFields() default {}``` - allows to mask in the log (```"XXXMASKEDXXX"```) actual values for field names in the list for the results returned by the method.

By default there is no masked field

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

## Issues and suggestions

Report your issues or suggestions [here](https://github.com/LenarBad/EasyLog/issues)

## Contrubutions

This is an opensource project - feel free to send your pull requests

Hot issues to work on:

 - [#10 Support non-Spring projects](https://github.com/LenarBad/EasyLog/issues/10)
 - [#15 Add JavaDocs](https://github.com/LenarBad/EasyLog/issues/15)
 - [#16 Add Unit Tests](https://github.com/LenarBad/EasyLog/issues/16)
