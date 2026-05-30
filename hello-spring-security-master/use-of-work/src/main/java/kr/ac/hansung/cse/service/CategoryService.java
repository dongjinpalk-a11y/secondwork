package kr.ac.hansung.cse.service;

import kr.ac.hansung.cse.exception.DuplicateCategoryException;
import kr.ac.hansung.cse.model.Category;
import kr.ac.hansung.cse.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Transactional
    public Category createCategory(String name){
        categoryRepository.findByName(name)
                .ifPresent(c -> {
                    throw new DuplicateCategoryException(name);
                });
        return categoryRepository.save(new Category(name));
    }

    @Transactional
    public void deleteCategory(Long id) {
        long count=categoryRepository.countProductsByCategoryId(id);

        if(count > 0) {
            throw new IllegalStateException(
                    "상품" + count + "개가 연결되어 있어 삭제할 수 없습니다."
            );
        }
        categoryRepository.delete(id);
    }
}
