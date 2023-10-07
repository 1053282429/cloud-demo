package org.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: 边俊超
 * @Date: 2023/10/7 17:17
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/get")
    public String getUser() {
        return "11111";
    }

}
