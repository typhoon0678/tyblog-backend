package store.tyblog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import store.tyblog.entity.Category;
import store.tyblog.entity.Member;
import store.tyblog.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByMemberAndIsDeletedFalse(Member member, Pageable pageable);

    Page<Post> findByMemberAndCategoriesEqualsAndIsDeletedFalse(Member member, Category category, Pageable pageable);

    Page<Post> findAllByIsDeletedFalse(Pageable pageable);
}
