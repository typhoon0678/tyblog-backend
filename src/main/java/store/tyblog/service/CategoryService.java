package store.tyblog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.tyblog.dto.category.CategoryRequestDto;
import store.tyblog.dto.category.CategoryResponseDto;
import store.tyblog.entity.Category;
import store.tyblog.entity.Member;
import store.tyblog.repository.CategoryRepository;
import store.tyblog.repository.MemberRepository;

import java.util.List;
import java.util.NoSuchElementException;

import static store.tyblog.common.util.SecurityUtil.getCurrentEmail;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final MemberRepository memberRepository;

    public void create(CategoryRequestDto categoryRequestDto) {
        Member member = memberRepository.findByEmail(getCurrentEmail())
                .orElseThrow(() -> new NoSuchElementException("회원정보가 없습니다."));

        categoryRepository.save(Category.builder()
                .name(categoryRequestDto.getName())
                .member(member)
                .build());
    }

    public List<CategoryResponseDto> getCategories() {
        Member member = memberRepository.findByEmail(getCurrentEmail())
                .orElseThrow(() -> new NoSuchElementException("회원정보가 없습니다."));

        List<Category> categories = categoryRepository.findByMemberAndIsDeletedFalse(member);
        return categories.stream().map(CategoryResponseDto::new).toList();
    }
}
