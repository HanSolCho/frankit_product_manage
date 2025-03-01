package com.frankit.product_manage.repository;


import com.frankit.product_manage.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;


public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByPriceGreaterThanEqual(Long price, Pageable pageable);
}
