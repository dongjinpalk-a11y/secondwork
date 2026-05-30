package kr.ac.hansung.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @GetMapping("/")
    public String root() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());

        // 로그인한 사용자의 권한 목록에서 화면에 보여줄 역할명만 추출한다.
        // Spring Security 7은 ROLE_USER 같은 역할 외에 MFA(2단계 인증) 관련 권한도 함께 넣어두기 때문에
        // "ROLE_" 로 시작하는 것만 골라내고, 앞의 "ROLE_" 접두사는 제거하여 화면에 전달한다.
        // ex) 권한 목록: ["ROLE_USER", "ROLE_ADMIN", "MFA_FACTOR"]
        //     → "ROLE_" 로 시작하는 것만 필터링: ["ROLE_USER", "ROLE_ADMIN"]
        //     → "ROLE_" 제거: ["USER", "ADMIN"]
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.replace("ROLE_", ""))
                .toList();
        model.addAttribute("roles", roles);
        return "home";
    }
}
