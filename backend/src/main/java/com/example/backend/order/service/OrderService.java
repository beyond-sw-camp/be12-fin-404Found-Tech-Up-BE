package com.example.backend.order.service;

import com.example.backend.cart.model.Cart;
import com.example.backend.cart.model.CartItem;
import com.example.backend.cart.repository.CartRepository;
import com.example.backend.coupon.model.UserCoupon;
import com.example.backend.coupon.repository.UserCouponRepository;
import com.example.backend.global.exception.CartException;
import com.example.backend.global.exception.OrderException;
import com.example.backend.global.exception.UserException;
import com.example.backend.global.response.responseStatus.CartResponseStatus;
import com.example.backend.global.response.responseStatus.OrderResponseStatus;
import com.example.backend.global.response.responseStatus.UserResponseStatus;
import com.example.backend.notification.service.NotificationProducerService;
import com.example.backend.order.model.OrderDetail;
import com.example.backend.order.model.Orders;
import com.example.backend.order.model.ShippingAddress;
import com.example.backend.order.model.dto.OrderCancelResponseDto;
import com.example.backend.order.model.dto.OrderRequestDto;
import com.example.backend.order.model.dto.OrderResponseDto;
import com.example.backend.order.model.dto.OrderVerifyRequestDto;
import com.example.backend.order.repository.OrderRepository;
import com.example.backend.order.repository.ShippingAddressRepository;
import com.example.backend.product.model.Product;
import com.example.backend.product.repository.ProductRepository;
import com.example.backend.user.model.User;
import com.example.backend.user.repository.UserRepository;
import com.example.backend.util.HttpClientUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserCouponRepository userCouponRepository;
    private final ShippingAddressRepository shippingAddressRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final RedissonClient redissonClient;
    private final NotificationProducerService notificationProducerService;

    @Value("${portone.store-id}")
    private String storeId;

    @Value("${portone.channel-key}")
    private String channelKey;

    /**
     * 주문 기능 구현
     * 1. 로그인한 사용자의 장바구니를 조회
     * 2. 각 CartItem으로부터 OrderDetail을 생성
     * 3. 주문의 총 금액 및 주문 날짜, 주문 상태를 설정하여 Orders 엔티티 생성
     * 4. 주문 후 장바구니를 비움
     */
    public Orders placeOrder(User user, OrderRequestDto dto) {
        // 사용자의 주문 정보(이름, 주소, 전화번호 등)을 저장 <- User.java 참조
        ShippingAddress address = shippingAddressRepository.findByUser(user)
                .map(existing -> {
                    existing.setRecipientName(dto.getRecipientName());
                    existing.setAddressLine1(dto.getAddress());
                    existing.setAddressLine2(dto.getAddressDetail());
                    existing.setPostalCode(dto.getPostalCode());
                    existing.setPhone(dto.getPhone());
                    existing.setEmail(dto.getEmail());
                    existing.setMemo(dto.getMemo());
                    return existing;
                })
                .orElseGet(() -> ShippingAddress.builder()
                        .recipientName(dto.getRecipientName())
                        .addressLine1(dto.getAddress())
                        .addressLine2(dto.getAddressDetail())
                        .postalCode(dto.getPostalCode())
                        .phone(dto.getPhone())
                        .email(dto.getEmail())
                        .memo(dto.getMemo())
                        .user(user)
                        .build()
                );

        shippingAddressRepository.save(address);

        // 사용자의 장바구니를 조회 (CartRepository에서 사용자 기준으로 찾음)
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new CartException(CartResponseStatus.CART_IS_EMPTY));
        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems.isEmpty()) {
            throw new CartException(CartResponseStatus.CART_IS_EMPTY);
        }

        int totalPrice = 0;
        List<OrderDetail> orderDetails = new ArrayList<>();

        // 쿠폰을 사용하였을 경우
        UserCoupon userCoupon = null;
        if (dto.getCouponIdx() != null) {
            userCoupon = userCouponRepository.findById(dto.getCouponIdx()).orElse(null);
        }

        // 각 장바구니 항목을 OrderDetail로 변환
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            int quantity = cartItem.getCartItemQuantity();
            int discount = product.getDiscount() != null ? product.getDiscount() : 0;
            double unitPrice = product.getPrice();
            double basePrice = unitPrice * (1 - discount / 100.0);

            UserCoupon detailCoupon = null;
            if (userCoupon != null
                    && product.getProductIdx().equals(userCoupon.getCoupon().getProduct().getProductIdx())) {
                double couponAmt = unitPrice * userCoupon.getCoupon().getCouponDiscountRate() / 100.0;
                basePrice -= couponAmt;
                detailCoupon = userCoupon;
                userCoupon = null;
            }

            totalPrice += (int)Math.round(basePrice * quantity);

            OrderDetail orderDetail = OrderDetail.builder()
                    .orderDetailQuantity(quantity)
                    .orderDetailPrice((int)unitPrice)
                    .orderDetailDiscount(discount)
                    .userCoupon(detailCoupon)
                    .product(product)
                    .build();

            orderDetails.add(orderDetail);
        }

        // Orders 엔티티 생성
        Orders order = Orders.builder()
                .orderTotalPrice(totalPrice)
                .shipCost(dto.getShipCost())
                .paymentMethod(dto.getPaymentMethod())
                .shippingMethod(dto.getShippingMethod())
                .orderStatus("PLACED")
                .orderDate(new Date())
                .storeId(storeId)
                .channelKey(channelKey)
                .user(user)
                .orderDetails(orderDetails)
                .build();

        // 양방향 매핑을 위해 OrderDetail에도 Order를 세팅
        orderDetails.forEach(d -> d.setOrders(order));

        // 주문 완료 후 장바구니 비우기
        cart.getCartItems().clear();
        cartRepository.save(cart);

        // 주문 엔티티 저장
        return orderRepository.save(order);
    }

    /**
     * 주문 결제 메서드 – PortOne API를 통해 결제 금액을 검증하고, 주문 상태를 업데이트합니다.
     *
     * @param user    로그인한 사용자
     * @param orderId 주문 고유 ID
     * @return 결제 완료된 Orders 엔티티
     */
    public Orders verify(User user, Long orderId, OrderVerifyRequestDto dto) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException(OrderResponseStatus.ORDER_NOT_FOUND));
        if (!order.getUser().getUserIdx().equals(user.getUserIdx())) {
            throw new OrderException(OrderResponseStatus.ORDER_USER_MISMATCH);
        }

        // PortOne API를 통해 실제 결제한 금액 조회
        double portoneTotal = HttpClientUtil.getTotalAmount(dto.getPaymentId());
        double orderTotal = order.getOrderTotalPrice() + order.getShipCost();
      
        //50000원 이하인데 배송비가 무료면 해킹임
        if (order.getOrderTotalPrice() < 50000 && order.getShipCost() == 0) {
            throw new OrderException(OrderResponseStatus.ORDER_TOTAL_MISMATCH);
        }

        if (portoneTotal != orderTotal) {
            // 결제한 금액이랑 실제 금액이랑 다름
            order.setOrderStatus("UNPAID");
            throw new OrderException(OrderResponseStatus.ORDER_TOTAL_MISMATCH);
        }
        order.setOrderStatus("PAID");
        order.setPaymentId(dto.getPaymentId());
        orderRepository.save(order);

        // 검증 성공 시 각 상품 해당 상품의 주문 수만큼 차감
        for (OrderDetail detail : order.getOrderDetails()) {
            Long productId = detail.getProduct().getProductIdx();
            RLock lock = redissonClient.getLock("lock:product:" + productId);

            // 블로킹 락 획득
            lock.lock();

            try {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new OrderException(OrderResponseStatus.ORDER_STOCK_INSUFFICIENT));

                int remaining = product.getStock() - detail.getOrderDetailQuantity();
                if (remaining < 0) {
                    throw new OrderException(OrderResponseStatus.ORDER_STOCK_INSUFFICIENT);
                }

                product.setStock(remaining);
                productRepository.save(product);

            } finally {
                // 락 해제
                lock.unlock();
            }
        }

        // 검증 성공 시 쿠폰 사용했다면 해당 쿠폰 상태 수정(couponUsed = true)
        if (dto.getCouponIdx() != null) {
            userCouponRepository.findById(dto.getCouponIdx())
                    .ifPresent(userCoupon -> {
                        userCoupon.setCouponUsed(true);
                        userCouponRepository.save(userCoupon);
                    });
        }

        // 주문 완료 알림 발송 – 첫 상품 이름 기준
