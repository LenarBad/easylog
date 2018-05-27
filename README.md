# EasyLog

EasyLog is an open source library for logging in Java projects.

Currently from the box EasyLog supports Spring projects only.

## How to use EasyLog

### 1. Add Maven dependency

```
<dependency>    
    <groupId>io.lenar</groupId>
    <artifactId>easy-log</artifactId>
    <version>0.0.4</version>
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

### 3. @LogCall and @LogMethod annotations 

Annotate methods that you want to log with @LogCall or @LogMethod annotations 

## Issues and suggestions

Report your issues or suggestions [here](https://github.com/LenarBad/EasyLog/issues)

## Contrubutions

This is an opensource project - feel free to send your pull requests
