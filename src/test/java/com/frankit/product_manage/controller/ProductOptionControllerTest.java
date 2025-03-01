package com.frankit.product_manage.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frankit.product_manage.Dto.Request.*;
import com.frankit.product_manage.Dto.Response.ProductSelectPagingResponseDto;
import com.frankit.product_manage.Dto.Response.ProductSelectResponseDto;
import com.frankit.product_manage.config.TestSecurityConfig;
import com.frankit.product_manage.entity.OptionType;
import com.frankit.product_manage.entity.ProductOption;
import com.frankit.product_manage.entity.SelectOptionValue;
import com.frankit.product_manage.service.ProductOptionService;
import com.frankit.product_manage.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductOptionController.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs // rest docs 자동 설정
@Import(TestSecurityConfig.class)
class ProductOptionControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    private ProductOptionService productOptionService;

    @Test
    @DisplayName("상품 옵션 추가 Controller 테스트")
    void testAddProductOption() throws Exception {
        //given
        String optionValue = "옵션 선택 값";

        SelectOptionValueAddRequestDto selectOptionValueAddRequestDto = new SelectOptionValueAddRequestDto();
        selectOptionValueAddRequestDto.setName(List.of(optionValue));
        selectOptionValueAddRequestDto.setProductOptionId(1L);

        ProductOptionAddRequestDto productOptionAddRequestDto = new ProductOptionAddRequestDto();
        productOptionAddRequestDto.setName("옵션 이름");
        productOptionAddRequestDto.setType(OptionType.SELECT);
        productOptionAddRequestDto.setProductId(1L);
        productOptionAddRequestDto.setPrice(1000L);
        productOptionAddRequestDto.setSelectOptionValue(selectOptionValueAddRequestDto);

        //when & then: 예상되는 결과 검증
        mockMvc.perform(post("/frankit/product-manage/option/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(productOptionAddRequestDto)))
                .andExpect(status().isOk())

                .andDo(
                        document("option/add", //여기이름이 스니펫 폴더 이름
                                requestFields(  // 실제 요청 본문 필드 설명
                                        fieldWithPath("name").description("옵션 이름"),
                                        fieldWithPath("type").description("옵션 값 타입 (INPUT(직접입력)/SELECT(선택지) 둘 중 하나)"),
                                        fieldWithPath("productId").description("해당 옵션의 상품 ID"),
                                        fieldWithPath("price").description("옵션 비용"),
                                        fieldWithPath("selectOptionValue.name[]").description("옵션 값 타입이 SELECT 일 때 select 옵션 값 리스트 "),
                                        fieldWithPath("selectOptionValue.productOptionId").description("옵션 값 타입이 SELECT 일 때 select 옵션이 속한 productOption의 ID  ")
                                )
                        )
                );

        verify(productOptionService, times(1)).addProductOption(productOptionAddRequestDto);
    }
    //여기서부터 다시ㅏ!1! 응답값!!!

//    [
//    {
//        "id": 5,
//            "name": "Color update",
//            "type": "INPUT",
//            "price": 3700,
//            "selectOptionValueList": []
//    },
//    ]
    @Test
    @DisplayName("상품 옵션 조회 Controller 테스트") //id만 사용하고 있지만 상품 자체의 정보는 보유하고있는게 좋을것으로 판단. 데이터 크기가 작고 확장가능성
    void testSelectProductOption() throws Exception {
        //given
        Long productId = 1L;
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

        when(productOptionService.selectProductOption(productId))
                .thenReturn(Optional.of(List.of(productOption)));  // Optional로 감싸서 반환


        //when & then: 예상되는 결과 검증
        mockMvc.perform(get("/frankit/product-manage/option/select/product")
                        .param("id",String.valueOf(productId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())

                .andDo(
                        document("option/all", //여기이름이 스니펫 폴더 이름
                                queryParameters(  // 실제 요청 본문 필드 설명
                                        parameterWithName("id")
                                                .description("상품 관리 번호")
                                ),
                                responseFields(
                                        fieldWithPath("[].id").description("상품 옵션 ID"),
                                        fieldWithPath("[].name").description("상품 옵션 이름"),
                                        fieldWithPath("[].type").description("옵션 타입 ('INPUT' 또는 'SELECT')"),
                                        fieldWithPath("[].price").description("옵션 가격"),
                                        fieldWithPath("[].selectOptionValueList").description("옵션 값 목록 (빈 배열일 수 있음)"),
                                        fieldWithPath("[].selectOptionValueList[].id").description("옵션 값 ID"),
                                        fieldWithPath("[].selectOptionValueList[].name").description("옵션 값 이름")
                                )
                        )
                );

        verify(productOptionService, times(1)).selectProductOption(productId);
    }

    @Test
    @DisplayName("상품 옵션 업데이트 Controller 테스트")
    void testUpdateProductOption() throws Exception {
        //given
        HashMap<Long,String> optionValueMap = new HashMap<>();
        optionValueMap.put(1L,"Red"); // key : select option value의 id /  value : select option의 value

        ProductOptionUpdateRequestDto productOptionUpdateRequestDto = new ProductOptionUpdateRequestDto();
        productOptionUpdateRequestDto.setId(1L);
        productOptionUpdateRequestDto.setName("Color");
        productOptionUpdateRequestDto.setType(OptionType.SELECT);
        productOptionUpdateRequestDto.setPrice(1000L);
        productOptionUpdateRequestDto.setProductId(1L);
        productOptionUpdateRequestDto.setSelectOptionValueMap(optionValueMap);

        //when & then: 예상되는 결과 검증
        mockMvc.perform(put("/frankit/product-manage/option/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(productOptionUpdateRequestDto)))
                .andExpect(status().isOk())

                .andDo(
                        document("option/update", //여기이름이 스니펫 폴더 이름
                                requestFields(  // 실제 요청 본문 필드 설명
                                        fieldWithPath("id").description("옵션 관리 번호"),
                                        fieldWithPath("name").description("옵션 이름"),
                                        fieldWithPath("type").description("옵션 값 타입 (INPUT(직접입력)/SELECT(선택지) 둘 중 하나)"),
                                        fieldWithPath("productId").description("해당 옵션의 상품 ID"),
                                        fieldWithPath("price").description("옵션 비용"),
                                        fieldWithPath("selectOptionValueMap").description("옵션값 key value hash map 구조는 selectOptionValueMap.*에 추가 설명"),
                                        fieldWithPath("selectOptionValueMap.*").description("... 데이터 구조 맞춰서 입력해주기")
//                                        subsectionWithPath("selectOptionValueUpdateRequestDto.selectOptionValueMap")
//                                                .description("옵션 값 타입이 SELECT일 때 각 옵션 값의 ID와 값 (예: {\"1\": \"Red\"})")
                                )
                        )
                );

        verify(productOptionService, times(1)).updateProductOption(productOptionUpdateRequestDto);
    }

    @Test
    @DisplayName("상품 옵션 삭제 Controller 테스트")
    void testDeleteProductOption() throws Exception {
        ProductOptionRequestDto productOptionRequestDto = new ProductOptionRequestDto();
        productOptionRequestDto.setId(1L);
        productOptionRequestDto.setName("옵션 이름");
        productOptionRequestDto.setType(OptionType.SELECT);
        productOptionRequestDto.setProductId(1L);
        productOptionRequestDto.setPrice(1000L);

        //when & then: 예상되는 결과 검증
        mockMvc.perform(delete("/frankit/product-manage/option/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(productOptionRequestDto)))
                .andExpect(status().isOk())

                .andDo(
                        document("option/delete", //여기이름이 스니펫 폴더 이름
                                requestFields(  // 실제 요청 본문 필드 설명
                                        fieldWithPath("id").description("옵션 관리 번호"),
                                        fieldWithPath("name").description("옵션 이름"),
                                        fieldWithPath("type").description("옵션 값 타입 (INPUT(직접입력)/SELECT(선택지) 둘 중 하나)"),
                                        fieldWithPath("productId").description("해당 옵션의 상품 ID"),
                                        fieldWithPath("price").description("옵션 비용")
                                )
                        )
                );

        verify(productOptionService, times(1)).deleteProductOption(productOptionRequestDto);
    }

    @Test
    @DisplayName("상품 옵션 값 삭제 Controller 테스트")
    void testDeleteSelectOptionValue()  throws Exception {
        SelectOptionValueDeleteRequestDto selectOptionValueDeleteRequestDto = new SelectOptionValueDeleteRequestDto();
        selectOptionValueDeleteRequestDto.setId(1L);

        //when & then: 예상되는 결과 검증
        mockMvc.perform(delete("/frankit/product-manage/option/delete/select-option-value")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(selectOptionValueDeleteRequestDto)))
                .andExpect(status().isOk())

                .andDo(
                        document("option-value/delete", //여기이름이 스니펫 폴더 이름
                                requestFields(  // 실제 요청 본문 필드 설명
                                        fieldWithPath("id").description("옵션 값 관리 번호")
                                )
                        )
                );

        verify(productOptionService, times(1)).deleteSelectOptionValue(selectOptionValueDeleteRequestDto);
    }
}