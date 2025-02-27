package com.frankit.product_manage.entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor( access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "select_option_value")
public class SelectOptionValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;  // 선택 가능한 값 (예: 빨강, 파랑, 10kg 등) 해당 명칭을 name으로 짓는게 맞을지. value가 더 적절해보이기도.. 고민 필요
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_option_id")
    private ProductOption productOption;  // 해당 값이 속한 상품 옵션

}
