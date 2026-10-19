package com.careerflow.backend.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Traces calls into controllers, services and repositories.
 *
 * Logging policy: this aspect never logs argument values or return values, because request
 * DTOs (register, login) and responses (JWT) carry credentials. It logs the method name, the
 * argument types, plain numeric/boolean/UUID/enum arguments (ids, flags) and the duration,
 * all at DEBUG. Strings and objects are reduced to their type name. Failures log the
 * exception type only, since exception messages can embed user input.
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("within(@org.springframework.stereotype.Repository *)" +
            " || within(@org.springframework.stereotype.Service *)" +
            " || within(@org.springframework.web.bind.annotation.RestController *)")
    public void springBeanPointcut() {
    }

    @AfterThrowing(pointcut = "springBeanPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        if (log.isDebugEnabled()) {
            log.debug("Exception {} in {}.{}()", e.getClass().getSimpleName(),
                    joinPoint.getSignature().getDeclaringType().getSimpleName(),
                    joinPoint.getSignature().getName());
        }
    }

    @Around("springBeanPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!log.isDebugEnabled()) {
            return joinPoint.proceed();
        }

        String target = joinPoint.getSignature().getDeclaringType().getSimpleName()
                + "." + joinPoint.getSignature().getName() + "()";
        long start = System.nanoTime();
        log.debug("Enter: {} args = {}", target, describeArguments(joinPoint.getArgs()));
        try {
            return joinPoint.proceed();
        } finally {
            log.debug("Exit: {} ({} ms)", target, (System.nanoTime() - start) / 1_000_000);
        }
    }

    /** Only values that cannot carry credentials or personal data are shown; the rest is a type name. */
    private static String describeArguments(Object[] args) {
        return Arrays.stream(args)
                .map(LoggingAspect::describe)
                .collect(Collectors.joining(", ", "[", "]"));
    }

    private static String describe(Object arg) {
        if (arg == null) {
            return "null";
        }
        if (arg instanceof Number || arg instanceof Boolean || arg instanceof UUID || arg instanceof Enum<?>) {
            return String.valueOf(arg);
        }
        return arg.getClass().getSimpleName();
    }
}
