package com.amazonscale.wishlists.controller;

import com.amazonscale.wishlists.dto.request.AddToWishlistRequest;
import com.amazonscale.wishlists.dto.request.CreateWishlistRequest;
import com.amazonscale.wishlists.dto.request.MoveWishlistItemRequest;
import com.amazonscale.wishlists.dto.request.UpdateWishlistRequest;
import com.amazonscale.wishlists.dto.response.WishlistItemResponse;
import com.amazonscale.wishlists.dto.response.WishlistResponse;
import com.amazonscale.wishlists.dto.response.WishlistSummaryResponse;
import com.amazonscale.wishlists.enums.WishlistPriority;
import com.amazonscale.wishlists.enums.WishlistType;
import com.amazonscale.wishlists.service.WishlistService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class WishlistControllerTest {

    private MockMvc mockMvc;

    @Mock
    private WishlistService wishlistService;

    @InjectMocks
    private WishlistController wishlistController;

    private ObjectMapper objectMapper;
    private WishlistResponse sampleWishlistResponse;
    private WishlistSummaryResponse sampleSummaryResponse;
    private WishlistItemResponse sampleItemResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(wishlistController).build();
        objectMapper = new ObjectMapper();

        sampleWishlistResponse = WishlistResponse.builder()
                .wishlistId(100L)
                .wishlistName("My Wishlist")
                .description("Tech items")
                .isDefault(false)
                .items(List.of())
                .totalItems(0)
                .currentPage(0)
                .totalPages(1)
                .hasNext(false)
                .hasPrevious(false)
                .build();

        sampleSummaryResponse = WishlistSummaryResponse.builder()
                .wishlistId(100L)
                .wishlistName("My Wishlist")
                .type(WishlistType.CUSTOM)
                .isDefault(false)
                .totalItems(0)
                .build();

        sampleItemResponse = WishlistItemResponse.builder()
                .wishlistItemId(1000L)
                .wishlistId(100L)
                .productId(10L)
                .productName("Smartphone")
                .priority(WishlistPriority.HIGH)
                .build();
    }

    @Test
    @DisplayName("Should create wishlist successfully and return HTTP 201 Created")
    void shouldCreateWishlistSuccessfully() throws Exception {
        // Arrange
        CreateWishlistRequest request = CreateWishlistRequest.builder().name("My Wishlist").description("Tech items").build();
        when(wishlistService.createWishlist(any(CreateWishlistRequest.class))).thenReturn(sampleWishlistResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/wishlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.wishlistId").value(100L))
                .andExpect(jsonPath("$.wishlistName").value("My Wishlist"));

        verify(wishlistService, times(1)).createWishlist(any(CreateWishlistRequest.class));
    }

    @Test
    @DisplayName("Should get user wishlists and return HTTP 200 OK")
    void shouldGetUserWishlistsSuccessfully() throws Exception {
        // Arrange
        when(wishlistService.getUserWishlists()).thenReturn(List.of(sampleSummaryResponse));

        // Act & Assert
        mockMvc.perform(get("/api/v1/wishlists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].wishlistId").value(100L));

        verify(wishlistService, times(1)).getUserWishlists();
    }

    @Test
    @DisplayName("Should get single wishlist with pagination and return HTTP 200 OK")
    void shouldGetWishlistSuccessfully() throws Exception {
        // Arrange
        when(wishlistService.getWishlist(100L, 0, 10)).thenReturn(sampleWishlistResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/wishlists/100")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.wishlistId").value(100L));

        verify(wishlistService, times(1)).getWishlist(100L, 0, 10);
    }

    @Test
    @DisplayName("Should update wishlist successfully and return HTTP 200 OK")
    void shouldUpdateWishlistSuccessfully() throws Exception {
        // Arrange
        UpdateWishlistRequest request = UpdateWishlistRequest.builder().name("Updated Wishlist").build();
        when(wishlistService.updateWishlist(eq(100L), any(UpdateWishlistRequest.class))).thenReturn(sampleWishlistResponse);

        // Act & Assert
        mockMvc.perform(put("/api/v1/wishlists/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(wishlistService, times(1)).updateWishlist(eq(100L), any(UpdateWishlistRequest.class));
    }

    @Test
    @DisplayName("Should delete wishlist and return HTTP 204 No Content")
    void shouldDeleteWishlistSuccessfully() throws Exception {
        // Arrange
        doNothing().when(wishlistService).deleteWishlist(100L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/wishlists/100"))
                .andExpect(status().isNoContent());

        verify(wishlistService, times(1)).deleteWishlist(100L);
    }

    @Test
    @DisplayName("Should add product to wishlist and return HTTP 201 Created")
    void shouldAddItemSuccessfully() throws Exception {
        // Arrange
        AddToWishlistRequest request = AddToWishlistRequest.builder().wishlistId(100L).productId(10L).build();
        when(wishlistService.addItem(any(AddToWishlistRequest.class))).thenReturn(sampleItemResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/wishlists/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.wishlistItemId").value(1000L));

        verify(wishlistService, times(1)).addItem(any(AddToWishlistRequest.class));
    }

    @Test
    @DisplayName("Should move wishlist item and return HTTP 200 OK")
    void shouldMoveItemSuccessfully() throws Exception {
        // Arrange
        MoveWishlistItemRequest request = MoveWishlistItemRequest.builder()
                .sourceWishlistId(100L).destinationWishlistId(200L).productId(10L).build();
        when(wishlistService.moveItem(any(MoveWishlistItemRequest.class))).thenReturn(sampleItemResponse);

        // Act & Assert
        mockMvc.perform(put("/api/v1/wishlists/items/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(wishlistService, times(1)).moveItem(any(MoveWishlistItemRequest.class));
    }

    @Test
    @DisplayName("Should remove product from wishlist and return HTTP 204 No Content")
    void shouldRemoveItemSuccessfully() throws Exception {
        // Arrange
        doNothing().when(wishlistService).removeItem(100L, 10L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/wishlists/100/items/10"))
                .andExpect(status().isNoContent());

        verify(wishlistService, times(1)).removeItem(100L, 10L);
    }

    @Test
    @DisplayName("Should clear wishlist and return HTTP 204 No Content")
    void shouldClearWishlistSuccessfully() throws Exception {
        // Arrange
        doNothing().when(wishlistService).clearWishlist(100L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/wishlists/100/items"))
                .andExpect(status().isNoContent());

        verify(wishlistService, times(1)).clearWishlist(100L);
    }
}
