# EasyLog

EasyLog is an open source library for logging in Java projects.

Currently from the box EasyLog supports Spring projects only.

## How to use EasyLog

### 1. Add Maven dependency

```
<dependency>    
    <groupId>io.lenar</groupId>
    <artifactId>easy-log</artifactId>
    <version>0.1.0</version>
</dependency>
```

### 2. Extend EasyLogger

In your project create the class that extends the <code>EasyLogger</code> aspect and add the <code>@Component</code> annotation.

```
import io.lenar.easy.log.EasyLogger;
import org.springframework.stereotype.Component;

@Component
public class MyLogger extends EasyLogger {
}
```

### 3. Method level @LogCall and @LogMethod annotations 

Annotate methods that you want to log with @LogCall or @LogMethod annotations 
#### Example
```
@LogCall(name="User Web Service")
public User createUser(CreateUserForm form) {
...
}
```

### 4. Class level @LogCalls and @LogMethods annotations 

Annotate a class with @LogCalls or @LogMethods annotations to log all methods of the class 

## Issues and suggestions

Report your issues or suggestions [here](https://github.com/LenarBad/EasyLog/issues)

## Contrubutions

This is an opensource project - feel free to send your pull requests

Hot issues that need to be worked on:

 - [#10 Support non-Spring projects](https://github.com/LenarBad/EasyLog/issues/10)
