### AOP (Aspect-Oriented Programming, 관점 지향 프로그래밍)란?

AOP는 관점 지향 프로그래밍으로, 프로그램의 횡단 관심사를 모듈화하는 프로그래밍 패러다임입니다. 횡단 관심사란 애플리케이션의 핵심 비즈니스 로직과는 별개로, 여러 곳에서 공통으로 처리해야 하는 기능(예: 로깅, 보안, 트랜잭션 관리 등)을 의미합니다. AOP는 이런 횡단 관심사를 핵심 비즈니스 로직과 분리하여, 코드 중복을 줄이고 모듈화를 쉽게 합니다.

1. AOP의 주요 개념
AOP에서 자주 사용되는 주요 용어와 개념은 다음과 같습니다.

Aspect (애스펙트): 횡단 관심사를 모듈화한 단위. 예를 들어, 로깅 기능을 하나의 애스펙트로 분리할 수 있습니다.

Join Point (조인 포인트): 애플리케이션 실행 중 특정 시점. 메서드 호출, 객체 생성, 예외 발생 등의 시점을 의미합니다.

Advice (어드바이스): 애스펙트에서 실제로 실행될 코드를 의미합니다. 언제, 어떻게 실행될지를 정의합니다. 어드바이스의 종류는 여러 가지가 있습니다.

Pointcut (포인트컷): 어드바이스가 적용될 Join Point를 선택하는 표현식입니다. 어떤 메서드나 클래스에 어드바이스를 적용할지 정의합니다.

Weaving (위빙): 애플리케이션 코드에 애스펙트를 적용하는 과정입니다. 위빙은 컴파일 시, 클래스 로딩 시, 또는 런타임에 적용될 수 있습니다.

2. Advice 종류
Before Advice: Join Point가 실행되기 전에 실행됩니다. 메서드 실행 전에 특정 로직을 실행할 때 사용됩니다.

After Returning Advice: Join Point가 정상적으로 실행된 후에 실행됩니다. 예외가 발생하지 않고 성공적으로 메서드가 실행된 후에 로직을 실행할 때 사용합니다.

After Throwing Advice: 메서드 실행 중 예외가 발생하면 실행됩니다.

After (Finally) Advice: 메서드의 실행 결과와 상관없이, 성공 또는 예외 발생 여부에 관계없이 실행됩니다.

Around Advice: Join Point의 전후로 모두 실행되는 Advice입니다. 메서드 실행 전과 후에 추가 작업을 실행할 수 있습니다. ProceedingJoinPoint를 사용해 실제 메서드 호출을 제어할 수 있습니다.

3. AOP 사용 사례
로깅: 메서드가 호출될 때마다 로그를 남기고 싶다면, 로깅 기능을 AOP로 분리하여 여러 메서드에서 동일한 로깅 로직을 반복하지 않도록 할 수 있습니다.

트랜잭션 관리: 데이터베이스 트랜잭션을 처리할 때, 메서드 실행 전후로 트랜잭션을 시작하고, 성공적으로 완료되면 커밋하거나 실패 시 롤백하는 기능을 쉽게 구현할 수 있습니다.

보안: 메서드 실행 전에 권한을 체크하고, 권한이 없는 경우 예외를 던져서 실행을 막는 기능을 AOP로 처리할 수 있습니다.

4. Spring AOP
Spring Framework는 AOP를 지원하며, 주로 프록시 기반 AOP를 사용합니다. Spring AOP는 런타임 시점에 프록시 객체를 생성해, 애플리케이션의 메서드 호출 흐름을 제어합니다.

4.1. Spring AOP 설정 예시
의존성 추가 (build.gradle)
```
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-aop'
}

```
4.2. Aspect 작성 예시
```
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    // 포인트컷: com.example 패키지 내 모든 메서드에 적용
    @Before("execution(* com.example..*(..))")
    public void logBeforeMethod() {
        System.out.println("메서드가 호출되기 전입니다.");
    }
}
```
위 코드에서 **@Aspect**는 이 클래스가 애스펙트임을 나타냅니다. **@Before**는 해당 메서드가 실행되기 전에 logBeforeMethod가 실행되도록 합니다. **execution(* com.example..*(..))**는 com.example 패키지 내의 모든 메서드에 적용된다는 의미입니다.

