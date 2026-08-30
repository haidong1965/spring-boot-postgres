package hd.webapp;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Before("execution(* hd.webapp.UserService.*(..))")
    public void logBefore(JoinPoint joinPoint){
    	 String methodName = joinPoint.getSignature().getName();
    	 String className = joinPoint.getTarget().getClass().getSimpleName();
         System.out.println(">>> Intercepted class " + className + " method call: " + methodName);
    }

    @After("execution(* hd.webapp.UserService.*(..))")
    public void logAfter(JoinPoint joinPoint){
    	String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        System.out.println("<<< Intercepted class " + className + " method call: " + methodName);
    }
}
