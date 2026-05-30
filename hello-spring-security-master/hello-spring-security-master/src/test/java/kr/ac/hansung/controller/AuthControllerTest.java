package kr.ac.hansung.controller;

import kr.ac.hansung.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @SpringBootTest : 전체 Application Context 로드 (Controller, Service, Security, JPA 등 모든 Bean 등록)
//                   Controller는 Spring Bean이므로 Spring Context 없이는 테스트 불가
//                   webEnvironment 기본값 = MOCK (실제 Tomcat 없이 가짜 웹 환경 구성)
// @MockitoBean    : Spring Context에 등록된 특정 Bean을 Mock으로 교체
// MockMvc         : 실제 서버 없이 HTTP 요청/응답 테스트 (Security 필터 포함)
@SpringBootTest
@DisplayName("AuthController 테스트")
class AuthControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @MockitoBean
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(wac)
            .apply(SecurityMockMvcConfigurers.springSecurity())  // Spring Security 필터 적용
            .build();
    }

    @Test
    @WithAnonymousUser
    @DisplayName("GET /login - 로그인 페이지 정상 응답 (200)")
    void loginPage_get_returns200() throws Exception {
        mockMvc.perform(get("/login"))
            .andExpect(status().isOk())
            .andExpect(view().name("login"));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("GET /signup - 회원가입 페이지 정상 응답 + model에 user 객체 포함")
    void signupPage_get_returns200() throws Exception {
        mockMvc.perform(get("/signup"))
            .andExpect(status().isOk())
            .andExpect(view().name("signup"))
            .andExpect(model().attributeExists("user"));
    }

    @Test
    @DisplayName("POST /signup - 신규 이메일 회원가입 성공 후 로그인 페이지로 리다이렉트")
    void signupProcess_newEmail_redirectsToLogin() throws Exception {
        given(userService.existsByEmail("new@hansung.ac.kr")).willReturn(false);

        mockMvc.perform(post("/signup")
                .with(csrf())  // CSRF 토큰 포함 (없으면 403)
                .param("email", "new@hansung.ac.kr")
                .param("password", "password123"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login?registered"));
    }

    @Test
    @DisplayName("POST /signup - 중복 이메일 회원가입 시 signup 페이지로 복귀 + emailExists 플래그")
    void signupProcess_existingEmail_returnsSignupPage() throws Exception {
        given(userService.existsByEmail("dup@hansung.ac.kr")).willReturn(true);

        mockMvc.perform(post("/signup")
                .with(csrf())
                .param("email", "dup@hansung.ac.kr")
                .param("password", "password123"))
            .andExpect(status().isOk())
            .andExpect(view().name("signup"))
            .andExpect(model().attribute("emailExists", true));
    }

    @Test
    @DisplayName("POST /signup - CSRF 토큰 없으면 403 (Spring Security 기본 보호)")
    void signupProcess_noCsrf_returns403() throws Exception {
        mockMvc.perform(post("/signup")
                .param("email", "test@hansung.ac.kr")
                .param("password", "password123"))
            .andExpect(status().isForbidden());
    }
}
