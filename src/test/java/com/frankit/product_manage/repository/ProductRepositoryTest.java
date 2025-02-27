package com.frankit.product_manage.repository;

import com.frankit.product_manage.entity.OptionType;
import com.frankit.product_manage.entity.Product;
import com.frankit.product_manage.entity.ProductOption;
import com.frankit.product_manage.exception.ProductException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {
    @Autowired
    ProductRepository productRepository;

    @Test
    @DisplayName("상품 등록 Test")
    void saveProductTest(){
        /*
        given
         */
        Date currentDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Product product = Product.builder()
                .name("name")
                .description("descroption")
                .price(3000l)
                .deliveryFee(1000l)
                .date(currentDate) // 현재 날짜 받아서 넣기
                .build();
        productRepository.save(product);

        /*
        when
         */
        Product result = productRepository.save(product);

        /*
        then
         */
        assertThat(result.getName()).isEqualTo(product.getName());

    }
    @Test
    @DisplayName("상품 조회 Test")
    void selectProductTest(){
        /*
        given
         */
        Date currentDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Product product = Product.builder()
                .name("name")
                .description("descroption")
                .price(3000l)
                .deliveryFee(1000l)
                .date(currentDate) // 현재 날짜 받아서 넣기
                .build();
        productRepository.save(product);

        /*
        when
         */
        List<Product> result = productRepository.findAll();

        /*
        then
         */
        assertThat(result.size()).isEqualTo(1);
        assertThat(product.getId()).isEqualTo(result.get(0).getId());

    }

    @Test
    @DisplayName("상품 변경 Test")
    void updateProductTest(){
        /*
        given
         */
        Date currentDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Product product = Product.builder()
                .name("name")
                .description("descroption")
                .price(3000l)
                .deliveryFee(1000l)
                .date(currentDate) // 현재 날짜 받아서 넣기
                .build();
        Product product2 = Product.builder()
                .name("name2")
                .description("descroption")
                .price(3000l)
                .deliveryFee(1000l)
                .date(currentDate) // 현재 날짜 받아서 넣기
                .build();
        productRepository.save(product);
        productRepository.save(product2);

        List<Product> result = productRepository.findAll();

        assertThat(product.getId()).isEqualTo(result.get(0).getId());

        Product updateProduct = result.get(0);
        updateProduct.setPrice(2000l);
        productRepository.save(updateProduct);

        /*
        when
         */
        List<Product> updateResult = productRepository.findAll();
        /*
        then
         */
        assertThat(updateProduct.getPrice()).isEqualTo(updateResult.get(0).getPrice());
        assertThat(3000l).isNotEqualTo(updateResult.get(0).getPrice());

    }

    @Test
    @DisplayName("상품 삭제 Test")
    void deleteProductTest(){
 /*
        given
         */
        Date currentDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Product product = Product.builder()
                .name("name")
                .description("descroption")
                .price(3000l)
                .deliveryFee(1000l)
                .date(currentDate) // 현재 날짜 받아서 넣기
                .build();
        Product product2 = Product.builder()
                .name("name2")
                .description("descroption")
                .price(3000l)
                .deliveryFee(1000l)
                .date(currentDate) // 현재 날짜 받아서 넣기
                .build();
        productRepository.save(product);
        productRepository.save(product2);


        /*
        when
         */
        productRepository.deleteById(2l);
        List<Product> deleteResult = productRepository.findAll();

        /*
        then
         */
        assertThat(deleteResult.size()).isEqualTo(1);
        assertThat(product.getId()).isEqualTo(deleteResult.get(0).getId());

    }
}