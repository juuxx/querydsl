package com.example.dev.demo.repository;

import com.example.dev.demo.DemoApplication;
import com.example.dev.demo.domain.Member;
import com.example.dev.demo.domain.QMember;
import com.example.dev.demo.domain.QTeam;
import com.example.dev.demo.dto.MemberInfoDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DataJpaTest
@RunWith(SpringRunner.class)
//@Rollback(false)
//@SpringBootTest(classes = {DemoApplication.class})
public class MemberRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    MemberRepository repository;

    @Test //test -1
    public void querydsl(){
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        QMember member = QMember.member;
        String userName = "kim";
        /*List<Member> result = queryFactory.select(member)
                                             .from(member)
                                             .where(member.userName.eq(userName))
                                             .fetch();*/

        /*QueryResults<Member> results = queryFactory.select(member)
                                                    .from(member)
                                                    .where(member.userName.eq(userName))
                                                    .fetchResults();*/
        List<Member> result = (List<Member>) queryFactory.from(member).fetch();

    }

    @Test //test -2
    public void joinTest(){
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        QMember member = QMember.member;
        QTeam team = QTeam.team;

/*        List<Member> list =
                queryFactory.select(member)
                        .from(member)
                        .innerJoin(team)
                        .on(member.city.eq(team.city))
                        .fetch();*/
        /*
        List<Member> list =
                queryFactory.selectFrom(member)
                        //.where(member.userName.eq("kim")) // 1. 단일 조건
                        //.where(member.userName.eq("kim"), member.city.eq("서울")) // 2. 복수 조건. and로 묶임
                        //.where(member.userName.eq("kim").or(member.city.eq("서울"))) // 3. 복수 조건. and나 or를 직접 명시할 수 있음
                        .where(
                                (member.userName.eq("kim").and(member.city.eq("서울")))
                                .or
                                (member.userName.eq("kim").and(member.city.eq("부산")))
                               )
                        .fetch();*/

        List<Member> list =
                    queryFactory.select(member)
                    .from(member)
                    .leftJoin(team)
                    .on(member.city.eq(team.city))
                    .where(team.city.eq("부산"))
                    .fetch();
    }

    @Test //test -3
    public void grouping(){
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        QMember member = QMember.member;
        QTeam team = QTeam.team;

        List<String> foundCities =
                queryFactory.from(member)
                        .select(member.city)
                        .groupBy(member.city)
                        .fetch();

        /*List<String> foundNames =
                queryFactory.select(member.userName)
                        .from(member)
                        .groupBy(member.age, member.userName)
                        .having(member.age.between(13,14))
                        .fetch();*/
    }

    @Test //test-3
    public void orderBy(){
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        QMember member = QMember.member;
        QTeam team = QTeam.team;

/*        List<Member> foundMembers =
                queryFactory.selectFrom(member)
                        .orderBy(member.id.asc(), member.userName.desc())
                        .fetch();*/

        List<Tuple> memeberList = //다중 프로젝션 할 경우 Tuple class 로 받을 수 있다.
                    queryFactory.select(member.userName, member.city, member.age, team.teamName)
                                .from(member)
                                .innerJoin(team)
                                .on(member.city.eq(team.city))
                                .orderBy(member.id.desc(), team.teamName.desc())
                                .fetch();

    }

    @Test
    public void paging(){
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        QMember member = QMember.member;

        QueryResults<Member> result =
                queryFactory.selectFrom(member)
                        .offset(0)
                        .limit(3)
                        .fetchResults();
        List<Member> foundMembers = result.getResults(); // 조회된 member
        long total = result.getTotal(); // 전체 개수
        long offset = result.getOffset(); // offset
        long limit = result.getLimit(); // limit
    }

    @Test
    public void createBean(){
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        QMember member = QMember.member;
        QTeam team = QTeam.team;

        List<MemberInfoDto> memeberList =
                queryFactory.select(Projections.bean(MemberInfoDto.class,
                                member.userName, member.city, member.age, team.teamName))
                        .from(member)
                        .innerJoin(team)
                        .on(member.city.eq(team.city))
                        .orderBy(member.id.desc(), team.teamName.desc())
                        .fetch();

    }

    @Test
    public void subQuery(){
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        QMember member = QMember.member;
        QTeam team = QTeam.team;

        List<Tuple> foundMembers =
                queryFactory.select(member.userName, member.city)
                        .from(member)
                        .where(member.city.in(
                                JPAExpressions.select(team.city)
                                        .from(team)
                        ))
                        .fetch();
    }

    @Test
    public void dynamicWhere(){
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QMember member = QMember.member;

        Map<String, String> param = new HashMap<>();
        param.put("userName", "kim");
        param.put("city", "서");


        BooleanBuilder builder = new BooleanBuilder();
        if(param.get("userName") != null){
            builder.and(member.userName.eq(param.get("userName")));
        }
        if(param.get("city") != null){
            builder.and(member.city.contains(param.get("city")));
        }

        List<Member> list =
                queryFactory.selectFrom(member)
                        .where(builder)
                        .fetch();
    }

    @Test
    public void booleanExpress(){
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QMember member = QMember.member;
        String userName = "kim";
        queryFactory.from(member).where(isEqualName(userName))
                .fetch();
    }

    public static BooleanExpression isEqualName(String userName){
        QMember member = QMember.member;
        if(StringUtils.isEmpty(userName)){
            return null;
        }
        return member.userName.eq(userName);
    }

    @Test
    public void updateQuery(){
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QMember member = QMember.member;
        JPAUpdateClause updateClause = new JPAUpdateClause(em, member);
        long count = updateClause.where(member.userName.eq("kim"))
                .set(member.age, 20)
                .execute();
/*        queryFactory.update(member).where(member.userName.eq("kim"))
                                 .set(member.age, 20)
                                 .execute();*/


    }

    @Test
    public void deletQuqery(){
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QMember member = QMember.member;

        JPADeleteClause deleteClause = new JPADeleteClause(em, member);
/*        long count = deleteClause.where(member.userName.eq("kim"))
                                 .execute();*/

        queryFactory.delete(member)
                    .where(member.userName.eq("kim"))
                    .execute();
    }

    @Test
    public void specTest(){
        String userName = "kim";
        repository.findAll(MemberSpecs.withName(userName));
    }

}