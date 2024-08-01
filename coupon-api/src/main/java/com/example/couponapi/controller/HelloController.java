package com.example.couponapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class HelloController {
    @GetMapping("/hello")
    public String hello() throws InterruptedException {
        Thread.sleep(500); //400 rps 정도 나온다.
        return "Hello, World!";
    }// 초당 2건을 처리 *N(서버에서 동시에 처리할 수 있느 수(톱켓에서 스레드풀의 맥스는 200으로 잡혀있음)) = 400 정도 나옴
}
