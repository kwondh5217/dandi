package com.e205.domain.member.repository;

import com.e205.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Integer> {
  boolean existsByEmail(String email);
  Optional<Member> findByEmail(String email);
}
