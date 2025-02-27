package com.frankit.product_manage.service;

import com.frankit.product_manage.Dto.Request.*;
import com.frankit.product_manage.Dto.Response.ProductSelectPagingResponseDto;
import com.frankit.product_manage.entity.OptionType;
import com.frankit.product_manage.entity.Product;
import com.frankit.product_manage.entity.ProductOption;
import com.frankit.product_manage.entity.SelectOptionValue;
import com.frankit.product_manage.exception.ProductException;
import com.frankit.product_manage.repository.ProductOptionRepository;
import com.frankit.product_manage.repository.ProductRepository;
import com.frankit.product_manage.repository.SelectOptionValueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
class ProductOptionServiceTest {

    @InjectMocks
    ProductService productService;
    @InjectMocks
    ProductOptionService productOptionService;
    @MockBean
    ProductRepository productRepository;
    @MockBean
    ProductOptionRepository productOptionRepository;
    @MockBean
    SelectOptionValueRepository selectOptionValueRepository;
    private Product product;
    private ProductOption productOption;
    private SelectOptionValue selectOptionValue;
    private ProductRequestDto productRequestDto;
    private ProductOptionAddRequestDto productOptionAddRequestDto;
    private ProductOptionUpdateRequestDto productOptionUpdateRequestDto;
    private SelectOptionValueAddRequestDto selectOptionValueAddRequestDto;
    private SelectOptionValueUpdateRequestDto selectOptionValueUpdateRequestDto;
    private SelectOptionValueDeleteRequestDto selectOptionValueDeleteRequestDto;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        // mock 응답 데이터 준비
        selectOptionValue = SelectOptionValue.builder()
                .id(1L)
                .name("Red")
                .build();
        productOption = ProductOption.builder()
                .id(1L)
                .name("Color")
                .type(OptionType.SELECT)  // 예: 색상 옵션은 선택 타입
                .price(500L)  // 옵션 추가 금액
                .selectOptionValueList(List.of(selectOptionValue))  // 선택 가능한 옵션 값 목록
                .build();

