package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.item.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ItemUpdateTest {

    @Autowired
    EntityManager em;

    @Test
    void updateTest() {
        Book book = em.find(Book.class, 1L);

        //TX
        book.setName("TEST11");

        //변경감지 == dirty Checking
        //TX commit
    }
}
