package io.lenar.easy.log.support.signature;

import org.aspectj.lang.ProceedingJoinPoint;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SignatureMap {

    private ConcurrentMap<String, EasyLogSignature> signatures;

    public SignatureMap() {
        this.signatures = new ConcurrentHashMap<>();
    }

    public EasyLogSignature get(ProceedingJoinPoint jp) {
        String key = jp.toLongString();
        if (signatures.containsKey(key)) {
            return signatures.get(key);
        }
        EasyLogSignature signature = new EasyLogSignature(new JPSignature(jp));
        signatures.put(key, signature);
        return signature;
    }

}
