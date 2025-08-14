package com.lightningbid.item.service;

import com.lightningbid.item.domain.repository.CategoryRepository;
import com.lightningbid.item.exception.CategoryIdNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public String findCategoryNameById(Long categoryId) {

        return categoryRepository.findById(categoryId)
                .orElseThrow(CategoryIdNotFoundException::new)
                .getName();
    }
}
