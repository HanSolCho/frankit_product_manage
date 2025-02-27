package com.frankit.product_manage.repository;

import com.frankit.product_manage.entity.OptionType;
import com.frankit.product_manage.entity.Product;
import com.frankit.product_manage.entity.ProductOption;
import com.frankit.product_manage.entity.SelectOptionValue;
import com.frankit.product_manage.exception.ProductException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class SelectOptionValueRepositoryTest {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductOptionRepository productOptionRepository;
    @Autowired
    SelectOptionValueRepository selectOptionValueRepository;
    private Product product; // 공통으로 사용할 Product 객체 선언
    private Product selectProduct;
    private ProductOption productOption;
    private ProductOption selectProductOption;
    @BeforeEach
    void setUp() {
        // given
        Date currentDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        product = Product.builder()
                .name("name")
                .description("description")
                .price(3000L)
                .deliveryFee(1000L)
                .date(currentDate) // 현재 날짜 받아서 넣기
                .build();
        productRepository.save(product);

        selectProduct = productRepository.findById(1l)
                .orElseThrow(() -> new ProductException("Product not found"));

        productOption = ProductOption.builder()
                .name("name")
                .type(OptionType.SELECT)
                .price(3000l)
                .product(selectProduct)
                .build();

        productOptionRepository.save(productOption);

        selectProductOption = productOptionRepository.findById(1l)
                .orElseThrow(() -> new ProductException("Product not found"));

    }
    @Test
    @DisplayName("상품 옵션 값 등록 Test")
    void saveSelectOptionValueTest(){
        /*
        given
         */
        SelectOptionValue selectOptionValue = SelectOptionValue.builder()
                .name("name")
                .productOption(selectProductOption)
                .build();
        /*
        when
         */
        SelectOptionValue result = selectOptionValueRepository.save(selectOptionValue);

        /*
        then
         */
        assertThat(result.getName()).isEqualTo(selectOptionValue.getName());

    }
    @Test
    @DisplayName("상품 옵션 값 조회 Test")
    void selectSelectOptionValueTest(){
        /*
        given
         */
        SelectOptionValue selectOptionValue = SelectOptionValue.builder()
                .name("name")
                .productOption(productOption)
                .build();
        selectOptionValueRepository.save(selectOptionValue);

        /*
        when
         */
        List<SelectOptionValue> result = selectOptionValueRepository.findAll();

        /*
        then
         */
        assertThat(result.size()).isEqualTo(1);
        assertThat(selectOptionValue.getId()).isEqualTo(result.get(0).getId());

    }

    @Test
    @DisplayName("상품 옵션 값 변경 Test")
    void updateProductTest(){
        /*
        given
         */
        SelectOptionValue selectOptionValue = SelectOptionValue.builder()
                .name("Blue")
                .productOption(productOption)
                .build();
        selectOptionValueRepository.save(selectOptionValue);

        List<SelectOptionValue> result = selectOptionValueRepository.findAll();

        assertThat(selectOptionValue.getId()).isEqualTo(result.get(0).getId());

        SelectOptionValue updateSelectOptionValue = result.get(0);
        updateSelectOptionValue.setName("Red");
        selectOptionValueRepository.save(updateSelectOptionValue);

        /*
        when
         */
        List<SelectOptionValue> updateResult = selectOptionValueRepository.findAll();
        /*
        then
         */
        assertThat(updateSelectOptionValue.getName()).isEqualTo(updateResult.get(0).getName());
        assertThat("Blue").isNotEqualTo(updateResult.get(0).getName());

    }

    @Test
    @DisplayName("상품 옵션 값 삭제 Test")
    void deleteProductTest(){
 /*
        given
         */
        SelectOptionValue selectOptionValue = SelectOptionValue.builder()
                .name("Blue")
                .productOption(productOption)
                .build();
        SelectOptionValue selectOptionValue2 = SelectOptionValue.builder()
                .name("Red")
                .productOption(productOption)
                .build();
        selectOptionValueRepository.save(selectOptionValue);
        selectOptionValueRepository.save(selectOptionValue2);


        /*
        when
         */
        selectOptionValueRepository.deleteById(2l);
        List<SelectOptionValue> deleteResult = selectOptionValueRepository.findAll();

        /*
        then
         */
        assertThat(deleteResult.size()).isEqualTo(1);
        assertThat(selectOptionValue.getName()).isEqualTo(deleteResult.get(0).getName());
    }
}