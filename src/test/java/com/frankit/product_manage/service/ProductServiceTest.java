package com.frankit.product_manage.service;

import com.frankit.product_manage.Dto.Request.MemberSignInRequsetDto;
import com.frankit.product_manage.Dto.Request.ProductRegisterRequestDto;
import com.frankit.product_manage.Dto.Request.ProductRequestDto;
import com.frankit.product_manage.Dto.Response.ProductSelectPagingResponseDto;
import com.frankit.product_manage.Dto.Response.ProductSelectResponseDto;
import com.frankit.product_manage.entity.*;
import com.frankit.product_manage.exception.product.ProductNotFoundException;
import com.frankit.product_manage.repository.MemberRepository;
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
    private Product product;
    private ProductOption productOption;
    private ProductRequestDto productRequestDto;
    private ProductRegisterRequestDto productRegisterRequestDto;
    private ProductSelectPagingResponseDto productSelectPagingResponseDto;
    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        // mock 응답 데이터 준비
        SelectOptionValue optionValue1 = SelectOptionValue.builder()
                .id(1L)
                .name("빨강")
                .build();

        ProductOption productOption = ProductOption.builder()
                .id(1L)
                .name("색상")
                .type(OptionType.SELECT)  // 예: 색상 옵션은 선택 타입
                .price(500L)  // 옵션 추가 금액
                .selectOptionValueList(List.of(optionValue1))  // 선택 가능한 옵션 값 목록
                .build();

        Date currentDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());

        product = Product.builder()
                .id(1L)
                .name("product")
                .description("description")
                .price(3000L)
                .deliveryFee(1000L)
                .date(currentDate) // 현재 날짜 받아서 넣기
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
        // given: 테스트를 위한 mock 객체 설정이 이미 되어 있음
        when(productRepository.save(ArgumentMatchers.any(Product.class))).thenReturn(product);
        // when: registerProduct 메서드 호출
        productService.registerProduct(productRegisterRequestDto);
        // then: productRepository.save() 메소드가 호출되었는지 검증
        verify(productRepository, times(1)).save(ArgumentMatchers.any(Product.class)); // save 메소드가 한 번 호출되었는지 확인
    }

    @Test
    void selectAllProduct() {
        // given: 페이지 정보를 설정
        int page = 0;
        int size = 1;

        Pageable pageable = PageRequest.of(page, size);

        // productRepository에서 반환할 Page 객체 설정
        Page<Product> productPage = new PageImpl<>(List.of(product));

        // when: productRepository.findAll을 mock하여 반환값 설정
        when(productRepository.findAll(pageable)).thenReturn(productPage);

        // when: service 메서드 호출
        ProductSelectPagingResponseDto result = productService.selectAllProduct(page, size);

        // then: 반환된 결과 검증
        assertThat(result.getProducts()).hasSize(1);  // 한 개의 product가 반환되어야 함
        assertThat(result.getProducts().getFirst().getName()).isEqualTo("product");
        assertThat(result.getNumber()).isEqualTo(0);  // 페이지 번호 검증
        assertThat(result.getSize()).isEqualTo(1);  // 페이지 사이즈 검증

        // productRepository.findAll이 한 번 호출되었는지 검증
        verify(productRepository, times(1)).findAll(pageable);
    }

    @Test
    void selectProductOverPrice() {
        // given: 페이지 정보를 설정
        int page = 0;
        int size = 1;
        Long price = 3000L;

        Pageable pageable = PageRequest.of(page, size);

        // productRepository에서 반환할 Page 객체 설정
        Page<Product> productPage = new PageImpl<>(List.of(product));

        // when: productRepository.findAll을 mock하여 반환값 설정
        when(productRepository.findByPriceGreaterThanEqual(price,pageable)).thenReturn(productPage);

        // when: service 메서드 호출
        ProductSelectPagingResponseDto result = productService.selectProductByPrice(price,page, size);

        // then: 반환된 결과 검증
        assertThat(result.getProducts()).hasSize(1);  // 한 개의 product가 반환되어야 함
        assertThat(result.getProducts().getFirst().getName()).isEqualTo("product");
        assertThat(result.getProducts().getFirst().getPrice()).isEqualTo(3000L);  // 페이지 번호 검증
        assertThat(result.getNumber()).isEqualTo(0);  // 페이지 번호 검증
        assertThat(result.getSize()).isEqualTo(1);  // 페이지 사이즈 검증

        // productRepository.findAll이 한 번 호출되었는지 검증
        verify(productRepository, times(1)).findByPriceGreaterThanEqual(price,pageable);
    }

    @Test
    void updateProduct() {
        // given: 제품이 존재하는 경우
        ProductRequestDto updaeDto = new ProductRequestDto();
        updaeDto.setId(1L);
        updaeDto.setName("update product");
        updaeDto.setDescription("description");
        updaeDto.setPrice(3000L);
        updaeDto.setDeliveryFee(1000L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(ArgumentMatchers.any(Product.class))).thenReturn(product);

        // when: updateProduct 메소드 호출
        productService.updateProduct(updaeDto);

        // then: 제품의 이름, 설명, 가격, 배송비가 업데이트 되었는지 확인
        assertThat(product.getName()).isEqualTo("update product");

        // then: save 메소드가 호출되었는지 검증
        verify(productRepository).save(product);
    }

    @Test
    void updateProduct_ProductNotFound() {
        // given: 제품이 존재하지 않는 경우
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then: 존재하지 않는 제품일 때 아무 작업도 하지 않고 예외가 발생하지 않음을 확인
        productService.updateProduct(productRequestDto);

        // then: save 메소드가 호출되지 않아야 한다.
        verify(productRepository, never()).save(product);
    }

    @Test
    void deleteProduct() {
        // given: 제품이 존재하는 경우
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // when: deleteProduct 메서드 호출
        productService.deleteProduct(productRequestDto);

        // then: deleteById 메서드가 호출되었는지 검증
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("제품 삭제 실패 (존재하지 않는 제품)")
    void deleteProductFail() {
        // given: 제품이 존재하지 않는 경우
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // when: deleteProduct 메서드 호출
        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(productRequestDto));

        // then: deleteById 메서드가 호출되지 않아야 한다
        verify(productRepository, never()).deleteById(1L);
    }
}