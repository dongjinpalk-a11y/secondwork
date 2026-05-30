package kr.ac.hansung.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto {

    private String name;
    private int price;
    private String description;
    private int stock;
}
