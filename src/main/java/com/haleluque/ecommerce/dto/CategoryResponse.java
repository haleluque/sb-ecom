package com.haleluque.ecommerce.dto;

import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse extends PagingResponse {
    private List<CategoryDTO> content;
}
