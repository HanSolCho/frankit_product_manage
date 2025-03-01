package com.frankit.product_manage.repository;

import com.frankit.product_manage.entity.OptionType;
import com.frankit.product_manage.entity.Product;
import com.frankit.product_manage.entity.ProductOption;
import com.frankit.product_manage.exception.product.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
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
class ProductOptionRepositoryTest {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductOptionRepository productOptionRepository;
    private Product selectProduct;

    @BeforeEach
    void setUp() {
        // given
        Date currentDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());

        Product product = Product.builder()
                .name("name")
                .description("description")
                .price(3000L)
                .deliveryFee(1000L)
                .date(currentDate) // 현재 날짜 받아서 넣기
                .build();
        productRepository.save(product);

        selectProduct = productRepository.findById(1L)
                .orElseThrow(ProductNotFoundException::new);

    }

    @Test
    @DisplayName("상품 옵션 등록 Test")
    void saveProductOptionTest(){
        /*
        given
         */
        ProductOption productOption = ProductOption.builder()
                .name("name")
                .type(OptionType.SELECT)
                .price(3000L)
                .product(selectProduct)
                .build();

        /*
        when
         */
        ProductOption result = productOptionRepository.save(productOption);

        /*
        then
         */
        assertThat(result.getName()).isEqualTo(productOption.getName());

    }

    @Test
    @DisplayName("상품 옵션 조회 Test")
    void selectProductOptionTest(){
        /*
        given
         */
        ProductOption productOption = ProductOption.builder()
                .name("name")
                .type(OptionType.SELECT)
                .price(3000L)
                .product(selectProduct)
                .build();

        productOptionRepository.save(productOption);

        /*
        when
         */
        List<ProductOption> result = productOptionRepository.findAll();

        /*
        then
         */
        assertThat(result.size()).isEqualTo(1);
        assertThat(productOption.getId()).isEqualTo(result.getFirst().getId());

    }

    @Test
    @DisplayName("상품 옵션 변경 Test")
    void updateProductOptionTest(){
        /*
        given
         */
        ProductOption productOption = ProductOption.builder()
                .name("name")
                .type(OptionType.SELECT)
                .price(3000L)
                .product(selectProduct)
                .build();
        ProductOption productOption2 = ProductOption.builder()
                .name("name2")
                .type(OptionType.SELECT)
                .price(3000L)
                .product(selectProduct)
                .build();

        productOptionRepository.save(productOption);
        productOptionRepository.save(productOption2);

        List<ProductOption> result = productOptionRepository.findAll();

        assertThat(productOption.getId()).isEqualTo(result.getFirst().getId());

        ProductOption updateProductOption = result.getFirst();
        updateProductOption.setPrice(2000L);
        productOptionRepository.save(updateProductOption);

        /*
        when
         */
        List<ProductOption> updateResult = productOptionRepository.findAll();
        /*
        then
         */
        assertThat(updateProductOption.getPrice()).isEqualTo(updateResult.getFirst().getPrice());
        assertThat(3000L).isNotEqualTo(updateResult.getFirst().getPrice());

    }

    @Test
    @DisplayName("상품 옵션 삭제 Test")
    void deleteProductOptionTest(){
 /*
        given
         */
        ProductOption productOption = ProductOption.builder()
                .name("name")
                .type(OptionType.SELECT)
                .price(3000L)
                .product(selectProduct)
                .build();

        ProductOption productOption2 = ProductOption.builder()
                .name("name2")
                .type(OptionType.SELECT)
                .price(3000L)
                .product(selectProduct)
                .build();

        productOptionRepository.save(productOption);
        productOptionRepository.save(productOption2);


        /*
        when
         */
        productOptionRepository.deleteById(2L);
        List<ProductOption> deleteResult = productOptionRepository.findAll();

        /*
        then
         */
        assertThat(deleteResult.size()).isEqualTo(1);
        assertThat(productOption.getId()).isEqualTo(deleteResult.getFirst().getId());

    }
    @Test
    @DisplayName("상품 옵션 상품 번호 별 조회")
    void findByProductId() {
        /*
        given
         */
        ProductOption productOption = ProductOption.builder()
                .name("name")
                .type(OptionType.SELECT)
                .price(3000L)
                .product(selectProduct)
                .build();

        productOptionRepository.save(productOption);

        /*
        when
         */
        Optional<List<ProductOption>> result = productOptionRepository.findByProductId(1L);

        /*
        then
         */
        assertThat(productOption.getName()).isEqualTo(result.get().getFirst().getName());
    }
}