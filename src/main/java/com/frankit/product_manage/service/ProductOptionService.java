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
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class ProductOptionService {
    @Autowired
    public ProductRepository productRepository;
    @Autowired
    public ProductOptionRepository productOptionRepository;

    @Autowired
    public SelectOptionValueRepository selectOptionValueRepository;

    @Transactional
    public void addProductOption(ProductOptionAddRequestDto productOptionAddRequestDto){

        Product product = productRepository.findById(productOptionAddRequestDto.getProductId())
                .orElseThrow(() -> new ProductOptionException(ErrorCode.PRODUCT_NOT_FOUND));

        // 옵션 개수가 3개 이하인지 확인
        if (product.getOptions().size() >= 3) {
            log.error("상품의 옵션이 3개를 초과하였습니다 : {}", product.getId());
            throw new ProductOptionException(ErrorCode.TOO_MANY_OPTIONS);
        }

        ProductOption productOption = ProductOption.builder()
                .name(productOptionAddRequestDto.getName())
                .type(productOptionAddRequestDto.getType())
                .price(productOptionAddRequestDto.getPrice())
                .product(product)
                .build();

        ProductOption addedProductOption = productOptionRepository.save(productOption);
        // SELECT 타입의 옵션 값 처리
       if (productOptionAddRequestDto.getType() == OptionType.SELECT) {
            for(String optionValue : productOptionAddRequestDto.getSelectOptionValue().getName()) {
                SelectOptionValue selectOptionValue = SelectOptionValue.builder()
                        .name(optionValue)
                        .productOption(addedProductOption)
                        .build();
                selectOptionValueRepository.save(selectOptionValue);
            }
        }
        log.info("Product option 추가 성공: {}", addedProductOption.getId());
    }
    public Optional<List<ProductOption>> selectProductOption(Long productId){
        return productOptionRepository.findByProductId(productId);
    }
    @Transactional
    public void updateProductOption(ProductOptionUpdateRequestDto productOptionUpdateRequestDto){

        Long productOptionId = productOptionUpdateRequestDto.getId();
        //상품 옵션 id에 해당하는 상품 옵션 정보 추출
        Optional<ProductOption> vaildProductOption = productOptionRepository.findById(productOptionId);
        if (vaildProductOption.isPresent()) {

            ProductOption productOption = vaildProductOption.get();
            //상품 옵션 타입이 셀렉트이고 업데이트 예정 타입이 인풋인 경우
            if(productOption.getType() == OptionType.SELECT && productOptionUpdateRequestDto.getType() == OptionType.INPUT){
                //상품 옵션 타입 하위 옵션 값 삭제
                selectOptionValueRepository.deleteByProductOptionId(productOption.getId());
            }
            //변경 값 저장
            productOption.setName(productOptionUpdateRequestDto.getName());
            productOption.setPrice(productOptionUpdateRequestDto.getPrice());
            productOption.setType(productOptionUpdateRequestDto.getType());

            productOptionRepository.save(productOption);

            // 업데이트 예정 타입이 select 였다면
            if (productOptionUpdateRequestDto.getType() == OptionType.SELECT) {
                //select 타입의 옵션 값 맵 추출
                HashMap<Long, String> selectOptionValueMap = productOptionUpdateRequestDto.getSelectOptionValueMap();
                //맵만큼 반복하면서 insert 작업 진행
                selectOptionValueMap.forEach((id, name) -> {
                    SelectOptionValue selectOptionValue = selectOptionValueRepository.findById(id)
                            .orElseThrow(() ->new ProductOptionException(ErrorCode.SELECT_OPTION_VALUE_NOT_FOUND));
                    selectOptionValue.setName(name);
                    selectOptionValueRepository.save(selectOptionValue);
                });
            }
            log.info("Product option 변경 성공: {}", productOption.getId());
        } else {
            log.error("해당하는 Product option을 찾을 수 없습니다: {}", productOptionId);
            throw new ProductOptionException(ErrorCode.PRODUCT_OPTION_NOT_FOUND);
        }

    }
    @Transactional
    public void deleteProductOption(ProductOptionRequestDto productOptionRequestDto){
        // 먼저 제품이 존재하는지 확인
        Optional<ProductOption> deleteProductOption = productOptionRepository.findById(productOptionRequestDto.getId());
        Long deleteProductOptionId = deleteProductOption.get().getId();
        if (deleteProductOption.isPresent()) {
            productOptionRepository.deleteById(deleteProductOptionId);  // 제품이 존재하면 삭제
            if (deleteProductOption.get().getType() == OptionType.SELECT) {
                selectOptionValueRepository.deleteByProductOptionId(deleteProductOptionId);
            }
            log.info("Product option 삭제 성공: {}", deleteProductOptionId);
        } else {
            log.error("해당하는 Product option을 찾을 수 없습니다: {}", productOptionRequestDto.getId());
            throw new ProductOptionException(ErrorCode.PRODUCT_OPTION_NOT_FOUND);
        }
    }

    @Transactional
    public void deleteSelectOptionValue(SelectOptionValueDeleteRequestDto selectOptionValueDeleteRequestDto){

        Optional<SelectOptionValue> deleteSelectOptionValue = selectOptionValueRepository.findById(selectOptionValueDeleteRequestDto.getId());

        if (deleteSelectOptionValue.isPresent()) {
            selectOptionValueRepository.deleteById(deleteSelectOptionValue.get().getId());  // 제품이 존재하면 삭제
            log.info("Select option value 삭제 성공: {}", deleteSelectOptionValue.get().getId());
        } else {
            log.error("해당하는 Product option의 값을 찾을 수 없습니다: {}", deleteSelectOptionValue.get().getId());
            throw new ProductOptionException(ErrorCode.SELECT_OPTION_VALUE_NOT_FOUND);
        }
    }
}
