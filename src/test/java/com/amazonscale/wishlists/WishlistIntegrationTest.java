package com.amazonscale.wishlists;

import com.amazonscale.product.entity.Product;
import com.amazonscale.product.repository.ProductRepository;
import com.amazonscale.security.CustomUserDetails;
import com.amazonscale.user.entity.User;
import com.amazonscale.user.enums.Role;
import com.amazonscale.user.repository.UserRepository;
import com.amazonscale.wishlists.controller.WishlistController;
import com.amazonscale.wishlists.dto.request.AddToWishlistRequest;
import com.amazonscale.wishlists.dto.request.CreateWishlistRequest;
import com.amazonscale.wishlists.dto.request.MoveWishlistItemRequest;
import com.amazonscale.wishlists.dto.request.UpdateWishlistRequest;
import com.amazonscale.wishlists.dto.response.WishlistItemResponse;
import com.amazonscale.wishlists.dto.response.WishlistResponse;
import com.amazonscale.wishlists.dto.response.WishlistSummaryResponse;
import com.amazonscale.wishlists.enums.WishlistPriority;
import com.amazonscale.wishlists.enums.WishlistType;
import com.amazonscale.wishlists.exception.WishlistAlreadyExistsException;
import com.amazonscale.wishlists.exception.WishlistNotFoundException;
import com.amazonscale.wishlists.service.WishlistService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class WishlistIntegrationTest {

    private MockMvc mockMvc;

    @Mock
    private WishlistService wishlistService;

    @InjectMocks
    private WishlistController wishlistController;

    private ObjectMapper objectMapper;
    private User user;
    private Product product;
    private WishlistResponse sampleWishlistResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(wishlistController).build();
        objectMapper = new ObjectMapper();

        user = User.builder()
                .id(1L)
                .firstName("Integration")
                .lastName("Tester")
                .email("integration@test.com")
                .password("encodedPassword")
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();

        product = Product.builder()
                .id(10L)
                .name("Test Product")
                .description("Integration test product")
                .imageUrl("http://img.com/product.jpg")
                .price(new BigDecimal("49.99"))
                .stock(100)
                .brand("TestBrand")
                .active(true)
                .build();

        sampleWishlistResponse = WishlistResponse.builder()
                .wishlistId(100L)
                .wishlistName("My Wishlist")
                .description("Integration test wishlist")
                .isDefault(false)
                .items(List.of())
                .totalItems(0)
                .currentPage(0)
                .totalPages(1)
                .hasNext(false)
                .hasPrevious(false)
                .build();

        CustomUserDetails userDetails = new CustomUserDetails(user);
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void fullWishlistLifecycle() throws Exception {
        // 1. Create a wishlist
        CreateWishlistRequest createRequest = CreateWishlistRequest.builder()
                .name("My Wishlist")
                .description("Integration test wishlist")
                .build();

        when(wishlistService.createWishlist(any(CreateWishlistRequest.class))).thenReturn(sampleWishlistResponse);

        mockMvc.perform(post("/api/v1/wishlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.wishlistName").value("My Wishlist"))
                .andExpect(jsonPath("$.isDefault").value(false));

        // 2. Get user wishlists
        WishlistSummaryResponse summary = WishlistSummaryResponse.builder()
                .wishlistId(100L)
                .wishlistName("My Wishlist")
                .type(WishlistType.CUSTOM)
                .isDefault(false)
                .totalItems(0)
                .build();
        when(wishlistService.getUserWishlists()).thenReturn(List.of(summary));

        mockMvc.perform(get("/api/v1/wishlists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        // 3. Get wishlist by ID
        when(wishlistService.getWishlist(100L, 0, 10)).thenReturn(sampleWishlistResponse);

        mockMvc.perform(get("/api/v1/wishlists/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.wishlistId").value(100L));

        // 4. Add item to wishlist
        AddToWishlistRequest addRequest = AddToWishlistRequest.builder()
                .wishlistId(100L)
                .productId(product.getId())
                .priority(WishlistPriority.HIGH)
                .note("Must have!")
                .build();

        WishlistItemResponse itemResponse = WishlistItemResponse.builder()
                .wishlistItemId(1000L)
                .wishlistId(100L)
                .productId(product.getId())
                .productName("Test Product")
                .priority(WishlistPriority.HIGH)
                .build();

        when(wishlistService.addItem(any(AddToWishlistRequest.class))).thenReturn(itemResponse);

        mockMvc.perform(post("/api/v1/wishlists/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productName").value("Test Product"))
                .andExpect(jsonPath("$.priority").value("HIGH"));

        // 5. Update wishlist
        UpdateWishlistRequest updateRequest = UpdateWishlistRequest.builder()
                .name("Updated Wishlist")
                .description("Updated description")
                .build();

        WishlistResponse updatedResponse = WishlistResponse.builder()
                .wishlistId(100L)
                .wishlistName("Updated Wishlist")
                .build();

        when(wishlistService.updateWishlist(eq(100L), any(UpdateWishlistRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/v1/wishlists/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.wishlistName").value("Updated Wishlist"));

        // 6. Remove item from wishlist
        doNothing().when(wishlistService).removeItem(100L, product.getId());

        mockMvc.perform(delete("/api/v1/wishlists/100/items/" + product.getId()))
                .andExpect(status().isNoContent());

        // 7. Delete wishlist
        doNothing().when(wishlistService).deleteWishlist(100L);

        mockMvc.perform(delete("/api/v1/wishlists/100"))
                .andExpect(status().isNoContent());
    }

    @Test
    void moveItemBetweenWishlists_Success() throws Exception {
        MoveWishlistItemRequest moveReq = MoveWishlistItemRequest.builder()
                .sourceWishlistId(100L)
                .destinationWishlistId(200L)
                .productId(product.getId())
                .build();

        WishlistItemResponse itemResponse = WishlistItemResponse.builder()
                .wishlistItemId(1000L)
                .productId(product.getId())
                .productName("Test Product")
                .build();

        when(wishlistService.moveItem(any(MoveWishlistItemRequest.class))).thenReturn(itemResponse);

        mockMvc.perform(put("/api/v1/wishlists/items/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(moveReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Test Product"));
    }

    @Test
    void clearWishlist_RemovesAllItems() throws Exception {
        doNothing().when(wishlistService).clearWishlist(100L);

        mockMvc.perform(delete("/api/v1/wishlists/100/items"))
                .andExpect(status().isNoContent());

        verify(wishlistService, times(1)).clearWishlist(100L);
    }
}
