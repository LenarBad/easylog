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
    <version>0.9.1</version>
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

### 3. Annotation parameters

#### Logging Level parameter

Also you can pass the logging level parameter <code>level</code> with any annotation.

Available options:  <code>DEBUG</code>, <code>INFO</code>, <code>WARN</code>, <code>ERROR</code>

By default <code>level=Level.INFO</code>

#### Label

With <code>@LogIt</code> you can set another parameter - <code>String label</code>.
<code>label</code> lets us create labels/ids in the logs to simplify a search for specific entries. 

 ```java
@LogIt(label="DEBUGGING ISSUE 1234"
public class ClassWithMethods {
...
}
```

 ```java
@LogIt(label="USER SERVICE CALL"
public class ClassWithMethods {
...
}
```
By default <code>label=""</code>

## Issues and suggestions

Report your issues or suggestions [here](https://github.com/LenarBad/EasyLog/issues)

## Contrubutions

This is an opensource project - feel free to send your pull requests

Hot issues to work on:

 - [#10 Support non-Spring projects](https://github.com/LenarBad/EasyLog/issues/10)
 - [#15 Add JavaDocs](https://github.com/LenarBad/EasyLog/issues/15)
 - [#16 Add Unit Tests](https://github.com/LenarBad/EasyLog/issues/16)
