package com.lightningbid.item.domain.model;

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
