package sejong.smartnotice.helper.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class TimeTraceAop {

    // 메서드 호출 전후에 수행
    // [접근 제한자 패턴] - " " - 리턴 타입 - " " - 패키지 - 클래스 - 메서드(매개변수)
    // controller 패키지 내부의 모든 메서드에 대해 시작 - 종료 시간을 계산하는 메서드
    @Around("execution(* sejong.smartnotice.controller.*Controller.*.*(..))")
    public Object logging(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        log.info("START: {}", joinPoint.toString());

        try {
            return joinPoint.proceed();
        } finally {
            long finish = System.currentTimeMillis();
            long totalTimeMs = finish - start;

            log.info("END: {}", joinPoint + " " + totalTimeMs + "ms");
        }
    }
}
