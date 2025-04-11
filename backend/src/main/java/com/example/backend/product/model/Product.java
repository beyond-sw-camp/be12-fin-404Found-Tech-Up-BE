package com.example.backend.product.model;

import com.example.backend.cart.model.CartItem;
import com.example.backend.coupon.model.Coupon;
import com.example.backend.order.model.OrderDetail;
import com.example.backend.product.model.dto.ProductRequestDto;
import com.example.backend.product.model.spec.*;
import com.example.backend.review.model.Review;
import com.example.backend.user.model.UserProduct;
import com.example.backend.wishlist.model.Wishlist;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productIdx;
    private String name;
    private Double price;
    private String brand;
    private Integer stock;
    private String description;
    private String category;

    @OneToMany(mappedBy = "product")
    private List<ProductImage> images;

    @OneToOne(mappedBy = "product")
    private CpuSpec cpuSpec;

    @OneToOne(mappedBy = "product")
    private GpuSpec gpuSpec;

    @OneToOne(mappedBy = "product")
    private RamSpec ramSpec;

    @OneToOne(mappedBy = "product")
    private SsdSpec ssdSpec;

    @OneToOne(mappedBy = "product")
    private HddSpec hddSpec;

    // 리뷰와 일대다 맵핑
    @OneToMany(mappedBy = "product")
    private List<Review> reviews;

    // 사용자의 제품과 일대다 맵핑
    @OneToMany(mappedBy = "products")
    private List<UserProduct> userProducts;

    // 쿠폰과 일대다 맵핑
    @OneToMany(mappedBy = "product")
    private List<Coupon> coupons;

    // 주문 상세 정보와 일대다 맵핑
    @OneToMany(mappedBy = "product")
    private List<OrderDetail> orderDetails;

    // 카트아이템과 일대다 맵핑
    @OneToMany(mappedBy = "product")
    private List<CartItem> cartItems;

    // 위시리스트와 일대다 맵핑
    @OneToMany(mappedBy = "product")
      private List<Wishlist> wishlists;

    public void update(ProductRequestDto dto) {
        this.name = dto.getName();
        this.price = dto.getPrice();
        this.brand = dto.getBrand();
        this.stock = dto.getStock();
        this.description = dto.getDescription();
        this.category = dto.getCategory();
    }

}
