package com.ms.reborn.domain.product.service;

import com.ms.reborn.domain.file.service.FileStorageService;
import com.ms.reborn.domain.product.dto.ProductRequest;
import com.ms.reborn.domain.product.entity.Product;
import com.ms.reborn.domain.product.repository.ProductRepository;
import com.ms.reborn.domain.user.entity.User;
import com.ms.reborn.domain.user.repository.UserRepository;
import com.ms.reborn.global.exception.CustomException;
import com.ms.reborn.global.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private ProductService productService;

    @Test
    void 상품이_정상적으로_등록된다() {
        // given
        Long userId = 1L;
        User user = User.builder().id(userId).email("test@ms.com").build();

        ProductRequest req = new ProductRequest("제목", "설명", 10000);
        MultipartFile image = mock(MultipartFile.class);
        List<MultipartFile> images = List.of(image);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(fileStorageService.storeFile(image, "product")).thenReturn("https://fake-url.com/test.jpg");

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        doAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            p.setId(100L);
            return p;
        }).when(productRepository).save(any(Product.class));

        // when
        Long savedId = productService.addProduct(req, userId, images, fileStorageService);

        // then
        assertEquals(100L, savedId);
        verify(userRepository).findById(userId);
        verify(fileStorageService).storeFile(image, "product");
        verify(productRepository).save(productCaptor.capture());

        Product savedProduct = productCaptor.getValue();
        assertEquals("제목", savedProduct.getTitle());
        assertEquals(1, savedProduct.getImages().size());
        assertEquals("https://fake-url.com/test.jpg", savedProduct.getImages().get(0).getImageUrl());
    }

    @Test
    void 존재하지_않는_유저는_예외가_발생한다() {
        // given
        Long userId = 999L;
        ProductRequest req = new ProductRequest("제목", "설명", 10000);
        List<MultipartFile> images = null;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        CustomException ex = assertThrows(CustomException.class, () ->
                productService.addProduct(req, userId, images, fileStorageService)
        );

        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
        verify(userRepository).findById(userId);
        verify(productRepository, never()).save(any());
    }

    @Test
    void 본인_상품이면_수정된다() {
        // given
        Long productId = 1L;
        Long userId = 1L;
        ProductRequest req = new ProductRequest("새 제목", "새 설명", 20000);

        User user = User.builder().id(userId).email("me@example.com").build();

        Product product = Product.builder()
                .id(productId)
                .user(user)
                .title("기존 제목")
                .description("기존 설명")
                .price(10000)
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // when
        productService.updateProduct(productId, req, userId);

        // then
        assertEquals("새 제목", product.getTitle());
        assertEquals("새 설명", product.getDescription());
        assertEquals(20000, product.getPrice());
        verify(productRepository).save(product);
    }

    @Test
    void 남의_상품이면_예외_발생() {
        // given
        Long productId = 1L;
        Long userId = 999L;
        User owner = User.builder().id(1L).email("owner@example.com").build();

        Product product = Product.builder()
                .id(productId)
                .user(owner)
                .title("기존 제목")
                .description("기존 설명")
                .price(10000)
                .build();

        ProductRequest req = new ProductRequest("제목", "설명", 99999);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // when & then
        CustomException ex = assertThrows(CustomException.class, () ->
                productService.updateProduct(productId, req, userId)
        );

        assertEquals(ErrorCode.UNAUTHORIZED_USER, ex.getErrorCode());
        verify(productRepository, never()).save(any());
    }

    @Test
    void 본인_상품이면_삭제된다() {
        // given
        Long productId = 1L;
        Long userId = 1L;
        User user = User.builder().id(userId).email("me@example.com").build();

        Product product = Product.builder()
                .id(productId)
                .user(user)
                .title("제목")
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // when
        productService.deleteProduct(productId, userId);

        // then
        verify(productRepository).delete(product);
    }

    @Test
    void 남의_상품이면_삭제_실패_예외발생() {
        // given
        Long productId = 1L;
        Long userId = 999L;
        User owner = User.builder().id(1L).email("owner@example.com").build();

        Product product = Product.builder()
                .id(productId)
                .user(owner)
                .title("제목")
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // when & then
        CustomException ex = assertThrows(CustomException.class, () ->
                productService.deleteProduct(productId, userId)
        );

        assertEquals(ErrorCode.UNAUTHORIZED_USER, ex.getErrorCode());
        verify(productRepository, never()).delete(any());
    }
}
