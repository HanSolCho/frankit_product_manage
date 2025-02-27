package com.frankit.product_manage.repository;


import com.frankit.product_manage.Dto.Request.ProductOptionRequestDto;
import com.frankit.product_manage.entity.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
    Optional<List<ProductOption>> findByProductId(Long productId);
}
