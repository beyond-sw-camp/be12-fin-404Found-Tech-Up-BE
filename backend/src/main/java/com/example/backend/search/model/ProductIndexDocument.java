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
    private Long productidx;

    @Field(name="name", type= FieldType.Text)
    private String productname;
    @Field(name="price", type= FieldType.Double)
    private Double price;
    @Field(name="discount", type= FieldType.Integer)
    private Integer discount;
    @Field(name="brand", type= FieldType.Text)
    private String brand;
    @Field(name="stock", type= FieldType.Integer)
    private Integer stock;
    @Field(name="description", type= FieldType.Text)
    private String description;
    @Field(name= "category", type= FieldType.Text)
    private String category;
    @Field(name= "rating", type= FieldType.Double)
    private Double rating;
    @Field(name= "image", type= FieldType.Text)
    private String image;
    @Field(name= "reviews", type= FieldType.Integer)
    private Integer reviews;
}
