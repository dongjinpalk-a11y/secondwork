package kr.ac.hansung.cse.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import kr.ac.hansung.cse.model.CategoryForm;
import kr.ac.hansung.cse.service.CategoryService;
import kr.ac.hansung.cse.exception.DuplicateCategoryException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "categoryList";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("categoryForm", new CategoryForm());
        return "categoryForm";
    }

    @PostMapping("/create")
    public String createCategory(
            @Valid @ModelAttribute("categoryForm") CategoryForm categoryForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "categoryForm";
        }

        try {
            categoryService.createCategory(categoryForm.getName());
            redirectAttributes.addFlashAttribute("successMessage", "등록 완료");
        } catch (DuplicateCategoryException e) {
            bindingResult.rejectValue("name", "duplicate", e.getMessage());
            return "categoryForm";
        }

        return "redirect:/categories";
    }

    @PostMapping("/{id}/delete")
    public String deleteCategory(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "삭제 완료");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/categories";
    }
}