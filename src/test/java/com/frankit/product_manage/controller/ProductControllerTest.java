package com.frankit.product_manage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frankit.product_manage.Dto.Request.ProductRegisterRequestDto;
import com.frankit.product_manage.Dto.Request.ProductRequestDto;
import com.frankit.product_manage.Dto.Response.ProductSelectPagingResponseDto;
import com.frankit.product_manage.Dto.Response.ProductSelectResponseDto;
import com.frankit.product_manage.config.TestSecurityConfig;
import com.frankit.product_manage.entity.OptionType;
import com.frankit.product_manage.entity.Product;
import com.frankit.product_manage.entity.ProductOption;
import com.frankit.product_manage.entity.SelectOptionValue;
import com.frankit.product_manage.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs // rest docs 자동 설정
@Import(TestSecurityConfig.class)
class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    @DisplayName("상품등록 Controller 테스트")
    void testRegisterProduct() throws Exception  {
        //given
        ProductRegisterRequestDto productRequestDto = new ProductRegisterRequestDto();
        productRequestDto.setName("공책");
        productRequestDto.setDescription("필기를 할 수 있는 노트입니다");
        productRequestDto.setPrice(3000L);
        productRequestDto.setDeliveryFee(0L);

        //when & then: 예상되는 결과 검증
        mockMvc.perform(post("/frankit/product-manage/product/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(productRequestDto)))
                .andExpect(status().isOk())

                .andDo(
                        document("product/register", //여기이름이 스니펫 폴더 이름
                                requestFields(  // 실제 요청 본문 필드 설명
                                        fieldWithPath("name").description("상품명"),
                                        fieldWithPath("description").description("상품소개"),
                                        fieldWithPath("price").description("가격"),
                                        fieldWithPath("deliveryFee").description("배달비")
                                )
                        )
                );

        verify(productService, times(1)).registerProduct(productRequestDto);
    }

    @Test
    @DisplayName("전체 상품 조회 Controller 테스트")
    void testSelectAllProduct() throws Exception   {
        int page = 0;
        int size = 1;

        // mock 응답 데이터 준비
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

        Product mockProducts = Product.builder()
                .id(1L)
                .name("첫번째 상품")
                .description("첫번째 상품입니다")
                .price(3000L)
                .deliveryFee(0L)
                .date(new Date())
                .options(List.of(productOption))
                .build();

        List<Product> content = Collections.singletonList(mockProducts);
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = new PageImpl<>(content, pageable, 10);


        when(productService.selectAllProduct(page, size)).thenReturn(new ProductSelectPagingResponseDto(
                content.stream().map(ProductSelectResponseDto::new).collect(Collectors.toList()),
                productPage.getNumber(),
                productPage.getSize())
        );

        //when & then: 예상되는 결과 검증
        mockMvc.perform(get("/frankit/product-manage/product/all")
                        .param("pageIndex",String.valueOf(page))
                        .param("pageSize",String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())

                .andDo(
                        document("product/all", //여기이름이 스니펫 폴더 이름
                                queryParameters(  // 실제 요청 본문 필드 설명
                                        parameterWithName("pageIndex")
                                                .description("전체 상품 페이지 중 몇번째 페이지인지"),
                                        parameterWithName("pageSize")
                                                .description("페이지 별 상품 개수")
                                ),
                                responseFields(  // 응답 본문 필드 설명
                                        fieldWithPath("products").description("상품 목록"),
                                        fieldWithPath("products[].id").description("상품 ID"),
                                        fieldWithPath("products[].name").description("상품 이름"),
                                        fieldWithPath("products[].description").description("상품 설명"),
                                        fieldWithPath("products[].price").description("상품 가격"),
                                        fieldWithPath("products[].deliveryFee").description("배송비"),
                                        fieldWithPath("products[].date").description("상품 등록 날짜 (ISO-8601 형식)"),
                                        fieldWithPath("products[].productOptionSelectList").description("상품 옵션 목록"),

                                        fieldWithPath("products[].productOptionSelectList[].id").description("옵션 ID"),
                                        fieldWithPath("products[].productOptionSelectList[].name").description("옵션 이름"),
                                        fieldWithPath("products[].productOptionSelectList[].type").description("옵션 타입 (SELECT 또는 INPUT)"),
                                        fieldWithPath("products[].productOptionSelectList[].price").description("옵션 가격"),
                                        fieldWithPath("products[].productOptionSelectList[].selectOptionValueList").description("선택 옵션 값 목록 (옵션 타입이 SELECT일 때)"),

                                        fieldWithPath("products[].productOptionSelectList[].selectOptionValueList[].id").description("옵션 값 ID"),
                                        fieldWithPath("products[].productOptionSelectList[].selectOptionValueList[].name").description("옵션 값 이름"),

                                        fieldWithPath("number").description("현재 페이지 번호"),
                                        fieldWithPath("size").description("페이지 크기")
                                )
                        )
                );

        verify(productService, times(1)).selectAllProduct(page,size);
    }

    @Test
    @DisplayName("기준 값 이상 상품 Controller 테스트")
    void testSelectProductOverprice() throws Exception   {
        int page = 0;
        int size = 1;
        Long price = 3000L;

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

        Product mockProducts = Product.builder()
                .id(1L)
                .name("첫번째 상품")
                .description("첫번째 상품입니다")
                .price(3000L)
                .deliveryFee(0L)
                .date(new Date())
                .options(List.of(productOption))
                .build();

        List<Product> content = Collections.singletonList(mockProducts);
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = new PageImpl<>(content, pageable, 10);


        when(productService.selectProductByGreaterPrice(price,page, size)).thenReturn(new ProductSelectPagingResponseDto(
                content.stream().map(ProductSelectResponseDto::new).collect(Collectors.toList()),
                productPage.getNumber(),
                productPage.getSize())
        );

        //when & then: 예상되는 결과 검증
        mockMvc.perform(get("/frankit/product-manage/product/over-price")
                        .param("price",String.valueOf(price))
                        .param("pageIndex",String.valueOf(page))
                        .param("pageSize",String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())

                .andDo(
                        document("product/over-price", //여기이름이 스니펫 폴더 이름
                                queryParameters(  // 실제 요청 본문 필드 설명
                                        parameterWithName("price")
                                                .description("기준 가격"),
                                        parameterWithName("pageIndex")
                                                .description("전체 상품 페이지 중 몇번째 페이지인지"),
                                        parameterWithName("pageSize")
                                                .description("페이지 별 상품 개수")
                                ),
                                responseFields(  // 응답 본문 필드 설명
                                        fieldWithPath("products").description("상품 목록"),
                                        fieldWithPath("products[].id").description("상품 ID"),
                                        fieldWithPath("products[].name").description("상품 이름"),
                                        fieldWithPath("products[].description").description("상품 설명"),
                                        fieldWithPath("products[].price").description("상품 가격"),
                                        fieldWithPath("products[].deliveryFee").description("배송비"),
                                        fieldWithPath("products[].date").description("상품 등록 날짜 (ISO-8601 형식)"),
                                        fieldWithPath("products[].productOptionSelectList").description("상품 옵션 목록"),

                                        fieldWithPath("products[].productOptionSelectList[].id").description("옵션 ID"),
                                        fieldWithPath("products[].productOptionSelectList[].name").description("옵션 이름"),
                                        fieldWithPath("products[].productOptionSelectList[].type").description("옵션 타입 (SELECT 또는 INPUT)"),
                                        fieldWithPath("products[].productOptionSelectList[].price").description("옵션 가격"),
                                        fieldWithPath("products[].productOptionSelectList[].selectOptionValueList").description("선택 옵션 값 목록 (옵션 타입이 SELECT일 때)"),

                                        fieldWithPath("products[].productOptionSelectList[].selectOptionValueList[].id").description("옵션 값 ID"),
                                        fieldWithPath("products[].productOptionSelectList[].selectOptionValueList[].name").description("옵션 값 이름"),

                                        fieldWithPath("number").description("현재 페이지 번호"),
                                        fieldWithPath("size").description("페이지 크기")
                                )
                        )
                );

        verify(productService, times(1)).selectProductByGreaterPrice(price,page,size);
    }

    @Test
    @DisplayName("기준 값 이하 상품 Controller 테스트")
    void testSelectProductUnderprice() throws Exception   {
        int page = 0;
        int size = 1;
        Long price = 3000L;

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

        Product mockProducts = Product.builder()
                .id(1L)
                .name("첫번째 상품")
                .description("첫번째 상품입니다")
                .price(3000L)
                .deliveryFee(0L)
                .date(new Date())
                .options(List.of(productOption))
                .build();

        List<Product> content = Collections.singletonList(mockProducts);
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = new PageImpl<>(content, pageable, 10);


        when(productService.selectProductByLessPrice(price,page, size)).thenReturn(new ProductSelectPagingResponseDto(
                content.stream().map(ProductSelectResponseDto::new).collect(Collectors.toList()),
                productPage.getNumber(),
                productPage.getSize())
        );

        //when & then: 예상되는 결과 검증
        mockMvc.perform(get("/frankit/product-manage/product/under-price")
                        .param("price",String.valueOf(price))
                        .param("pageIndex",String.valueOf(page))
                        .param("pageSize",String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())

                .andDo(
                        document("product/under-price", //여기이름이 스니펫 폴더 이름
                                queryParameters(  // 실제 요청 본문 필드 설명
                                        parameterWithName("price")
                                                .description("기준 가격"),
                                        parameterWithName("pageIndex")
                                                .description("전체 상품 페이지 중 몇번째 페이지인지"),
                                        parameterWithName("pageSize")
                                                .description("페이지 별 상품 개수")
                                ),
                                responseFields(  // 응답 본문 필드 설명
                                        fieldWithPath("products").description("상품 목록"),
                                        fieldWithPath("products[].id").description("상품 ID"),
                                        fieldWithPath("products[].name").description("상품 이름"),
                                        fieldWithPath("products[].description").description("상품 설명"),
                                        fieldWithPath("products[].price").description("상품 가격"),
                                        fieldWithPath("products[].deliveryFee").description("배송비"),
                                        fieldWithPath("products[].date").description("상품 등록 날짜 (ISO-8601 형식)"),
                                        fieldWithPath("products[].productOptionSelectList").description("상품 옵션 목록"),

                                        fieldWithPath("products[].productOptionSelectList[].id").description("옵션 ID"),
                                        fieldWithPath("products[].productOptionSelectList[].name").description("옵션 이름"),
                                        fieldWithPath("products[].productOptionSelectList[].type").description("옵션 타입 (SELECT 또는 INPUT)"),
                                        fieldWithPath("products[].productOptionSelectList[].price").description("옵션 가격"),
                                        fieldWithPath("products[].productOptionSelectList[].selectOptionValueList").description("선택 옵션 값 목록 (옵션 타입이 SELECT일 때)"),

                                        fieldWithPath("products[].productOptionSelectList[].selectOptionValueList[].id").description("옵션 값 ID"),
                                        fieldWithPath("products[].productOptionSelectList[].selectOptionValueList[].name").description("옵션 값 이름"),

                                        fieldWithPath("number").description("현재 페이지 번호"),
                                        fieldWithPath("size").description("페이지 크기")
                                )
                        )
                );

        verify(productService, times(1)).selectProductByLessPrice(price,page,size);
    }

    @Test
    @DisplayName("상품 정보 변경 Controller 테스트")
    void testUpdateProduct() throws Exception   {
        //given
        ProductRequestDto productRequestDto = new ProductRequestDto();
        productRequestDto.setId(1L);
        productRequestDto.setName("공책");
        productRequestDto.setDescription("필기를 할 수 있는 노트입니다");
        productRequestDto.setPrice(3000L);
        productRequestDto.setDeliveryFee(0L);

        //when & then: 예상되는 결과 검증
        mockMvc.perform(MockMvcRequestBuilders.put("/frankit/product-manage/product/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(productRequestDto)))
                .andExpect(status().isOk())

                .andDo(
                        document("product/update", //여기이름이 스니펫 폴더 이름
                                requestFields(  // 실제 요청 본문 필드 설명
                                        fieldWithPath("id").description("상품관리번호"),
                                        fieldWithPath("name").description("상품명"),
                                        fieldWithPath("description").description("상품소개"),
                                        fieldWithPath("price").description("가격"),
                                        fieldWithPath("deliveryFee").description("배달비")
                                )
                        )
                );

        verify(productService, times(1)).updateProduct(productRequestDto);
    }

    @Test
    @DisplayName("상품 삭제 Cotnroller 테스트")
    void testDeleteProduct() throws Exception {
        //given
        ProductRequestDto productRequestDto = new ProductRequestDto();
        productRequestDto.setId(1L);
        productRequestDto.setName("공책");
        productRequestDto.setDescription("필기를 할 수 있는 노트입니다");
        productRequestDto.setPrice(3000L);
        productRequestDto.setDeliveryFee(0L);

        //when & then: 예상되는 결과 검증
        mockMvc.perform(MockMvcRequestBuilders.delete("/frankit/product-manage/product/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(productRequestDto)))
                .andExpect(status().isOk())

                .andDo(
                        document("product/delete", //여기이름이 스니펫 폴더 이름
                                requestFields(  // 실제 요청 본문 필드 설명
                                        fieldWithPath("id").description("상품관리번호"),
                                        fieldWithPath("name").description("상품명"),
                                        fieldWithPath("description").description("상품소개"),
                                        fieldWithPath("price").description("가격"),
                                        fieldWithPath("deliveryFee").description("배달비")
                                )
                        )
                );

        verify(productService, times(1)).deleteProduct(productRequestDto);
    }
}