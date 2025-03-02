package com.frankit.product_manage.controller;

import com.frankit.product_manage.config.TestSecurityConfig;
import com.frankit.product_manage.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(MemberController.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs // rest docs 자동 설정
@Import(TestSecurityConfig.class)
class AdminControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @Test
    void selectAllMember() {
    }

    @Test
    void selectMemberById() {
    }

    @Test
    void updateRoleMember() {
    }

    @Test
    void deleteMember() {
    }
}