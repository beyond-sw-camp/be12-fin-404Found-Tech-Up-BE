package com.example.backend.search.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Document(indexName="product")
public class ProductIndexDocument {

    @Id
    @Field(type= FieldType.Long)
    private Long idx;

    @Field(type= FieldType.Text)
    private String productName;
    @Field(type= FieldType.Double)
    private Double price;
    @Field(type= FieldType.Integer)
    private Integer discount;
    @Field(type= FieldType.Text)
    private String brand;
    @Field(type= FieldType.Integer)
    private Integer stock;
    @Field(type= FieldType.Text)
    private String description;
    @Field(type= FieldType.Text)
    private String category;
    @Field(type= FieldType.Double)
    private Double rating;
    @Field(type= FieldType.Text)
    private String image;
}
