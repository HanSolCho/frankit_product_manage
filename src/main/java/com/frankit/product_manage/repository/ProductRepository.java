package com.frankit.product_manage.repository;


import com.frankit.product_manage.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;



public interface ProductRepository extends JpaRepository<Product, Long> {
}
