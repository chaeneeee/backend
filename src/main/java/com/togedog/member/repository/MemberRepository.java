package com.togedog.member.repository;

import com.togedog.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    Optional<Member> findByPhone(String phone);

    Optional<Member> findByNickName(String nickName);

    boolean existsByEmail(String email);

    boolean findByEmailAndPhoneAndName(String email, String Phone, String Name);

    List<Member> findByMemberIdOrMemberId(Long MemberId1, Long MemberId2);
}
