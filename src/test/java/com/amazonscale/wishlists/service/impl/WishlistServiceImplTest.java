package com.amazonscale.wishlists.service.impl;

import com.amazonscale.product.entity.Product;
import com.amazonscale.product.repository.ProductRepository;
import com.amazonscale.security.CustomUserDetails;
import com.amazonscale.user.entity.User;
import com.amazonscale.user.enums.Role;
import com.amazonscale.wishlists.dto.request.AddToWishlistRequest;
import com.amazonscale.wishlists.dto.request.CreateWishlistRequest;
import com.amazonscale.wishlists.dto.request.MoveWishlistItemRequest;
import com.amazonscale.wishlists.dto.request.UpdateWishlistRequest;
import com.amazonscale.wishlists.dto.response.WishlistItemResponse;
import com.amazonscale.wishlists.dto.response.WishlistResponse;
import com.amazonscale.wishlists.dto.response.WishlistSummaryResponse;
import com.amazonscale.wishlists.entity.Wishlist;
import com.amazonscale.wishlists.entity.WishlistItem;
import com.amazonscale.wishlists.enums.WishlistPriority;
import com.amazonscale.wishlists.enums.WishlistType;
import com.amazonscale.wishlists.exception.DefaultWishlistModificationException;
import com.amazonscale.wishlists.exception.WishlistAlreadyExistsException;
import com.amazonscale.wishlists.exception.WishlistItemAlreadyExistsException;
import com.amazonscale.wishlists.repository.WishlistItemRepository;
import com.amazonscale.wishlists.repository.WishlistRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishlistServiceImplTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private WishlistItemRepository wishlistItemRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private WishlistServiceImpl wishlistService;

    private User currentUser;
    private Wishlist wishlist;
    private Product product;
    private WishlistItem wishlistItem;

    @BeforeEach
    void setUp() {
        currentUser = User.builder()
                .id(1L)
                .firstName("Wishlist")
                .lastName("User")
                .email("user@example.com")
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();

        CustomUserDetails userDetails = new CustomUserDetails(currentUser);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        product = Product.builder()
                .id(10L)
                .name("Smartphone")
                .price(new BigDecimal("699.99"))
                .stock(10)
                .active(true)
                .build();

        wishlist = Wishlist.builder()
                .id(100L)
                .user(currentUser)
                .name("Tech Wishlist")
                .description("All tech items")
                .type(WishlistType.CUSTOM)
                .isDefault(false)
                .items(new ArrayList<>())
                .build();

        wishlistItem = WishlistItem.builder()
                .id(1000L)
                .wishlist(wishlist)
                .product(product)
                .priority(WishlistPriority.HIGH)
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should create custom wishlist successfully")
    void shouldCreateWishlistSuccessfully() {
        // Arrange
        CreateWishlistRequest request = CreateWishlistRequest.builder()
                .name("Tech Wishlist")
                .description("All tech items")
                .build();

        when(wishlistRepository.existsByUser_IdAndNameIgnoreCase(1L, "Tech Wishlist")).thenReturn(false);
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(wishlist);

        // Act
        WishlistResponse response = wishlistService.createWishlist(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getWishlistId()).isEqualTo(100L);
        assertThat(response.getWishlistName()).isEqualTo("Tech Wishlist");

        verify(wishlistRepository).save(any(Wishlist.class));
    }

    @Test
    @DisplayName("Should throw WishlistAlreadyExistsException when creating wishlist with duplicate name")
    void shouldThrowExceptionWhenCreatingDuplicateWishlist() {
        // Arrange
        CreateWishlistRequest request = CreateWishlistRequest.builder().name("Tech Wishlist").build();
        when(wishlistRepository.existsByUser_IdAndNameIgnoreCase(1L, "Tech Wishlist")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> wishlistService.createWishlist(request))
                .isInstanceOf(WishlistAlreadyExistsException.class)
                .hasMessageContaining("Wishlist with name 'Tech Wishlist' already exists.");
    }

    @Test
    @DisplayName("Should get paginated wishlist items")
    void shouldGetWishlistSuccessfully() {
        // Arrange
        when(wishlistRepository.findByIdAndUser_Id(100L, 1L)).thenReturn(Optional.of(wishlist));
        when(wishlistItemRepository.findAllByWishlist_IdOrderByCreatedAtDesc(eq(100L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(wishlistItem)));

        // Act
        WishlistResponse response = wishlistService.getWishlist(100L, 0, 10);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getWishlistId()).isEqualTo(100L);
        assertThat(response.getItems()).hasSize(1);
    }

    @Test
    @DisplayName("Should get all user wishlists summary")
    void shouldGetUserWishlistsSuccessfully() {
        // Arrange
        when(wishlistRepository.findAllByUser_IdOrderByCreatedAtDesc(1L)).thenReturn(List.of(wishlist));

        // Act
        List<WishlistSummaryResponse> responses = wishlistService.getUserWishlists();

        // Assert
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getWishlistId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("Should add product to wishlist successfully")
    void shouldAddItemSuccessfully() {
        // Arrange
        AddToWishlistRequest request = AddToWishlistRequest.builder()
                .wishlistId(100L)
                .productId(10L)
                .priority(WishlistPriority.HIGH)
                .note("On sale")
                .build();

        when(wishlistRepository.findByIdAndUser_Id(100L, 1L)).thenReturn(Optional.of(wishlist));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(wishlistItemRepository.existsByWishlist_IdAndProduct_Id(100L, 10L)).thenReturn(false);
        when(wishlistItemRepository.save(any(WishlistItem.class))).thenReturn(wishlistItem);

        // Act
        WishlistItemResponse response = wishlistService.addItem(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getProductId()).isEqualTo(10L);

        verify(wishlistItemRepository).save(any(WishlistItem.class));
    }

    @Test
    @DisplayName("Should throw WishlistItemAlreadyExistsException when adding existing product")
    void shouldThrowExceptionWhenAddingExistingProduct() {
        // Arrange
        AddToWishlistRequest request = AddToWishlistRequest.builder().wishlistId(100L).productId(10L).build();
        when(wishlistRepository.findByIdAndUser_Id(100L, 1L)).thenReturn(Optional.of(wishlist));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(wishlistItemRepository.existsByWishlist_IdAndProduct_Id(100L, 10L)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> wishlistService.addItem(request))
                .isInstanceOf(WishlistItemAlreadyExistsException.class)
                .hasMessage("Product already exists in wishlist.");
    }

    @Test
    @DisplayName("Should move item to destination wishlist successfully")
    void shouldMoveItemSuccessfully() {
        // Arrange
        Wishlist destWishlist = Wishlist.builder().id(200L).user(currentUser).name("Other List").build();
        MoveWishlistItemRequest request = MoveWishlistItemRequest.builder()
                .sourceWishlistId(100L)
                .destinationWishlistId(200L)
                .productId(10L)
                .build();

        when(wishlistRepository.findByIdAndUser_Id(100L, 1L)).thenReturn(Optional.of(wishlist));
        when(wishlistRepository.findByIdAndUser_Id(200L, 1L)).thenReturn(Optional.of(destWishlist));
        when(wishlistItemRepository.findByWishlist_IdAndProduct_Id(100L, 10L)).thenReturn(Optional.of(wishlistItem));
        when(wishlistItemRepository.existsByWishlist_IdAndProduct_Id(200L, 10L)).thenReturn(false);
        when(wishlistItemRepository.save(wishlistItem)).thenReturn(wishlistItem);

        // Act
        WishlistItemResponse response = wishlistService.moveItem(request);

        // Assert
        assertThat(response).isNotNull();
        verify(wishlistItemRepository).save(wishlistItem);
    }

    @Test
    @DisplayName("Should update wishlist successfully")
    void shouldUpdateWishlistSuccessfully() {
        // Arrange
        UpdateWishlistRequest request = UpdateWishlistRequest.builder().name("Updated Tech").description("New desc").build();
        when(wishlistRepository.findByIdAndUser_Id(100L, 1L)).thenReturn(Optional.of(wishlist));
        when(wishlistRepository.findByUser_IdAndNameIgnoreCase(1L, "Updated Tech")).thenReturn(Optional.empty());
        when(wishlistRepository.save(wishlist)).thenReturn(wishlist);

        // Act
        WishlistResponse response = wishlistService.updateWishlist(100L, request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(wishlist.getName()).isEqualTo("Updated Tech");
    }

    @Test
    @DisplayName("Should throw DefaultWishlistModificationException when modifying default wishlist")
    void shouldThrowExceptionWhenModifyingDefaultWishlist() {
        // Arrange
        wishlist.setIsDefault(true);
        UpdateWishlistRequest request = UpdateWishlistRequest.builder().name("New Name").build();
        when(wishlistRepository.findByIdAndUser_Id(100L, 1L)).thenReturn(Optional.of(wishlist));

        // Act & Assert
        assertThatThrownBy(() -> wishlistService.updateWishlist(100L, request))
                .isInstanceOf(DefaultWishlistModificationException.class)
                .hasMessage("Default wishlists cannot be modified.");
    }

    @Test
    @DisplayName("Should delete custom wishlist successfully")
    void shouldDeleteWishlistSuccessfully() {
        // Arrange
        when(wishlistRepository.findByIdAndUser_Id(100L, 1L)).thenReturn(Optional.of(wishlist));

        // Act
        wishlistService.deleteWishlist(100L);

        // Assert
        verify(wishlistRepository).delete(wishlist);
    }

    @Test
    @DisplayName("Should throw DefaultWishlistModificationException when deleting default wishlist")
    void shouldThrowExceptionWhenDeletingDefaultWishlist() {
        // Arrange
        wishlist.setIsDefault(true);
        when(wishlistRepository.findByIdAndUser_Id(100L, 1L)).thenReturn(Optional.of(wishlist));

        // Act & Assert
        assertThatThrownBy(() -> wishlistService.deleteWishlist(100L))
                .isInstanceOf(DefaultWishlistModificationException.class)
                .hasMessage("Default wishlists cannot be deleted.");
    }

    @Test
    @DisplayName("Should clear all items from wishlist")
    void shouldClearWishlistSuccessfully() {
        // Arrange
        when(wishlistRepository.findByIdAndUser_Id(100L, 1L)).thenReturn(Optional.of(wishlist));

        // Act
        wishlistService.clearWishlist(100L);

        // Assert
        verify(wishlistItemRepository).deleteAllByWishlist_Id(100L);
    }
}
