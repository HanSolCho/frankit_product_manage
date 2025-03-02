package com.frankit.product_manage.service;

import com.frankit.product_manage.Dto.Request.*;
import com.frankit.product_manage.entity.OptionType;
import com.frankit.product_manage.entity.Product;
import com.frankit.product_manage.entity.ProductOption;
import com.frankit.product_manage.entity.SelectOptionValue;
import com.frankit.product_manage.exception.ErrorCode;
import com.frankit.product_manage.exception.product.option.ProductOptionException;
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

        selectOptionValue = SelectOptionValue.builder()
                .id(1L)
                .name("Red")
                .build();
        productOption = ProductOption.builder()
                .id(1L)
                .name("Color")
                .type(OptionType.SELECT)
                .price(500L)
                .selectOptionValueList(List.of(selectOptionValue))
                .build();

        Date currentDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());

        HashMap<Long, String> selectOptionValueMap = new HashMap<>();
        selectOptionValueMap.put(1L, "Red");
        selectOptionValueMap.put(2L, "Blue");

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

        selectOptionValueAddRequestDto = new SelectOptionValueAddRequestDto();
        selectOptionValueAddRequestDto.setName(List.of("Red", "Blue"));

        productOptionAddRequestDto = new ProductOptionAddRequestDto();
        productOptionAddRequestDto.setProductId(1L);
        productOptionAddRequestDto.setName("Color");
        productOptionAddRequestDto.setType(OptionType.SELECT);
        productOptionAddRequestDto.setPrice(1000L);
        productOptionAddRequestDto.setSelectOptionValue(selectOptionValueAddRequestDto);

        productOptionUpdateRequestDto = new ProductOptionUpdateRequestDto();
        productOptionUpdateRequestDto.setId(1L);
        productOptionUpdateRequestDto.setProductId(1L);
        productOptionUpdateRequestDto.setName("update Color");
        productOptionUpdateRequestDto.setType(OptionType.SELECT);
        productOptionUpdateRequestDto.setPrice(1000L);
        productOptionUpdateRequestDto.setSelectOptionValueMap(selectOptionValueMap);

        selectOptionValueDeleteRequestDto = new SelectOptionValueDeleteRequestDto();
        selectOptionValueDeleteRequestDto.setId(1L);

    }
    @Test
    void addProductOption() {
        // given:
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productOptionRepository.save(any(ProductOption.class))).thenReturn(ProductOption.builder().id(1L).name("Color").build());
        when(selectOptionValueRepository.save(any(SelectOptionValue.class))).thenReturn(SelectOptionValue.builder().id(1L).name("Red").build());

        // when:
        productOptionService.addProductOption(productOptionAddRequestDto);

        // then:
        verify(productRepository, times(1)).findById(1L);
        verify(productOptionRepository, times(1)).save(any(ProductOption.class));
        verify(selectOptionValueRepository, times(2)).save(any(SelectOptionValue.class));
    }

    @Test
    void addProductOption_ProductNotFound() {
        // given:
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then:
        assertThrows(ProductOptionException.class, () -> productOptionService.addProductOption(productOptionAddRequestDto));
        verify(productRepository, never()).save(product);
    }

    @Test
    void addProductOption_ExceedsOptionLimit() {
        // given:
        product.setOptions(List.of(productOption, productOption, productOption));  // 3개의 옵션 추가

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // when & then:
        ProductOptionException exception = assertThrows(ProductOptionException.class,
                () -> productOptionService.addProductOption(productOptionAddRequestDto));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.TOO_MANY_OPTIONS);
        verify(productRepository, never()).save(product);
    }

    @Test
    void selectProductOption() {
        List<ProductOption> productOptionList = List.of(productOption, productOption);

        // when:
        when(productOptionRepository.findByProductId(1L)).thenReturn(Optional.of(productOptionList));
        Optional<List<ProductOption>> result = productOptionService.selectProductOption(productRequestDto.getId());

        // then:
        verify(productOptionRepository, times(1)).findByProductId(1L);

        assertTrue(result.isPresent());
        assertEquals(2, result.get().size());
        assertEquals("Color", result.get().getFirst().getName());

    }

    @Test
    void updateProductOption() {
        // given:
        ProductOptionUpdateRequestDto updateRequestDto = new ProductOptionUpdateRequestDto();
        updateRequestDto.setId(1L);
        updateRequestDto.setName("Updated Option");
        updateRequestDto.setPrice(1000L);
        updateRequestDto.setType(OptionType.INPUT);

        when(productOptionRepository.findById(1L)).thenReturn(Optional.of(productOption));
        when(selectOptionValueRepository.findById(anyLong())).thenReturn(Optional.of(selectOptionValue));

        // when:
        productOptionService.updateProductOption(updateRequestDto);

        // then:
        verify(productOptionRepository).save(productOption); // save 호출 검증
        verify(selectOptionValueRepository).deleteByProductOptionId(1L); // SELECT에서 INPUT으로 변경 시 하위 옵션 삭제
    }

    @Test
    void testUpdateProductOption_ProductNotFound() {
        // given:
        ProductOptionUpdateRequestDto updateRequestDto = new ProductOptionUpdateRequestDto();
        updateRequestDto.setId(999L); // 존재하지 않는 ID

        when(productOptionRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then:
        verify(productOptionRepository, never()).save(productOption);
    }

    @Test
    void testUpdateProductOption_SelectToInput() {
        // given:
        ProductOptionUpdateRequestDto updateRequestDto = new ProductOptionUpdateRequestDto();
        updateRequestDto.setId(1L);
        updateRequestDto.setName("Updated Option");
        updateRequestDto.setPrice(1000l);
        updateRequestDto.setType(OptionType.INPUT);

        List<ProductOption> productOptionList = List.of(productOption, productOption);

        when(productOptionRepository.findById(1L)).thenReturn(Optional.of(productOption));
        when(selectOptionValueRepository.findById(anyLong())).thenReturn(Optional.of(selectOptionValue));
        when(productOptionRepository.findByProductId(1L)).thenReturn(Optional.of(productOptionList));

        // when:
        productOptionService.updateProductOption(updateRequestDto);
        Optional<List<ProductOption>> result = productOptionService.selectProductOption(productRequestDto.getId());

        // then:
        verify(selectOptionValueRepository).deleteByProductOptionId(1L);
        verify(productOptionRepository).save(productOption);
        assertNotEquals(OptionType.SELECT, result.get().getFirst().getType());
    }

    @Test
    void deleteProductOption() {
        // given:
        when(productOptionRepository.findById(1L)).thenReturn(Optional.of(productOption));
        doNothing().when(productOptionRepository).deleteById(1L);
        doNothing().when(selectOptionValueRepository).deleteByProductOptionId(1L);

        ProductOptionRequestDto productOptionRequestDto = new ProductOptionRequestDto();
        productOptionRequestDto.setId(1L);

        // when:
        productOptionService.deleteProductOption(productOptionRequestDto);

        // then:
        verify(productOptionRepository, times(1)).deleteById(1L);
        verify(selectOptionValueRepository, times(1)).deleteByProductOptionId(1L);
    }

    @Test
    void deleteProductOption_ExistingProductOption_InputType() {
        // given:
        productOption.setType(OptionType.INPUT);
        when(productOptionRepository.findById(1L)).thenReturn(Optional.of(productOption));
        doNothing().when(productOptionRepository).deleteById(1L);

        ProductOptionRequestDto productOptionRequestDto = new ProductOptionRequestDto();
        productOptionRequestDto.setId(1L);

        // when:
        productOptionService.deleteProductOption(productOptionRequestDto);

        // then:
        verify(productOptionRepository, times(1)).deleteById(1L);
        verify(selectOptionValueRepository, never()).deleteByProductOptionId(anyLong());
    }

    @Test
    void deleteProductOption_ProductOptionNotFound() {
        // given:
        when(productOptionRepository.findById(1L)).thenReturn(Optional.empty());

        ProductOptionRequestDto productOptionRequestDto = new ProductOptionRequestDto();
        productOptionRequestDto.setId(1L);

        // when & then:
        verify(productOptionRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteSelectOptionValue() {
        // given:
        when(selectOptionValueRepository.findById(1L)).thenReturn(Optional.of(selectOptionValue));

        // when:
        productOptionService.deleteSelectOptionValue(selectOptionValueDeleteRequestDto);

        // then:
        verify(selectOptionValueRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteSelectOptionValue_SelectOptionValueNotFound() {
        // given:
        SelectOptionValueDeleteRequestDto selectOptionValueDeleteRequestDto = new SelectOptionValueDeleteRequestDto();
        selectOptionValueDeleteRequestDto.setId(1L);

        when(selectOptionValueRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then:
        verify(selectOptionValueRepository, never()).deleteById(anyLong());
    }
}