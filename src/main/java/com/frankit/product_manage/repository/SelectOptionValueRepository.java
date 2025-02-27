package com.frankit.product_manage.repository;

import com.frankit.product_manage.entity.SelectOptionValue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SelectOptionValueRepository extends JpaRepository<SelectOptionValue, Long> {
    void deleteByProductOptionId(Long productOptionId);
}
