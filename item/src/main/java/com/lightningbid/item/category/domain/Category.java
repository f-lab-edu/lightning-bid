package com.lightningbid.item.category.domain;

import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    private Long id;
    private String name;
}
