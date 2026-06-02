package kr.ac.hansung.controller;

import jakarta.validation.Valid;
import kr.ac.hansung.dto.ProductDto;
import kr.ac.hansung.entity.Product;
import kr.ac.hansung.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;


    @GetMapping
    public String list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        // 1. id를 기준으로 오름차순 정렬 조건을 설정하여 페이지 요청 객체 생성
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id"));

        // 2. 검색어가 공백문자("")로 들어올 경우 null로 정규화 처리 (URL을 깔끔하게 유지하기 위함)
        String normalizedKeyword = (keyword != null && !keyword.isBlank()) ? keyword : null;

        Page<Product> productPage;
        if (normalizedKeyword != null) {
            // 검색어가 존재하는 경우: 키워드 검색 페이징 조회 실행
            productPage = productService.searchProducts(normalizedKeyword, pageRequest);
        } else {
            // 검색어가 없는 경우: 전체 목록 페이징 조회 실행
            productPage = productService.getProducts(pageRequest);
        }

        // 3. Thymeleaf 뷰에서 페이징 바와 목록을 그릴 수 있도록 Model에 바인딩
        model.addAttribute("productPage", productPage);
        model.addAttribute("keyword", normalizedKeyword);

        return "products/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.findById(id));
        return "products/detail";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("product", new ProductDto());
        return "products/add";
    }

    @PostMapping
    public String save(@ModelAttribute ProductDto dto) {
        productService.save(dto);
        return "redirect:/products";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        productService.deleteById(id);
        return "redirect:/products";
    }

    @GetMapping("/{id}/edit")
    public String editProductForm(@PathVariable Long id, Model model) {
        Product product=productService.findById(id);

        ProductDto dto=new ProductDto();
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setDescription(product.getDescription());

        model.addAttribute("productDto",dto);
        model.addAttribute("productId",id);

        return "products/edit";
    }
    @PostMapping("/{id}/edit")
    public String editProduct(@PathVariable Long id,
                              @Valid @ModelAttribute("productDto") ProductDto productDto,
                              org.springframework.validation.BindingResult bindingResult,
                              Model model,
                              org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {

        // 검증 에러가 있는 경우 수정 폼으로 다시 리턴
        if (bindingResult.hasErrors()) {
            model.addAttribute("productId", id);
            return "products/edit";
        }

        productService.updateProduct(id, productDto);

        // 목록 화면에서 띄워줄 성공 알림 메시지 등록
        ra.addFlashAttribute("successMessage", "상품이 성공적으로 수정되었습니다.");

        return "redirect:/products";
    }
}