package io.lenar.easy.log;

import io.lenar.easy.log.annotations.LogIt;
import org.aspectj.lang.ProceedingJoinPoint;
import static io.lenar.easy.log.support.PJPSupport.*;

public class InterfaceLogger extends UneasyLogger {

    public Object logIfMethodHasAnnotatedInterface(ProceedingJoinPoint jp) throws Throwable {
        if (hasTargetMethodLevelLogItAnnotation(jp) || hasTargetClassLevelLogItAnnotation(jp)) {
            return jp.proceed(jp.getArgs());
        }

        LogIt annotation = getInterfaceMethodLevelAnnotationIfAny(jp);
        if (annotation != null) {
            return logMethod(jp, annotation);
        }

        annotation = getInterfaceLevelAnnotationIfAny(jp);
        if (annotation != null) {
            return logMethod(jp, annotation);
        }

        return jp.proceed(jp.getArgs());
    }

}
