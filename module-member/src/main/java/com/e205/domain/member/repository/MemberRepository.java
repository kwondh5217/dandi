package com.e205.domain.member.repository;

import com.e205.domain.member.entity.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Integer> {

  boolean existsByEmail(String email);

  Optional<Member> findByEmail(String email);

  @Query("SELECT m FROM Member m WHERE m.id IN :ids")
  List<Member> findMembersByIds(@Param("ids") List<Integer> ids);
}
