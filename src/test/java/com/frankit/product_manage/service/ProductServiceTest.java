package com.frankit.product_manage.service;

import com.frankit.product_manage.Dto.Request.MemberSignInRequsetDto;
import com.frankit.product_manage.Dto.Request.ProductRegisterRequestDto;
import com.frankit.product_manage.Dto.Request.ProductRequestDto;
import com.frankit.product_manage.Dto.Response.ProductSelectPagingResponseDto;
import com.frankit.product_manage.Dto.Response.ProductSelectResponseDto;
import com.frankit.product_manage.entity.*;
import com.frankit.product_manage.exception.product.ProductNotFoundException;
import com.frankit.product_manage.repository.MemberRepository;
import com.frankit.product_manage.repository.ProductOptionRepository;
import com.frankit.product_manage.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
@ExtendWith(SpringExtension.class)
class ProductServiceTest {
    @InjectMocks
    ProductService productService;
    @MockBean
    ProductRepository productRepository;
    @MockBean
    ProductOptionRepository productOptionRepository;
    private Product product;
    private ProductOption productOption;
    private ProductRequestDto productRequestDto;
    private ProductRegisterRequestDto productRegisterRequestDto;
    private ProductSelectPagingResponseDto productSelectPagingResponseDto;
    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);

        SelectOptionValue optionValue1 = SelectOptionValue.builder()
                .id(1L)
                .name("빨강")
                .build();

        ProductOption productOption = ProductOption.builder()
                .id(1L)
                .name("색상")
                .type(OptionType.SELECT)
                .price(500L)
                .selectOptionValueList(List.of(optionValue1))
                .build();

        Date currentDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());

        product = Product.builder()
                .id(1L)
                .name("product")
                .description("description")
                .price(3000L)
                .deliveryFee(1000L)
                .date(currentDate)
                .options(List.of(productOption))
                .build();

        //Dto 설정
        productRequestDto = new ProductRequestDto();
        productRequestDto.setId(1L);
        productRequestDto.setName("product");
        productRequestDto.setDescription("description");
        productRequestDto.setPrice(3000L);
        productRequestDto.setDeliveryFee(1000L);
        productRegisterRequestDto = new ProductRegisterRequestDto();
        productRegisterRequestDto.setName("product");
        productRegisterRequestDto.setDescription("description");
        productRegisterRequestDto.setPrice(3000L);
        productRegisterRequestDto.setDeliveryFee(1000L);

    }
    @Test
    void registerProduct() {
        // given:
        when(productRepository.save(ArgumentMatchers.any(Product.class))).thenReturn(product);
        // when:
        productService.registerProduct(productRegisterRequestDto);
        // then:
        verify(productRepository, times(1)).save(ArgumentMatchers.any(Product.class));
    }

    @Test
    void selectAllProduct() {
        // given:
        int page = 0;
        int size = 1;

        Pageable pageable = PageRequest.of(page, size);

        Page<Product> productPage = new PageImpl<>(List.of(product));

        when(productRepository.findAll(pageable)).thenReturn(productPage);
        // when:
        ProductSelectPagingResponseDto result = productService.selectAllProduct(page, size);

        // then:
        assertThat(result.getProducts()).hasSize(1);
        assertThat(result.getProducts().getFirst().getName()).isEqualTo("product");
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(1);
        verify(productRepository, times(1)).findAll(pageable);
    }

    @Test
    void selectProductByName() {
        // given:
        int page = 0;
        int size = 1;
        String name = "product";
        Pageable pageable = PageRequest.of(page, size);

        Page<Product> productPage = new PageImpl<>(List.of(product));

        when(productRepository.findByNameContainingIgnoreCase(name,pageable)).thenReturn(productPage);
        // when:
        ProductSelectPagingResponseDto result = productService.selectProductByName(name,page, size);

        // then:
        assertThat(result.getProducts()).hasSize(1);
        assertThat(result.getProducts().getFirst().getName()).isEqualTo("product");
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(1);
        verify(productRepository, times(1)).findByNameContainingIgnoreCase(name,pageable);
    }

    @Test
    void selectProductOverPrice() {
        // given:
        int page = 0;
        int size = 1;
        Long price = 3000L;

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = new PageImpl<>(List.of(product));
        when(productRepository.findByPriceGreaterThanEqual(price,pageable)).thenReturn(productPage);

        // when:
        ProductSelectPagingResponseDto result = productService.selectProductByGreaterPrice(price,page, size);

        // then:
        assertThat(result.getProducts()).hasSize(1);
        assertThat(result.getProducts().getFirst().getName()).isEqualTo("product");
        assertThat(result.getProducts().getFirst().getPrice()).isEqualTo(3000L);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(1);
        verify(productRepository, times(1)).findByPriceGreaterThanEqual(price,pageable);
    }

    @Test
    void selectProductLessPrice() {
        // given:
        int page = 0;
        int size = 1;
        Long price = 3000L;

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = new PageImpl<>(List.of(product));

        when(productRepository.findByPriceLessThanEqual(price,pageable)).thenReturn(productPage);

        // when:
        ProductSelectPagingResponseDto result = productService.selectProductByLessPrice(price,page, size);

        // then:
        assertThat(result.getProducts()).hasSize(1);
        assertThat(result.getProducts().getFirst().getName()).isEqualTo("product");
        assertThat(result.getProducts().getFirst().getPrice()).isEqualTo(3000L);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(1);

        verify(productRepository, times(1)).findByPriceLessThanEqual(price,pageable);
    }

    @Test
    void updateProduct() {
        // given:
        ProductRequestDto updaeDto = new ProductRequestDto();
        updaeDto.setId(1L);
        updaeDto.setName("update product");
        updaeDto.setDescription("description");
        updaeDto.setPrice(3000L);
        updaeDto.setDeliveryFee(1000L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(ArgumentMatchers.any(Product.class))).thenReturn(product);

        // when:
        productService.updateProduct(updaeDto);

        // then:
        assertThat(product.getName()).isEqualTo("update product");
        verify(productRepository).save(product);
    }

    @Test
    void updateProduct_ProductNotFound() {
        // given:
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then:
        productService.updateProduct(productRequestDto);
        verify(productRepository, never()).save(product);
    }

    @Test
    void deleteProduct() {
        // given:
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // when:
        productService.deleteProduct(productRequestDto);

        // then:
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("제품 삭제 실패 (존재하지 않는 제품)")
    void deleteProductFail() {
        // given:
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // when:
        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(productRequestDto));

        // then:
        verify(productRepository, never()).deleteById(1L);
    }
}