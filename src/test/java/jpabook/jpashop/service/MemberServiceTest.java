package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repositroy.MemberRepositoryOld;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepositoryOld memberRepositoryOld;
    @Test
    @DisplayName("회원가입")
    void join() {
        //given
        Member member = new Member();
        member.setName("kim");

        //when
        Long savedId = memberService.join(member);

        //then
        Member findMember = memberRepositoryOld.findOne(savedId);
        Assertions.assertThat(member.getId()).isEqualTo(findMember.getId());
    }

    @Test
    @DisplayName("중복_회원_예외")
    void validate() {
        //given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        //when
        memberService.join(member1);

        //then
        Assertions.assertThatThrownBy(() -> {
            memberService.join(member2);
        }).isInstanceOf(IllegalStateException.class);
    }
}