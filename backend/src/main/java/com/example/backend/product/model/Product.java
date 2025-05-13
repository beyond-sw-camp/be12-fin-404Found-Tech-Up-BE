package com.example.backend.product.model;

import com.example.backend.cart.model.CartItem;
import com.example.backend.coupon.model.Coupon;
import com.example.backend.order.model.OrderDetail;
import com.example.backend.product.model.dto.ProductRequestDto;
import com.example.backend.product.model.spec.*;
import com.example.backend.review.model.Review;
import com.example.backend.search.model.ProductIndexDocument;
import com.example.backend.user.model.UserProduct;
import com.example.backend.wishlist.model.Wishlist;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productIdx;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false)
    private Double price;
    private Integer discount;
    @Column(nullable = false)
    private String brand;
    @Column(nullable = false)
    private Integer stock;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private String category;
    private Double rating;

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
    @BatchSize(size = 100)
    @OneToMany(mappedBy = "products")
    private List<UserProduct> userProducts;

    // 쿠폰과 일대다 맵핑
    @BatchSize(size = 100)
    @OneToMany(mappedBy = "product")
    private List<Coupon> coupons;

    // 주문 상세 정보와 일대다 맵핑
    @BatchSize(size = 100)
    @OneToMany(mappedBy = "product")
    private List<OrderDetail> orderDetails;

    // 카트아이템과 일대다 맵핑
    @BatchSize(size = 100)
    @OneToMany(mappedBy = "product")
    private List<CartItem> cartItems;

    // 위시리스트와 일대다 맵핑
    @BatchSize(size = 100)
    @OneToMany(mappedBy = "product")
      private List<Wishlist> wishlists;

    public void update(ProductRequestDto dto) {
        this.name = dto.getName();
        this.price = dto.getPrice();
        this.discount = dto.getDiscount();
        this.brand = dto.getBrand();
        this.stock = dto.getStock();
        this.description = dto.getDescription();
        this.category = dto.getCategory();
    }

    public ProductIndexDocument toSearchDocument() {
        return ProductIndexDocument.builder()
                .productidx(productIdx)
                .brand(brand)
                .description(description)
                .category(category)
                .stock(stock)
                .rating(rating)
                .discount(discount)
                .productname(name)
                .image(images != null ? "https://"+ images.get(0).getImageUrl() : "")
                .price(price)
                .build();
    }
}
