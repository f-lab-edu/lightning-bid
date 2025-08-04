package com.lightningbid.item.domain.repository;

import com.lightningbid.item.domain.model.Category;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CategoryRepository {

    public Optional<Category> findById(Long id) {
        return Optional.ofNullable(Category.builder().id(id).name("카테고리명").build());
    }
}
