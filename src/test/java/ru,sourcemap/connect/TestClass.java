package ru.sourcemap.connect;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.sourcemap.connect.controller.user.LoginController;

@SpringBootTest
public class TestClass {

    @Autowired
    private LoginController loginController;

    @Test
    void name() {

        System.out.println("test: " + loginController.toString());

    }
}