4.3. Around Advice 예시
```
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PerformanceAspect {

    // Around Advice: 메서드 실행 전후로 처리
    @Around("execution(* com.example.service.*.*(..))")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        // 실제 메서드 실행
        Object result = joinPoint.proceed();

        long elapsedTime = System.currentTimeMillis() - start;
        System.out.println(joinPoint.getSignature() + " 실행 시간: " + elapsedTime + "ms");

        return result;
    }
}
```
**Around**는 메서드 실행 전후로 로직을 삽입할 수 있습니다. 여기서는 메서드의 실행 시간을 측정하는 예입니다. **ProceedingJoinPoint**를 통해 실제 메서드를 호출합니다.

5. Pointcut 표현식
execution 표현식은 메서드의 시그니처를 기반으로 포인트컷을 정의하는 가장 일반적인 방식입니다.
```
execution(modifiers-pattern? ret-type-pattern declaring-type-pattern? name-pattern(param-pattern) throws-pattern?)
```
예시:
execution(* com.example.service.*.*(..)): com.example.service 패키지의 모든 메서드에 적용
execution(public * *(..)): 모든 public 메서드에 적용
execution(* set*(..)): 모든 set으로 시작하는 메서드에 적용
6. AOP와 프록시
Spring AOP는 기본적으로 프록시 패턴을 사용하여, 실제 객체 대신 프록시 객체를 만들어 메서드 호출을 가로챕니다. 이 프록시 객체가 메서드 호출 전후에 어드바이스를 적용할 수 있게 해줍니다. JDK 동적 프록시 또는 CGLIB 프록시가 사용될 수 있습니다.

7. AOP의 장점
코드 재사용성: 로깅, 트랜잭션 관리, 보안 등의 횡단 관심사를 코드에서 분리하여 재사용할 수 있습니다.
유지보수성: 비즈니스 로직과 횡단 관심사를 분리하여, 코드가 더욱 깔끔하고 유지보수가 쉬워집니다.
코드 간소화: 로직에 반복적으로 추가해야 할 코드를 제거하고, 애스펙트로 집중 관리할 수 있습니다.
8. AOP의 단점
복잡성 증가: AOP는 매우 강력한 기능을 제공하지만, 잘못 사용하면 로직의 흐름을 복잡하게 만들 수 있습니다.
디버깅 어려움: 프록시 객체를 사용하기 때문에 디버깅 시에 흐름을 추적하기 어려울 수 있습니다.
결론
AOP는 로깅, 트랜잭션, 보안과 같은 비즈니스 로직에 영향을 미치지 않는 공통된 로직을 분리하여 관리할 수 있는 매우 유용한 도구입니다. 이를 적절히 활용하면 코드의 재사용성을 높이고, 유지보수성을 개선할 수 있습니다. Spring AOP는 이러한 기능을 쉽게 사용할 수 있게 도와주며, 코드의 복잡도를 줄이는 데 큰 기여를 합니다.

#### Spring AOP의 포인트컷
Spring AOP의 포인트컷(Pointcut) 표현식은 메서드 실행 지점을 지정하는 데 사용됩니다. 이 표현식은 **execution**과 같은 키워드로 메서드 서명(signature)을 기반으로 필터링합니다. 아래 표는 Spring AOP에서 자주 사용하는 포인트컷 표현식을 요약한 것입니다.

표현식	설명
execution	메서드 실행 시점을 포인트컷으로 지정할 때 사용됩니다.
within	특정 패키지나 클래스 내의 메서드를 지정합니다.
this	프록시 객체의 타입을 기준으로 지정합니다.
target	실제 대상 객체의 타입을 기준으로 지정합니다.
args	메서드에 전달되는 파라미터 타입을 기준으로 지정합니다.
@annotation	특정 애노테이션이 적용된 메서드를 지정합니다.
@within	특정 애노테이션이 클래스 레벨에 적용된 경우를 지정합니다.
@target	특정 애노테이션이 클래스 레벨에 적용된 대상 객체를 기준으로 지정합니다.
@args	메서드 파라미터에 특정 애노테이션이 적용된 경우를 지정합니다.
