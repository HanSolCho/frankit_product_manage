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
        Member member = Member.builder().id("id").password("123").role("USER").build();

        /*
        when
         */
        Member result = memberRepository.save(member);

        /*
        then
         */
        assertThat(result.getId()).isEqualTo(member.getId());
        assertThat(result.getPassword()).isEqualTo(member.getPassword());

    }

    @Test
    @DisplayName("멤버 조회 Test")
    void selectMemberTest(){
        /*
        given
         */
        Member member = Member.builder().id("id").password("123").role("USER").build();
        memberRepository.save(member);

        /*
        when
         */
        List<Member> result = memberRepository.findAll();

        /*
        then
         */
        assertThat(result.size()).isEqualTo(1);
        assertThat(member.getId()).isEqualTo(result.getFirst().getId());

    }

    @Test
    @DisplayName("멤버 변경 Test")
    void updateMemberTest(){
        /*
        given
         */
        Member member = Member.builder().id("id").password("123").role("USER").build();
        memberRepository.save(member);

        List<Member> result = memberRepository.findAll();

        assertThat(member.getId()).isEqualTo(result.getFirst().getId());

        Member updateMember = result.getFirst();
        updateMember.setId("updateId");
        memberRepository.save(updateMember);

        /*
        when
         */
        List<Member> updateResult = memberRepository.findAll();
        /*
        then
         */
        assertThat(updateMember.getId()).isEqualTo(updateResult.getFirst().getId());
        assertThat("id").isNotEqualTo(updateResult.getFirst().getId());

    }

    @Test
    @DisplayName("멤버 삭제 Test")
    void deleteMemberTest(){
        /*
        given
         */
        Member member = Member.builder().id("id").password("123").role("USER").build();
        Member member2 = Member.builder().id("id2").password("123").role("USER").build();
        memberRepository.save(member);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findAll();

        assertThat(result.size()).isEqualTo(2);
        assertThat(member.getId()).isEqualTo(result.getFirst().getId());

        /*
        when
         */
        memberRepository.deleteById(2L); // Member 변수에서 key(id)는 num, 변수로 저장되는 id는 memberId
        List<Member> deleteResult = memberRepository.findAll();

        /*
        then
         */
        assertThat(deleteResult.size()).isEqualTo(1);
        assertThat(member.getId()).isEqualTo(deleteResult.getFirst().getId());

    }

    @Test
    @DisplayName("멤버 Id 별 조회 Test")
    void findById() {
         /*
        given
         */
        Member member = Member.builder().id("id").password("123").role("USER").build();
        Member member2 = Member.builder().id("id2").password("123").role("USER").build();
        Member member3 = Member.builder().id("id3").password("123").role("USER").build();
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