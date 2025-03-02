package com.frankit.product_manage.controller;

import com.frankit.product_manage.Dto.Request.ProductRegisterRequestDto;
import com.frankit.product_manage.Dto.Request.ProductRequestDto;
import com.frankit.product_manage.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/frankit/product-manage/product")
public class ProductController {

    private ProductService productService;

    public ProductController(ProductService productService){
        this.productService = productService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerProduct(@RequestBody ProductRegisterRequestDto productRequestDto){
        productService.registerProduct(productRequestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    public ResponseEntity<?> selectAllProduct(@RequestParam(value = "pageIndex", defaultValue = "0") int page,
                                              @RequestParam(value = "pageSize", defaultValue = "10") int size){
        return ResponseEntity.ok(productService.selectAllProduct(page, size));
    }

    @GetMapping("/over-price")
    public ResponseEntity<?> selectProductOverPrice(
            @RequestParam(value = "price", defaultValue = "0") Long price,
            @RequestParam(value = "pageIndex", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int size){
        return ResponseEntity.ok(productService.selectProductByGreaterPrice(price,page,size));
    }

    @GetMapping("/under-price")
    public ResponseEntity<?> selectProductUnderPrice(
            @RequestParam(value = "price", defaultValue = "0") Long price,
            @RequestParam(value = "pageIndex", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int size){
        return ResponseEntity.ok(productService.selectProductByLessPrice(price,page,size));
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateProduct(@RequestBody ProductRequestDto productRequestDto){
        productService.updateProduct(productRequestDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteProduct(@RequestBody ProductRequestDto productRequestDto){
        productService.deleteProduct(productRequestDto);
        return ResponseEntity.ok().build();
    }

}
