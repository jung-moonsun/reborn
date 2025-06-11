package com.ms.reborn.domain.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {
    @NotBlank(message = "상품명은 필수입니다.")
    private String title;
    @NotBlank(message = "설명은 필수입니다.")
    private String description;
    @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
    private int price;
}
