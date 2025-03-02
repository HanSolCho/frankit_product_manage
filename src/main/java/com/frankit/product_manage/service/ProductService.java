package com.frankit.product_manage.service;

import com.frankit.product_manage.Dto.Request.ProductRegisterRequestDto;
import com.frankit.product_manage.Dto.Request.ProductRequestDto;
import com.frankit.product_manage.Dto.Response.ProductSelectPagingResponseDto;
import com.frankit.product_manage.Dto.Response.ProductSelectResponseDto;
import com.frankit.product_manage.entity.OptionType;
import com.frankit.product_manage.entity.Product;
import com.frankit.product_manage.entity.ProductOption;
import com.frankit.product_manage.exception.product.ProductNotFoundException;
import com.frankit.product_manage.repository.ProductOptionRepository;
import com.frankit.product_manage.repository.ProductRepository;
import com.frankit.product_manage.repository.SelectOptionValueRepository;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class ProductService {
    @Autowired
    public ProductRepository productRepository;
    @Autowired
    private ProductOptionRepository productOptionRepository;
    @Autowired
    private SelectOptionValueRepository selectOptionValueRepository;

    public void registerProduct(ProductRegisterRequestDto productRequestDto){
        Date currentDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());

        Product product = Product.builder()
                .name(productRequestDto.getName())
                .description(productRequestDto.getDescription())
                .price(productRequestDto.getPrice())
                .deliveryFee(productRequestDto.getDeliveryFee())
                .date(currentDate) // 현재 날짜 받아서 넣기
                .build();

        productRepository.save(product);
        log.info("제품 등록 성공: {}", product.getId());
    }

    public ProductSelectPagingResponseDto selectAllProduct(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findAll(pageable); // 데이터 조회

        List<ProductSelectResponseDto> productSelectResponseDtoList = productPage.stream()
                .map(ProductSelectResponseDto::new) // Product -> ProductResponseDTO 변환
                .collect(Collectors.toList());

        return new ProductSelectPagingResponseDto(productSelectResponseDtoList, productPage.getNumber(), productPage.getSize());
    }

    public ProductSelectPagingResponseDto selectProductByName(String name,int page, int size){

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findByNameContainingIgnoreCase(name,pageable); // 데이터 조회

        List<ProductSelectResponseDto> productSelectResponseDtoList = productPage.stream()
                .map(ProductSelectResponseDto::new)
                .collect(Collectors.toList());

        return new ProductSelectPagingResponseDto(productSelectResponseDtoList, productPage.getNumber(), productPage.getSize());
    }

    // 상품 조회 : 금액 관련해서 select 추가 ( 금액 ~만원 이상, 배달비 ~원 이하 이런식으로)
    public ProductSelectPagingResponseDto selectProductByGreaterPrice(Long price, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findByPriceGreaterThanEqual(price,pageable);
        List<ProductSelectResponseDto> productSelectResponseDtoList = productPage.stream()
                .map(ProductSelectResponseDto::new) // Product -> ProductResponseDTO 변환
                .collect(Collectors.toList());

        return new ProductSelectPagingResponseDto(productSelectResponseDtoList, productPage.getNumber(), productPage.getSize());
    }

    public ProductSelectPagingResponseDto selectProductByLessPrice(Long price, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findByPriceLessThanEqual(price,pageable);
        List<ProductSelectResponseDto> productSelectResponseDtoList = productPage.stream()
                .map(ProductSelectResponseDto::new) // Product -> ProductResponseDTO 변환
                .collect(Collectors.toList());

        return new ProductSelectPagingResponseDto(productSelectResponseDtoList, productPage.getNumber(), productPage.getSize());
    }

    public void updateProduct(ProductRequestDto productRequestDto){

        Optional<Product> vaildProduct = productRepository.findById(productRequestDto.getId());

        if (vaildProduct.isPresent()) {
            Product product = vaildProduct.get();

            product.setName(productRequestDto.getName());
            product.setDescription(productRequestDto.getDescription());
            product.setPrice(productRequestDto.getPrice());
            product.setDeliveryFee(productRequestDto.getDeliveryFee()); // 오타 수정

            productRepository.save(product);
            log.info("제품 수정 성공: {}", product.getId());
        } else {
            log.error("수정 실패: 존재하지 않는 제품 ID: {}", productRequestDto.getId());
            throw new ProductNotFoundException();
        }
    }

    @Transactional
    public void deleteProduct(ProductRequestDto productRequestDto){

        // 먼저 제품이 존재하는지 확인
        Optional<Product> deleteProduct = productRepository.findById(productRequestDto.getId());

        if (deleteProduct.isPresent()) {
            productRepository.deleteById(deleteProduct.get().getId());
            log.info("제품 삭제 성공: {}", productRequestDto.getId());
            Optional<List<ProductOption>> deleteProductOption = productOptionRepository.findByProductId(productRequestDto.getId());
            if(deleteProductOption.isPresent()) {
                for (ProductOption productOption : deleteProductOption.get()) {
                    productOptionRepository.deleteById(productOption.getId());
                    if (productOption.getType() == OptionType.SELECT) {
                        selectOptionValueRepository.deleteByProductOptionId(productOption.getId());
                    }
                }
            }
        } else {
            log.error("삭제 실패: 존재하지 않는 제품 ID: {}", productRequestDto.getId());
            throw new ProductNotFoundException();
        }

    }

}
