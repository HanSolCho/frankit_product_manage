package com.frankit.product_manage;

import com.frankit.product_manage.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(TestSecurityConfig.class)
@SpringBootTest
class ProductManageApplicationTests {

    @Test
    void contextLoads() {
    }

}
