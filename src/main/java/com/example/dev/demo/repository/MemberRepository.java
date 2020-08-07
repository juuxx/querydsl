package com.example.dev.demo.repository;

import com.example.dev.demo.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import sun.tools.tree.BooleanExpression;

import java.util.List;
import java.util.Optional;
@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, JpaSpecificationExecutor<Member> {

    List<Member> findByUserName(String userName);
    List<Member> findAll();


}
