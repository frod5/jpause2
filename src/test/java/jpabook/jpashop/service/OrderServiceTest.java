package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repositroy.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    @DisplayName("상품 주문")
    void order() {
        //given
        Member member = createMember();

        em.persist(member);

        Book book = createBook("시골 JPA", 10000, 10);
        em.persist(book);

        int orderCount = 2;

        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertThat(getOrder.getStatus()).isEqualTo(OrderStatus.ORDER);  //주문 상태 체크
        assertThat(getOrder.getOrderItems().size()).isEqualTo(1); //주문 상품 종류 체크
        assertThat(getOrder.getTotalPrice()).isEqualTo(10000 * orderCount); // 주문 가격 체크
        assertThat(book.getStockQuantity()).isEqualTo(8); // 재고 체크
    }
    @Test
    @DisplayName("재고_수량_초과_예외")
    void validateStock() {
        //given
        Member member = createMember();
        em.persist(member);
        Book book = createBook("시골 JPA", 10000, 10);
        em.persist(book);

        int orderCount = 11;

        assertThatThrownBy(() -> {
            orderService.order(member.getId(), book.getId(), orderCount);
        }).isInstanceOf(NotEnoughStockException.class);
    }
    @Test
    @DisplayName("주문 취소")
    void cancelOrder() {
        //given
        Member member = createMember();
        em.persist(member);
        Book book = createBook("시골 JPA", 10000, 10);
        em.persist(book);

        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //when
        orderService.cancelOrder(orderId);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertThat(getOrder.getStatus()).isEqualTo(OrderStatus.CANCEL);
        assertThat(10).isEqualTo(book.getStockQuantity());
    }


    private Book createBook(String name, int price, int stock) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stock);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("member1");
        member.setAddress(new Address("서울", "강가","123-123"));
        return member;
    }
}