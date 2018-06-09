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
    <version>0.3.1</version>
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

### 3. Method level @LogCall and @LogMethod annotations 

Annotate methods that you want to log with @LogCall or @LogMethod annotations 

#### Example

```java
@LogCall(name="User Web Service")
public User createUser(CreateUserForm form) {
...
}
```

### 4. Class level @LogCalls and @LogMethods annotations 

Annotate a class with @LogCalls or @LogMethods annotations to log all methods of the class 

### 5. Any level (Class or Method) @LogIt annotation

 - Annotate a class with @LogIt to log all class's methods
 - Annotate a method with @LogIt to log only that method
 
 <code>@LogIt(type=Type.METHOD)</code> - "Method style" logging (by default)
 
 <code>@LogIt(type=Type.CALL)</code> - "Service call style" logging 
 
 <code>@LogIt(type=Type.SERVICE)</code> - "Service call style" logging 
 
 <code>@LogIt(name="User Web Service", type=Type.CALL)</code> - "Service call style" logging for the *User Web Service*
 
#### Default example - class level
 
 ```java
@LogIt
public class ClassWithMethods {
...
}
```

#### Default example - method level

 ```java
@LogIt
public User createUser(String email, String name) {
...
}
```

### 6. Logging Level parameter

Also you can pass the logging level parameter <code>level</code> with any annotation.
Available options:  <code>Level.DEBUG</code>, <code>Level.INFO</code>, <code>Level.WARN</code>, <code>Level.ERROR</code>

## Issues and suggestions

Report your issues or suggestions [here](https://github.com/LenarBad/EasyLog/issues)

## Contrubutions

This is an opensource project - feel free to send your pull requests

Hot issues to work on:

 - [#10 Support non-Spring projects](https://github.com/LenarBad/EasyLog/issues/10)
