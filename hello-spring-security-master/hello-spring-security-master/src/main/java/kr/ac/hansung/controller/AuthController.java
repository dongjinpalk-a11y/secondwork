package kr.ac.hansung.controller;

import kr.ac.hansung.dto.UserDto;
import kr.ac.hansung.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("user", new UserDto());
        return "signup";
    }

    @PostMapping("/signup")
    public String signupProcess(@ModelAttribute("user") UserDto dto, Model model) {
        if (userService.existsByEmail(dto.getEmail())) {
            model.addAttribute("emailExists", true);
            return "signup";
        }
        userService.signup(dto);
        return "redirect:/login?registered";
    }
}