//        String firstProductName = order.getOrderDetails().get(0).getProduct().getName();
//        notificationProducerService.sendOrderCompleteNotification(orderId, firstProductName, user.getUserIdx());

        return orderRepository.save(order);
    }

    // 주문 취소
    @Transactional
    public Orders cancelOrder(User user, Long orderId) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException(OrderResponseStatus.ORDER_NOT_FOUND));
        // 관리자가 아니라면 취소 못함.
        if (!user.getIsAdmin()) {
            throw new UserException(UserResponseStatus.UNIDENTIFIED_ROLE);
        }

        String status = order.getOrderStatus();
        if ("CANCELED".equals(status)) {
            throw new OrderException(OrderResponseStatus.ORDER_ALREADY_CANCELED);
        }
        if (!Objects.equals("REFUND_REQUESTED", status)) {
            // 요청 받은 상태가 아니라면 환불하지 않는다.
            throw new OrderException(OrderResponseStatus.ORDER_CANNOT_CANCEL);
        }

        // 재고 복원
        for (OrderDetail detail : order.getOrderDetails()) {
            Product p = detail.getProduct();
            p.setStock(p.getStock() + detail.getOrderDetailQuantity());
            productRepository.save(p);
        }

        // 결제된 상태가 아니라면 환불 요청안되므로 무조건 환불
        String paymentId = order.getPaymentId(); // assume you stored it
        boolean refundOk = HttpClientUtil.requestRefund(paymentId);
        if (!refundOk) {
            throw new OrderException(OrderResponseStatus.ORDER_REFUND_FAILED);
        }

        // 쿠폰을 사용했다면, 사용한 쿠폰 롤백
        if(order.getCouponIdx() != null) {
            userCouponRepository.findById(order.getCouponIdx())
                    .ifPresent(userCoupon -> {userCoupon.setCouponUsed(false);});
        }

        // 최종 상태 업데이트
        order.setOrderStatus("CANCELED");
        return orderRepository.save(order);
    }

    // 주문 내역 조회 (사용자 기준)
    public List<OrderResponseDto> getOrderHistory(User user) {
        List<Orders> orders = orderRepository.findAllByUserWithDetails(user.getUserIdx());
        return orders.stream()
                .map(OrderResponseDto::from)
                .collect(Collectors.toList());
    }

    // 주문 상세 조회
    public Orders getOrderDetail(User user, Long orderId) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException(OrderResponseStatus.ORDER_NOT_FOUND));
        if (!order.getUser().getUserIdx().equals(user.getUserIdx())) {
            throw new OrderException(OrderResponseStatus.ORDER_USER_MISMATCH);
        }
        return order;
    }

    // 환불 요청
    public OrderCancelResponseDto requestRefund(User user, Long orderId) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException(OrderResponseStatus.ORDER_NOT_FOUND));
        if (!order.getUser().getUserIdx().equals(user.getUserIdx())) {
            throw new OrderException(OrderResponseStatus.ORDER_USER_MISMATCH);
        }

        // 결제된 상태가 아니라면 환불 요청 받지 않음
        String status = order.getOrderStatus();
        if (!"PAID".equals(status)) {
            throw new OrderException(OrderResponseStatus.ORDER_CANCEL_FAIL);
        }

        order.setOrderStatus("REFUND_REQUESTED");
        orderRepository.save(order);
        return OrderCancelResponseDto.from(orderId, "REFUND_REQUESTED");
    }

    public List<OrderResponseDto> getOrdersByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new OrderException(OrderResponseStatus.ORDER_NOT_FOUND));
        return orderRepository.findAllByUserOrderByOrderDateDesc(user).stream().map(OrderResponseDto::from).toList();
    }
}