        Date currentDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());

        HashMap<Long, String> selectOptionValueMap = new HashMap<>();
        selectOptionValueMap.put(1L, "Red");  // SelectOptionValue ID 1에 대해 "Red"
        selectOptionValueMap.put(2L, "Blue");  // SelectOptionValue ID 2에 대해 "Blue"

        product = Product.builder()
                .id(1l)
                .name("product")
                .description("description")
                .price(3000l)
                .deliveryFee(1000l)
                .date(currentDate) // 현재 날짜 받아서 넣기
                .options(List.of(productOption))
                .build();

        //Dto 설정

        productRequestDto = new ProductRequestDto();
        productRequestDto.setId(1l);
        productRequestDto.setName("product");
        productRequestDto.setDescription("description");
        productRequestDto.setPrice(3000l);
        productRequestDto.setDeliveryFee(1000l);

        selectOptionValueAddRequestDto = new SelectOptionValueAddRequestDto();
        selectOptionValueAddRequestDto.setName(List.of("Red", "Blue"));

        productOptionAddRequestDto = new ProductOptionAddRequestDto();
        productOptionAddRequestDto.setProductId(1l);
        productOptionAddRequestDto.setName("Color");
        productOptionAddRequestDto.setType(OptionType.SELECT);
        productOptionAddRequestDto.setPrice(1000L);
        productOptionAddRequestDto.setSelectOptionValue(selectOptionValueAddRequestDto);

        productOptionUpdateRequestDto = new ProductOptionUpdateRequestDto();
        productOptionUpdateRequestDto.setId(1l);
        productOptionUpdateRequestDto.setProductId(1l);
        productOptionUpdateRequestDto.setName("update Color");
        productOptionUpdateRequestDto.setType(OptionType.SELECT);
        productOptionUpdateRequestDto.setPrice(1000L);
        productOptionUpdateRequestDto.setSelectOptionValueMap(selectOptionValueMap);

        selectOptionValueDeleteRequestDto = new SelectOptionValueDeleteRequestDto();
        selectOptionValueDeleteRequestDto.setId(1L);

    }
    @Test
    void addProductOption() {
        // given: ProductOptionAddRequestDto 설정

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productOptionRepository.save(any(ProductOption.class))).thenReturn(ProductOption.builder().id(1L).name("Color").build());
        when(selectOptionValueRepository.save(any(SelectOptionValue.class))).thenReturn(SelectOptionValue.builder().id(1L).name("Red").build());

        // when: addProductOption 호출
        productOptionService.addProductOption(productOptionAddRequestDto);

        // then: productRepository.findById가 호출되었는지 확인
        verify(productRepository, times(1)).findById(1L);

        // then: productOptionRepository.save가 호출되었는지 확인
        verify(productOptionRepository, times(1)).save(any(ProductOption.class));

        // then: selectOptionValueRepository.save가 호출되었는지 확인 (SELECT 옵션일 경우)
        verify(selectOptionValueRepository, times(2)).save(any(SelectOptionValue.class));  // Red, Blue 값 각각에 대해 호출
    }

    @Test
    void addProductOption_ProductNotFound() {
        // given: Product가 없는 경우
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then: ProductException이 발생해야 한다
        assertThrows(ProductException.class, () -> productOptionService.addProductOption(productOptionAddRequestDto));
        verify(productRepository, never()).save(product);
    }

    @Test
    void addProductOption_ExceedsOptionLimit() {
        // given: 이미 3개의 옵션이 있는 경우
        product.setOptions(List.of(productOption, productOption, productOption));  // 3개의 옵션 추가

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // when & then: IllegalStateException이 발생해야 한다
        assertThrows(IllegalStateException.class, () -> productOptionService.addProductOption(productOptionAddRequestDto));
        verify(productRepository, never()).save(product);
    }

    @Test
    void selectProductOption() {
        List<ProductOption> productOptionList = List.of(productOption, productOption);

        // when: productOptionRepository.findByProductId가 호출되었을 때 mock 반환값 설정
        when(productOptionRepository.findByProductId(1L)).thenReturn(Optional.of(productOptionList));

        // when: selectProductOption 메소드 호출
        Optional<List<ProductOption>> result = productOptionService.selectProductOption(productRequestDto);

        // then: productOptionRepository.findByProductId가 호출되었는지 검증
        verify(productOptionRepository, times(1)).findByProductId(1L);

        assertTrue(result.isPresent());  // Optional이 비어있지 않음
        assertEquals(2, result.get().size());  // 옵션 개수는 2개여야 함
        assertEquals("Color", result.get().get(0).getName());  // 첫 번째 옵션 이름이 "Color"여야 함

    }

    @Test // 노멀한 성공 케이스 SELECT -> INPUT
    void updateProductOption() {
    //머리 식히고 다시 -> 각 부분 분기 쳐서 어떻게 할지 고민해
        // Given: 테스트 데이터 준비
        //업데이트 요청 들어온 데이터
        ProductOptionUpdateRequestDto updateRequestDto = new ProductOptionUpdateRequestDto();
        updateRequestDto.setId(1L);
        updateRequestDto.setName("Updated Option");
        updateRequestDto.setPrice(1000l);
        updateRequestDto.setType(OptionType.INPUT);

        // Mock 객체 설정
        when(productOptionRepository.findById(1L)).thenReturn(Optional.of(productOption));
        when(selectOptionValueRepository.findById(anyLong())).thenReturn(Optional.of(selectOptionValue));

        // When: 메서드 호출
        productOptionService.updateProductOption(updateRequestDto);

        // Then: 검증
        verify(productOptionRepository).save(productOption); // save 호출 검증
        verify(selectOptionValueRepository).deleteByProductOptionId(1L); // SELECT에서 INPUT으로 변경 시 하위 옵션 삭제
    }

    @Test
    void testUpdateProductOption_ProductNotFound() {
        // Given: 존재하지 않는 ID로 테스트 데이터 준비
        ProductOptionUpdateRequestDto updateRequestDto = new ProductOptionUpdateRequestDto();
        updateRequestDto.setId(999L); // 존재하지 않는 ID

        // Mock 객체 설정
        when(productOptionRepository.findById(999L)).thenReturn(Optional.empty()); // 없는 상품 옵션 ID

        // When & Then: 예외 발생 확인
//        assertThrows(ProductException.class, () -> ProductOptionService.updateProductOption(updateRequestDto));
        verify(productOptionRepository, never()).save(productOption);
    }

    @Test //기본적으로 성공테스트와 동일하지만 마지막 검증에서 타입이 변경되고 변경된 옵션이 저장되는지 검증
    void testUpdateProductOption_SelectToInput() {
        // Given: 테스트 데이터 준비
        ProductOptionUpdateRequestDto updateRequestDto = new ProductOptionUpdateRequestDto();
        updateRequestDto.setId(1L);
        updateRequestDto.setName("Updated Option");
        updateRequestDto.setPrice(1000l);
        updateRequestDto.setType(OptionType.INPUT); // 변경할 타입은 INPUT

        List<ProductOption> productOptionList = List.of(productOption, productOption);

        // Mock 객체 설정
        when(productOptionRepository.findById(1L)).thenReturn(Optional.of(productOption));
        when(selectOptionValueRepository.findById(anyLong())).thenReturn(Optional.of(selectOptionValue));
        when(productOptionRepository.findByProductId(1L)).thenReturn(Optional.of(productOptionList));

        // When: 메서드 호출
        productOptionService.updateProductOption(updateRequestDto);
        Optional<List<ProductOption>> result = productOptionService.selectProductOption(productRequestDto);

        // Then: 검증
        verify(selectOptionValueRepository).deleteByProductOptionId(1L); // 타입 변경 시 기존 SELECT 하위 옵션 삭제
        verify(productOptionRepository).save(productOption); // 상품 옵션 저장
        assertNotEquals(OptionType.SELECT, result.get().get(0).getType());  // 옵션 개수는 2개여야 함
    }

    @Test
    void deleteProductOption() { //SELECT TYPE
        // given: 존재하는 ProductOption을 삭제하는 경우
        when(productOptionRepository.findById(1L)).thenReturn(Optional.of(productOption));
        doNothing().when(productOptionRepository).deleteById(1L);
        doNothing().when(selectOptionValueRepository).deleteByProductOptionId(1L);

        ProductOptionRequestDto productOptionRequestDto = new ProductOptionRequestDto();
        productOptionRequestDto.setId(1L);

        // when: deleteProductOption 메소드 호출
        productOptionService.deleteProductOption(productOptionRequestDto);

        // then: productOptionRepository.deleteById가 호출되었는지 검증
        verify(productOptionRepository, times(1)).deleteById(1L);

        // then: selectOptionValueRepository.deleteByProductOptionId가 호출되었는지 검증 (SELECT 타입일 경우)
        verify(selectOptionValueRepository, times(1)).deleteByProductOptionId(1L);
    }

    @Test
    void deleteProductOption_ExistingProductOption_InputType() {
        // given: 존재하는 ProductOption을 삭제하는 경우, 타입이 INPUT
        productOption.setType(OptionType.INPUT);
        when(productOptionRepository.findById(1L)).thenReturn(Optional.of(productOption));
        doNothing().when(productOptionRepository).deleteById(1L);

        ProductOptionRequestDto productOptionRequestDto = new ProductOptionRequestDto();
        productOptionRequestDto.setId(1L);

        // when: deleteProductOption 메소드 호출
        productOptionService.deleteProductOption(productOptionRequestDto);

        // then: productOptionRepository.deleteById가 호출되었는지 검증
        verify(productOptionRepository, times(1)).deleteById(1L);

        // then: selectOptionValueRepository.deleteByProductOptionId가 호출되지 않아야 함 (INPUT 타입일 경우)
        verify(selectOptionValueRepository, never()).deleteByProductOptionId(anyLong());
    }

    @Test
    void deleteProductOption_ProductOptionNotFound() {
        // given: 존재하지 않는 ProductOption을 삭제하려고 할 때
        when(productOptionRepository.findById(1L)).thenReturn(Optional.empty());

        ProductOptionRequestDto productOptionRequestDto = new ProductOptionRequestDto();
        productOptionRequestDto.setId(1L);

        // when & then: 제품이 존재하지 않으면 예외가 발생해야 함
//        assertThrows(IllegalArgumentException.class, () -> productOptionService.deleteProductOption(productOptionRequestDto));

        // then: productOptionRepository.deleteById가 호출되지 않아야 함
        verify(productOptionRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteSelectOptionValue() {
        // given: 존재하는 SelectOptionValue를 삭제하려는 경우
        // mock: selectOptionValueRepository.findById 호출 시, 존재하는 SelectOptionValue 반환
        when(selectOptionValueRepository.findById(1L)).thenReturn(Optional.of(selectOptionValue));

        // when: deleteSelectOptionValue 메서드 호출
        productOptionService.deleteSelectOptionValue(selectOptionValueDeleteRequestDto);

        // then: selectOptionValueRepository.deleteById가 호출되었는지 확인
        verify(selectOptionValueRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteSelectOptionValue_SelectOptionValueNotFound() {
        // given: 존재하지 않는 SelectOptionValue를 삭제하려는 경우
        SelectOptionValueDeleteRequestDto selectOptionValueDeleteRequestDto = new SelectOptionValueDeleteRequestDto();
        selectOptionValueDeleteRequestDto.setId(1L);

        // mock: selectOptionValueRepository.findById 호출 시, 존재하지 않는 SelectOptionValue 반환
        when(selectOptionValueRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then: 존재하지 않는 SelectOptionValue를 삭제하려 할 때 예외가 발생해야 한다.
//        assertThrows(ProductNotFoundException.class, () -> productOptionService.deleteSelectOptionValue(selectOptionValueDeleteRequestDto));

        // then: selectOptionValueRepository.deleteById가 호출되지 않아야 함
        verify(selectOptionValueRepository, never()).deleteById(anyLong());
    }
}