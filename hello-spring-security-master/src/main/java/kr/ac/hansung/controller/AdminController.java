package kr.ac.hansung.controller;

import kr.ac.hansung.repository.ProductRepository;
import kr.ac.hansung.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "관리자 대시보드");
        model.addAttribute("userCount", userRepository.count());
        model.addAttribute("productCount", productRepository.count());
        model.addAttribute("outOfStockCount", productRepository.countByStockEquals(0));
        return "admin/dashboard";
    }
}
