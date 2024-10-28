package store.tyblog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.tyblog.entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByUsername(String username);
}
