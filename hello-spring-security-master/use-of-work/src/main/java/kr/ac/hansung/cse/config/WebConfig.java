package kr.ac.hansung.cse.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * Spring MVC 웹 계층 설정
 *
 * @EnableWebMvc  : Spring MVC 활성화 (DispatcherServlet, HandlerMapping 등 자동 등록)
 * @ComponentScan : controller 패키지의 @Controller 빈을 자동 등록
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {
        "kr.ac.hansung.cse.controller",
        "kr.ac.hansung.cse.exception"  // GlobalExceptionHandler(@ControllerAdvice) 스캔 추가
})
public class WebConfig implements WebMvcConfigurer, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Thymeleaf 설정 (3단 구조)
     *
     * TemplateResolver → TemplateEngine → ViewResolver
     *
     * ① TemplateResolver : 뷰 이름 → 실제 파일 경로 변환
     *                      "productList" → /WEB-INF/views/productList.html
     * ② TemplateEngine   : HTML 파일을 파싱하고 렌더링
     * ③ ViewResolver     : DispatcherServlet과 Thymeleaf 연결
     */
    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setApplicationContext(applicationContext);
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(false); // 개발 중 캐시 비활성화 (운영 시 true)
        return resolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templateResolver());
        engine.setEnableSpringELCompiler(true);
        return engine;
    }

    @Bean
    public ThymeleafViewResolver viewResolver() {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(templateEngine());
        resolver.setCharacterEncoding("UTF-8");
        resolver.setContentType("text/html;charset=UTF-8");
        resolver.setOrder(1);
        return resolver;
    }

    // 정적 리소스(CSS, JS, 이미지) 처리 경로 설정
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/WEB-INF/resources/");
    }
}
