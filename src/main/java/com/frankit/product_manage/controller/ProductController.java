package com.frankit.product_manage.controller;

import com.frankit.product_manage.Dto.Request.ProductRegisterRequestDto;
import com.frankit.product_manage.Dto.Request.ProductRequestDto;
import com.frankit.product_manage.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**todo: 맵핑을 각 controller별 다르게 주기 위해 상단에 /product , /member 같은 거 추가 공통 url은 아예 최상단으로 뺄수있는지 확ㅇ니
 *
 * 상품 조회 : 배달비, 금액 관련해서 select 추가 ( 금액 ~만원 이상, 배달비 ~원 이하 이런식으로)
 */

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
    public ResponseEntity<?> selectAllProduct(@RequestParam(value = "page", defaultValue = "0") int page,
                                              @RequestParam(value = "size", defaultValue = "10") int size){
        return ResponseEntity.ok(productService.selectAllProduct(page, size));
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
