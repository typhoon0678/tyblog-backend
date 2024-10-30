package store.tyblog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.tyblog.entity.Category;
import store.tyblog.entity.Member;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByMemberAndIsDeletedFalse(Member member);
}
