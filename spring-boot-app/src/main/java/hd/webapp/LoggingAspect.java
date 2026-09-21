package hd.webapp;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
	
	private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* hd.webapp.UserService.*(..))")
    public void logBefore(JoinPoint joinPoint){
    	 String methodName = joinPoint.getSignature().getName();
    	 String className = joinPoint.getTarget().getClass().getSimpleName();
         log.info(">>> Intercepted class {} method call: {}", className, methodName);
    }

    @After("execution(* hd.webapp.UserService.*(..))")
    public void logAfter(JoinPoint joinPoint){
    	String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        log.info("<<< Intercepted class {} method call: {}", className, methodName);
    }
}
