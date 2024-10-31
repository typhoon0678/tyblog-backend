package store.tyblog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import store.tyblog.dto.post.PostRequestDto;
import store.tyblog.dto.post.PostResponseDto;
import store.tyblog.entity.Category;
import store.tyblog.entity.Member;
import store.tyblog.entity.Post;
import store.tyblog.repository.CategoryRepository;
import store.tyblog.repository.MemberRepository;
import store.tyblog.repository.PostRepository;

import java.util.List;
import java.util.NoSuchElementException;

import static store.tyblog.common.util.SecurityUtil.getCurrentEmail;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;

    public void create(PostRequestDto postRequestDto) {
        Member member = memberRepository.findByEmail(getCurrentEmail())
                .orElseThrow(() -> new NoSuchElementException("회원 정보가 없습니다."));

        List<Category> categories = postRequestDto.getCategoryIds().stream()
                .map(categoryRepository::getReferenceById)
                .toList();

        Post post = Post.builder()
                .member(member)
                .title(postRequestDto.getTitle())
                .content(postRequestDto.getContent())
                .categories(categories)
                .build();

        postRepository.save(post);
    }

    public PostResponseDto getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 게시글이 없습니다."));

        return new PostResponseDto(post);
    }

    public Page<PostResponseDto> getPosts(String username, long categoryId, Pageable pageable) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("회원 정보가 없습니다."));

        Page<Post> postPage;

        if (categoryId == 0L) {
            postPage = postRepository.findByMemberAndIsDeletedFalse(member, pageable);
        } else {
            Category category = Category.builder()
                    .id(categoryId)
                    .build();

            postPage = postRepository.findByMemberAndCategoriesEqualsAndIsDeletedFalse(
                    member, category, pageable);
        }


        return postPage.map(PostResponseDto::new);
    }

    public Page<PostResponseDto> getRecentPosts(Pageable pageable) {
        Page<Post> postPage = postRepository.findAllByIsDeletedFalse(pageable);
        return postPage.map(PostResponseDto::new);
    }
}
