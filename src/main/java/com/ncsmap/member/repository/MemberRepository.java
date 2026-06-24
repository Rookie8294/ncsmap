package com.ncsmap.member.repository;

import com.ncsmap.member.entity.Member;
import com.ncsmap.member.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<Member> findByProviderAndProviderId(Provider provider, String providerId);
}
