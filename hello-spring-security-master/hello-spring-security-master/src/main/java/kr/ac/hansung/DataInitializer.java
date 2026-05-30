package kr.ac.hansung;

import kr.ac.hansung.entity.Product;
import kr.ac.hansung.entity.Role;
import kr.ac.hansung.entity.User;
import kr.ac.hansung.repository.ProductRepository;
import kr.ac.hansung.repository.RoleRepository;
import kr.ac.hansung.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProductRepository productRepository;

    @Override
    public void run(ApplicationArguments args) {
        Role userRole = roleRepository.findByName("ROLE_USER")
            .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
            .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));

        if (!userRepository.existsByEmail("admin@hansung.ac.kr")) {
            User admin = new User();
            admin.setEmail("admin@hansung.ac.kr");
            admin.setPassword(passwordEncoder.encode("admin1234"));
            admin.getRoles().add(userRole);
            admin.getRoles().add(adminRole);
            userRepository.save(admin);
            log.info("초기 관리자 계정 생성: admin@hansung.ac.kr / admin1234");
        }

        if (productRepository.count() == 0) {
            productRepository.save(new Product("Spring Boot 4 완벽 가이드", 35000, "Spring Boot 4 + JPA + Security 실습서", 50));
            productRepository.save(new Product("Spring Security 7 핵심 원리", 28000, "세션·JWT·OAuth2 기반 보안 구현", 30));
            productRepository.save(new Product("JPA 프로그래밍 실전", 32000, "Hibernate 7 기반 ORM 마스터", 25));
            productRepository.save(new Product("Thymeleaf 완전 정복", 22000, "서버사이드 템플릿 엔진 가이드", 40));
            log.info("샘플 상품 4건 생성 완료");
        }
    }
}
