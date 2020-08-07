package com.example.dev.demo.repository;

import com.example.dev.demo.domain.Member;
import org.springframework.data.jpa.domain.Specification;

import javax.swing.text.html.HTMLDocument;

public class MemberSpecs {
    public static Specification<Member> withName(String userName){
        return (Specification<Member>)((root, query, builder) ->
                    builder.equal(root.get("userName"), userName)
        );
    }
}
