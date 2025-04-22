package com.example.backend.order.service;

import com.example.backend.cart.model.Cart;
import com.example.backend.cart.model.CartItem;
import com.example.backend.cart.repository.CartRepository;
import com.example.backend.global.exception.CartException;
import com.example.backend.global.exception.OrderException;
import com.example.backend.global.response.responseStatus.CartResponseStatus;
import com.example.backend.global.response.responseStatus.OrderResponseStatus;
import com.example.backend.order.model.OrderDetail;
import com.example.backend.order.model.Orders;
import com.example.backend.order.model.ShippingAddress;
import com.example.backend.order.model.dto.OrderCancelResponseDto;
import com.example.backend.order.model.dto.OrderRequestDto;
import com.example.backend.order.model.dto.OrderResponseDto;
import com.example.backend.order.repository.OrderRepository;
import com.example.backend.order.repository.ShippingAddressRepository;
import com.example.backend.product.model.Product;
import com.example.backend.product.repository.ProductRepository;
import com.example.backend.user.model.User;
import com.example.backend.user.repository.UserRepository;
import com.example.backend.util.HttpClientUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ShippingAddressRepository shippingAddressRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

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
        if (cartItems == null || cartItems.isEmpty()) {
            throw new CartException(CartResponseStatus.CART_IS_EMPTY);
        }

        double totalPrice = 0.0;
        List<OrderDetail> orderDetails = new ArrayList<>();

        // 각 장바구니 항목을 OrderDetail로 변환
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            int quantity = cartItem.getCartItemQuantity();
            // 주문 상세 금액: 단가 * 수량
            int price = product.getPrice().intValue();
            totalPrice += price * quantity;

            OrderDetail orderDetail = OrderDetail.builder()
                    .orderDetailQuantity(quantity)
                    .orderDetailPrice(price)
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
        for (OrderDetail detail : orderDetails) {
            detail.setOrders(order);
        }

        // 주문 엔티티 저장

        // 주문 완료 후 사용자의 장바구니 비우기
//        cart.getCartItems().clear();
//        cartRepository.save(cart);

        return orderRepository.save(order);
    }

    /**
     * 주문 결제 메서드 – PortOne API를 통해 결제 금액을 검증하고, 주문 상태를 업데이트합니다.
     *
     * @param user    로그인한 사용자
     * @param orderId 주문 고유 ID
     * @return 결제 완료된 Orders 엔티티
     */
    public Orders verify(User user, Long orderId, String paymentId) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException(OrderResponseStatus.ORDER_NOT_FOUND));
        if (!order.getUser().getUserIdx().equals(user.getUserIdx())) {
            throw new OrderException(OrderResponseStatus.ORDER_USER_MISMATCH);
        }

        // PortOne API를 통해 실제 결제한 금액 조회
        double portoneTotal = HttpClientUtil.getTotalAmount(paymentId);
        double orderTotal   = order.getOrderTotalPrice() + order.getShipCost();

        if (portoneTotal != orderTotal) {
            // 결제한 금액이랑 실제 금액이랑 다름
            order.setOrderStatus("UNPAID");
            throw new OrderException(OrderResponseStatus.ORDER_TOTAL_MISMATCH);
        }
        order.setOrderStatus("PAID");

        // 검증 성공 시 각 상품 해당 상품의 주문 수만큼 차감
        for (OrderDetail detail : order.getOrderDetails()) {
            Product product = detail.getProduct();
            int remaining = product.getStock() - detail.getOrderDetailQuantity();
            if (remaining < 0) {
                throw new OrderException(OrderResponseStatus.ORDER_STOCK_INSUFFICIENT);
            }
            product.setStock(remaining);
            productRepository.save(product);
        }

        // 검증 성공 시 쿠폰 사용했다면 해당 쿠폰 상태 수정(couponUsed = true)


        return orderRepository.save(order);
    }

    // 주문 취소
    public OrderCancelResponseDto cancelOrder(User user, Long orderId) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException(OrderResponseStatus.ORDER_NOT_FOUND));
        if (!user.getIsAdmin()) {
            throw new OrderException(OrderResponseStatus.ORDER_USER_MISMATCH);
        }
        order.setOrderStatus("CANCELED");
        orderRepository.save(order);
        return OrderCancelResponseDto.from(orderId, "CANCELED");
    }

    // 주문 내역 조회 (사용자 기준)
    public List<Orders> getOrderHistory(User user) {
        return orderRepository.findAll().stream()
                .filter(order -> order.getUser().getUserIdx().equals(user.getUserIdx()))
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
        order.setOrderStatus("REFUND_REQUESTED");
        orderRepository.save(order);
        return OrderCancelResponseDto.from(orderId, "REFUND_REQUESTED");
    }

    public List<OrderResponseDto> getOrdersByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()-> new OrderException(OrderResponseStatus.ORDER_NOT_FOUND));
        return orderRepository.findAllByUserOrderByOrderDateDesc(user).stream().map(OrderResponseDto::from).toList();
    }
}
