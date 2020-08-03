package com.example.dev.demo.repository;

import com.example.dev.demo.DemoApplication;
import com.example.dev.demo.domain.Member;
import com.example.dev.demo.domain.QMember;
import com.example.dev.demo.domain.QTeam;
import com.querydsl.core.QueryFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import java.util.List;
@DataJpaTest
@RunWith(SpringRunner.class)
@Rollback(false)
//@SpringBootTest(classes = {DemoApplication.class})
public class MemberRepositoryTest {

    //@Autowired
    //JPAQueryFactory jpaQueryFactory;

    @Autowired
    EntityManager em;

    @Test
    public void jpql(){
        int age = 12;
        String query = "select m from Member m "
                    + "where m.age = :age";
        List<Member> result = em.createQuery(query, Member.class).getResultList();
    }

    @Test
    public void querydsl(){
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        //Member member1 = new Member(2L, "lee", 25);

        QMember member = QMember.member;
        String userName = "kim";
        List<Member> result = queryFactory.select(member)
                                             .from(member)
                                             .where(member.userName.eq(userName))
                                             .fetch();

        System.out.println("=============");
        for (Member m : result){
            System.out.println("=============" + m.getUserName());
        }

    }

    @Test
    public void joinTest(){
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        QMember member = QMember.member;
        QTeam team = QTeam.team;

        List<Member> list =
                    queryFactory.select(member)
                    .from(member)
                    .leftJoin(team)
                    .on(member.city.eq(team.city))
                    .where(team.city.eq("부산"))
                    .fetch();
    }
}