package com.example.dev.demo;

import com.example.dev.demo.domain.Member;
import com.example.dev.demo.domain.QMember;
import com.querydsl.core.Query;
import com.querydsl.core.QueryFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.persistence.*;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);

    }
}
