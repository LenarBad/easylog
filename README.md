[![Maven Central](https://img.shields.io/maven-central/v/io.lenar/easy-log.svg)](https://maven-badges.herokuapp.com/maven-central/io.lenar/easy-log)

# EasyLog 

EasyLog is an open source library for logging/debugging in Java projects.

EasyLog supports Java project with and without Spring.

See how to setup EasyLog in example projects
- [EasyLog for non-Spring projects - example](https://github.com/LenarBad/EasyLog-no-Spring-Example)
- [EasyLog for Spring projects - example](https://github.com/LenarBad/EasyLog-Spring-Example)

## How to use EasyLog

### 1. Extend EasyLogger

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

```String[] maskFields() default {}``` - allows to replace actual values for field names for the results returned by the method with ```"XXXMASKEDXXX"```.

_Note: We don't modify returned results, just customize how the results look in the logs._

By default there is no masked field.

Might be used for: 
 - masking any sensitive information that shouldn't be logged
 - decreasing the amount of logged info. For example we can replace huge lists/arrays (in returned results) that are not important in terms of logging with ```"XXXMASKEDXXX"```

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

## Warning

Don't use the ```maskFields``` parameter for complex objects in highly loaded applications where the performance is the most importance thing.

It walks through all object's fields recursively to find all the fields that should be masked.

## Issues and suggestions

Report your issues or suggestions [here](https://github.com/LenarBad/EasyLog/issues)

## Contrubutions

This is an opensource project - feel free to send your pull requests

Hot issues to work on:

 - [#15 Add JavaDocs](https://github.com/LenarBad/EasyLog/issues/15)
 - [#16 Add Unit Tests](https://github.com/LenarBad/EasyLog/issues/16)
