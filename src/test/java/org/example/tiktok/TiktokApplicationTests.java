package org.example.tiktok;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@MapperScan("org.example.tiktok.mapper")
class TiktokApplicationTests {

    @Test
    void contextLoads() {
    }

}
