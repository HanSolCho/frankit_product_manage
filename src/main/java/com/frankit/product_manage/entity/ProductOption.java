package com.frankit.product_manage.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor( access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "product_option")
public class ProductOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;  // 옵션 이름 (예: 색상, 사이즈)
    @Enumerated(EnumType.STRING)
    private OptionType type;  // 옵션 타입 (입력 타입 / 선택 타입) input,select
    private Long price;  // 옵션 추가 금액
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;  // 해당 옵션이 속하는 상품

    @OneToMany(mappedBy = "productOption", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SelectOptionValue> selectOptionValueList;  // 선택 타입일 경우, 선택 가능한 값 목록

}
