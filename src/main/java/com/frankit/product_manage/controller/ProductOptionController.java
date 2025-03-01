package com.frankit.product_manage.controller;

import com.frankit.product_manage.Dto.Request.*;
import com.frankit.product_manage.service.ProductOptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/frankit/product-manage/option")
public class ProductOptionController {

    private ProductOptionService productOptionService;

    public ProductOptionController(ProductOptionService productOptionService){
        this.productOptionService = productOptionService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addProductOption(@RequestBody ProductOptionAddRequestDto productOptionAddRequestDto){
        productOptionService.addProductOption(productOptionAddRequestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/select/product")
    public ResponseEntity<?> selectProductOption(@RequestParam(value = "id") Long productId){
        return ResponseEntity.ok(productOptionService.selectProductOption(productId));
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateProductOption(@RequestBody ProductOptionUpdateRequestDto productOptionUpdateRequestDto){
        productOptionService.updateProductOption(productOptionUpdateRequestDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteProductOption(@RequestBody ProductOptionRequestDto productOptionRequestDto){
        productOptionService.deleteProductOption(productOptionRequestDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/select-option-value")
    public ResponseEntity<?> deleteSelectOptionValue(@RequestBody SelectOptionValueDeleteRequestDto selectOptionValueDeleteRequestDto){
        productOptionService.deleteSelectOptionValue(selectOptionValueDeleteRequestDto);
        return ResponseEntity.ok().build();
    }

}
