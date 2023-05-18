package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repositroy.OrderRepository;
import jpabook.jpashop.repositroy.OrderSearch;
import jpabook.jpashop.repositroy.OrderSimpleQueryDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * xToOne (ManyToOne, OneToOne)
 * Order
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
//        for (Order order : all) { 지연로딩 강제 초기화
//            order.getMember().getName();
//            order.getDelivery().getAddress();
//        }
        return all;
    }

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        //Order 2 row
        //N+1 -> 1 + 회원 N + 배송 N
        return orderRepository.findAllByString(new OrderSearch()).stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
    }

    /**
     * fetch join을 사용하여 N+1 문제 해결
     */
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        return orderRepository.findAllWithMemberDelivery()
                .stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
    }

    /**
     * JPA에서 DTO바로 조회. 일반 조인사용. (fetch join은 다 가져오기때문에)
     */
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4() {
       return orderRepository.findOrderDtos();
    }

    /**
     * V3,V4 정리
     * 엔티티를 DTO로 변환하거나, DTO로 바로 조회하는 두가지 방법은 각각 장단점이 있다. 둘중 상황에
     * 따라서 더 나은 방법을 선택하면 된다. 엔티티로 조회하면 리포지토리 재사용성도 좋고, 개발도 단순해진다.
     * 따라서 권장하는 방법은 다음과 같다.
     *
     * 쿼리 방식 선택 권장 순서
     * 1. 우선 엔티티를 DTO로 변환하는 방법을 선택한다.
     * 2. 필요하면 페치 조인으로 성능을 최적화 한다. 대부분의 성능 이슈가 해결된다.
     * 3. 그래도 안되면 DTO로 직접 조회하는 방법을 사용한다.
     * 4. 최후의 방법은 JPA가 제공하는 네이티브 SQL이나 스프링 JDBC Template을 사용해서 SQL을 직접 사용한다.
     */

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name =  order.getMember().getName();  //LAZY 강제 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); //LAZY 강제 초기화
        }
    }
}
