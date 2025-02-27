package com.frankit.product_manage.repository;

import com.frankit.product_manage.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("멤버 등록 Test")
    void saveMemberTest(){
        /*
        given
         */
        Member member = Member.builder().id("hi1").password("123").build();

        /*
        when
         */
        Member result1 = memberRepository.save(member);

        /*
        then
         */
        assertThat(result1.getId()).isEqualTo(member.getId());
        assertThat(result1.getPassword()).isEqualTo(member.getPassword());

    }

    @Test
    @DisplayName("멤버 조회 Test")
    void selectMemberTest(){
        /*
        given
         */
        Member member = Member.builder().id("hi1").password("123").build();
        memberRepository.save(member);

        /*
        when
         */
        List<Member> result = memberRepository.findAll();

        /*
        then
         */
        assertThat(result.size()).isEqualTo(1);
        assertThat(member.getId()).isEqualTo(result.get(0).getId());

    }

    @Test
    @DisplayName("멤버 변경 Test")
    void updateMemberTest(){
        /*
        given
         */
        Member member = Member.builder().id("hi1").password("123").build();
        memberRepository.save(member);

        List<Member> result = memberRepository.findAll();

        assertThat(member.getId()).isEqualTo(result.get(0).getId());

        Member updateMember = result.get(0);
        updateMember.setId("hi2");
        memberRepository.save(updateMember);

        /*
        when
         */
        List<Member> updateResult = memberRepository.findAll();
        /*
        then
         */
        assertThat(updateMember.getId()).isEqualTo(updateResult.get(0).getId());
        assertThat("hi1").isNotEqualTo(updateResult.get(0).getId());

    }

    @Test
    @DisplayName("멤버 삳제 Test")
    void deleteMemberTest(){
        /*
        given
         */
        Member member = Member.builder().id("hi1").password("123").build();
        Member member2 = Member.builder().id("hi2").password("123").build();
        memberRepository.save(member);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findAll();

        assertThat(result.size()).isEqualTo(2);
        assertThat(member.getId()).isEqualTo(result.get(0).getId());

        /*
        when
         */
        memberRepository.deleteById(2l); // Member 변수에서 key(id)는 num, 변수로 저장되는 id는 memberId
        List<Member> deleteResult = memberRepository.findAll();

        /*
        then
         */
        assertThat(deleteResult.size()).isEqualTo(1);
        assertThat(member.getId()).isEqualTo(deleteResult.get(0).getId());

    }

    @Test
    @DisplayName("멤버 Id 별 조회 Test")
    void findById() {
         /*
        given
         */
        Member member = Member.builder().id("hi1").password("123").build();
        Member member2 = Member.builder().id("hi2").password("123").build();
        Member member3 = Member.builder().id("hi3").password("123").build();
        memberRepository.save(member);
        memberRepository.save(member2);
        memberRepository.save(member3);

        /*
        when
         */
        Optional<Member> result = memberRepository.findById(member.getId());

        /*
        then
         */
        assertThat(member.getId()).isEqualTo(result.get().getId());
    }
}