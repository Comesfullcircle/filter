Filter와 Interceptor는 Spring Framework에서 **요청(request)**과 응답(response) 흐름을 가로채서 특정 작업을 수행하기 위해 사용하는 두 가지 주요 개념

1. Filter (필터)
개념:
Servlet 필터는 서블릿 컨테이너 수준에서 동작하며, HTTP 요청 및 응답을 가로채고, 가공할 수 있는 기능을 제공합니다. 이는 DispatcherServlet이 요청을 처리하기 전에 실행됩니다.
필터는 주로 인증, 권한 검증, 요청 로깅, 요청 데이터 수정과 같은 작업에 사용됩니다.
동작 위치:
필터는 서블릿 컨테이너의 요청 처리 파이프라인의 초입에 위치하여 가장 먼저 요청을 처리하고, 가장 나중에 응답을 처리합니다.
사용 목적:
보안(인증 및 권한 부여)
요청/응답 로깅
데이터 압축 및 변환(예: 응답을 압축하거나, 요청 데이터를 변환)
CORS 처리
필터 작성 방법:
java
코드 복사
@Slf4j
@Component
public class LoggerFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("Request received in LoggerFilter");
        
        // 요청 전 처리 로직
        chain.doFilter(request, response); // 다음 필터 또는 서블릿으로 요청 전달
        
        // 응답 후 처리 로직
        log.info("Response sent from LoggerFilter");
    }
}
doFilter() 메서드 내에서 요청 전과 응답 후에 원하는 로직을 추가할 수 있습니다.
chain.doFilter(request, response)는 다음 필터나 서블릿으로 요청을 전달하는 역할을 합니다.
필터 등록 (선택사항):
필터를 특정 경로에서만 동작시키고 싶거나 필터의 순서를 지정하고 싶다면, **FilterRegistrationBean**을 사용하여 등록할 수 있습니다.

java
코드 복사
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<LoggerFilter> loggingFilter() {
        FilterRegistrationBean<LoggerFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LoggerFilter());
        registrationBean.addUrlPatterns("/api/*");  // 특정 URL 패턴에서만 필터 적용
        registrationBean.setOrder(1);  // 필터 순서 지정
        return registrationBean;
    }
}
특징:
서블릿 표준에 정의된 기능이므로 Spring MVC뿐만 아니라 다른 서블릿 기반 프레임워크에서도 사용 가능합니다.
요청, 응답의 전후에 로직을 삽입할 수 있습니다.
2. Interceptor (인터셉터)
개념:
Spring Interceptor는 Spring MVC에서 제공하는 기능으로, 컨트롤러의 전후에 특정 작업을 수행할 수 있는 기능을 제공합니다.
인터셉터는 Spring MVC의 DispatcherServlet 이후에 실행되며, 핸들러(컨트롤러)가 호출되기 전에 전처리를 하거나, 컨트롤러가 처리한 응답을 후처리하는 데 사용됩니다.
동작 위치:
인터셉터는 Spring의 DispatcherServlet과 컨트롤러 사이에서 동작합니다. 요청이 컨트롤러로 도달하기 전에, 컨트롤러가 응답을 생성한 후에 동작합니다.
사용 목적:
로그인 체크: 특정 페이지에 대한 접근 권한을 확인하기 전에 처리
요청 처리 시간 로깅: 요청 처리 시간을 측정하고 로그로 남김
데이터 검증 및 변환: 요청 데이터를 검증하거나 가공
인터셉터 작성 방법:
Spring에서는 HandlerInterceptor 인터페이스를 구현하여 인터셉터를 작성합니다.

java
코드 복사
@Component
public class LoggerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("Request URI: {}", request.getRequestURI());
        return true;  // true를 반환해야 요청이 다음 단계(컨트롤러)로 진행됨
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("Post handle executed");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("After completion: Request processed");
    }
}
preHandle()은 컨트롤러가 실행되기 전에 실행됩니다. 여기서 true를 반환해야 요청이 컨트롤러로 전달됩니다.
postHandle()은 컨트롤러가 실행된 후, 뷰가 렌더링되기 전에 실행됩니다.
afterCompletion()은 응답이 클라이언트로 전달된 후에 실행됩니다.
인터셉터 등록:
인터셉터는 WebMvcConfigurer를 통해 등록할 수 있으며, 특정 경로나 패턴에 대해서만 인터셉터를 적용할 수 있습니다.

java
코드 복사
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoggerInterceptor())
                .addPathPatterns("/api/**")  // 특정 경로에만 인터셉터 적용
                .excludePathPatterns("/api/login");  // 특정 경로는 제외
    }
}
특징:
인터셉터는 Spring MVC 전용 기능입니다.
요청 전후에 여러 단계에서 커스터마이징을 할 수 있으며, 특정 컨트롤러나 경로에 대해 설정을 세밀하게 제어할 수 있습니다.
3. Filter와 Interceptor의 차이점
구분	Filter	Interceptor
적용 범위	서블릿 컨테이너 (Spring MVC에 한정되지 않음)	Spring MVC 내에서만 동작
실행 시점	DispatcherServlet 실행 이전	DispatcherServlet 이후, 컨트롤러 전후
사용 목적	보안, 요청 및 응답 변환, 로깅, CORS 처리 등	로그인 체크, 요청 로깅, 응답 후처리 등
요청/응답 제어	요청과 응답을 수정하거나 차단할 수 있음	요청의 흐름을 제어하지만, 응답은 수정 불가
특정 경로 설정	FilterRegistrationBean으로 URL 패턴 설정	addInterceptors로 세부적인 경로 설정 가능
언제 Filter를 사용하고, 언제 Interceptor를 사용해야 할까?
Filter는 Spring 이외의 서블릿 기반 애플리케이션에도 적용할 수 있는 기능이 필요할 때 사용됩니다. 예를 들어, 보안, 인증, CORS 처리 등 전역적인 요청/응답 처리를 할 때 유용합니다.
Interceptor는 Spring MVC의 컨트롤러에 대한 세부적인 전후 처리를 할 때 적합합니다. 예를 들어, 로그인 처리, 요청 로깅, 데이터 검증 등을 수행할 때 사용됩니다.
결론:
Filter는 서블릿 기반 웹 애플리케이션에서 전역적인 요청과 응답을 다루는 데 사용됩니다.
Interceptor는 Spring MVC에서 컨트롤러에 대한 전처리/후처리를 위한 더 세밀한 컨트롤이 가능합니다.
