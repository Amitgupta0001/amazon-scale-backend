package com.amazonscale.wishlists.service.impl;

import com.amazonscale.product.entity.Product;
import com.amazonscale.product.exception.ProductNotFoundException;
import com.amazonscale.product.repository.ProductRepository;
import com.amazonscale.security.CustomUserDetails;
import com.amazonscale.user.entity.User;
import com.amazonscale.user.exception.UserNotFoundException;
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
import com.amazonscale.wishlists.exception.*;
import com.amazonscale.wishlists.mapper.WishlistMapper;
import com.amazonscale.wishlists.repository.WishlistItemRepository;
import com.amazonscale.wishlists.repository.WishlistRepository;
import com.amazonscale.wishlists.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final ProductRepository productRepository;


    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new UserNotFoundException(getCurrentUser().getId());
        }
        return userDetails.getUser();
    }

    @Override
    public WishlistResponse createWishlist(CreateWishlistRequest request) {

        User currentUser = getCurrentUser();

        if (wishlistRepository.existsByUser_IdAndNameIgnoreCase(currentUser.getId(), request.getName())) {
            throw new WishlistAlreadyExistsException("Wishlist with name '" + request.getName() + "' already exists.");
        }

        Wishlist wishlist = Wishlist.builder()
                .user(currentUser)
                .name(request.getName())
                .description(request.getDescription())
                .type(WishlistType.CUSTOM)
                .isDefault(false)
                .build();

        Wishlist savedWishlist = wishlistRepository.save(wishlist);

        return WishlistMapper.toWishlistResponse(savedWishlist, List.of(), 0, 0, 1, false, false);
    }

    @Override
    @Transactional(readOnly = true)
    public WishlistResponse getWishlist(Long wishlistId, int page, int size) {

        User currentUser = getCurrentUser();

        Wishlist wishlist = wishlistRepository.findByIdAndUser_Id(wishlistId, currentUser.getId()).orElseThrow(() ->
                        new WishlistNotFoundException("Wishlist not found with id: " + wishlistId));

        Pageable pageable = PageRequest.of(page, size);

        Page<WishlistItem> wishlistItems =
                wishlistItemRepository.findAllByWishlist_IdOrderByCreatedAtDesc(wishlistId, pageable);

        return WishlistMapper.toWishlistResponse(
                wishlist,
                WishlistMapper.toWishlistItemResponses(wishlistItems.getContent()),
                (int) wishlistItems.getTotalElements(),
                wishlistItems.getNumber(),
                wishlistItems.getTotalPages(),
                wishlistItems.hasNext(),
                wishlistItems.hasPrevious()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<WishlistSummaryResponse> getUserWishlists() {

        User currentUser = getCurrentUser();

        List<Wishlist> wishlists = wishlistRepository.findAllByUser_IdOrderByCreatedAtDesc(currentUser.getId());

        return WishlistMapper.toWishlistSummaryResponses(wishlists);
    }

    @Override
    public WishlistItemResponse addItem(AddToWishlistRequest request) {

        User currentUser = getCurrentUser();

        // Verification
        Wishlist wishlist = wishlistRepository.findByIdAndUser_Id(request.getWishlistId(), currentUser.getId())
                .orElseThrow(() -> new WishlistNotFoundException("Wishlist not found."));

        // Verify product exists
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(request.getProductId()));

        // Prevent duplicate products
        if (wishlistItemRepository.existsByWishlist_IdAndProduct_Id(wishlist.getId(), product.getId())) {
            throw new WishlistItemAlreadyExistsException("Product already exists in wishlist.");
        }

        WishlistItem wishlistItem = WishlistItem.builder()
                .wishlist(wishlist)
                .product(product)
                .priority(
                        request.getPriority() == null
                                ? WishlistPriority.MEDIUM
                                : request.getPriority()
                )
                .note(request.getNote())
                .build();

        WishlistItem savedItem = wishlistItemRepository.save(wishlistItem);

        return WishlistMapper.toWishlistItemResponse(savedItem);
    }

    @Override
    public WishlistItemResponse moveItem(MoveWishlistItemRequest request) {
        User currentUser = getCurrentUser();

        Wishlist sourceWishlist = wishlistRepository.findByIdAndUser_Id(request.getSourceWishlistId(), currentUser.getId())
                .orElseThrow(() -> new WishlistNotFoundException("Source wishlist not found."));

        Wishlist destinationWishlist = wishlistRepository.findByIdAndUser_Id(request.getDestinationWishlistId(), currentUser.getId())
                .orElseThrow(() -> new WishlistNotFoundException("Destination wishlist not found."));

        WishlistItem wishlistItem = wishlistItemRepository.findByWishlist_IdAndProduct_Id(sourceWishlist.getId(), request.getProductId())
                .orElseThrow(() -> new WishlistItemNotFoundException("Product not found in source wishlist."));

        if (wishlistItemRepository.existsByWishlist_IdAndProduct_Id(destinationWishlist.getId(), request.getProductId())) {
            throw new WishlistItemAlreadyExistsException("Product already exists in destination wishlist.");
        }

        wishlistItem.setWishlist(destinationWishlist);
        WishlistItem updatedItem = wishlistItemRepository.save(wishlistItem);
        return WishlistMapper.toWishlistItemResponse(updatedItem);
    }

    @Override
    public WishlistResponse updateWishlist(Long wishlistId, UpdateWishlistRequest request) {

        User currentUser = getCurrentUser();

        Wishlist wishlist = wishlistRepository.findByIdAndUser_Id(wishlistId, currentUser.getId())
                .orElseThrow(() -> new WishlistNotFoundException("Wishlist not found."));


        if (Boolean.TRUE.equals(wishlist.getIsDefault())) {
            throw new DefaultWishlistModificationException("Default wishlists cannot be modified.");
        }

        wishlistRepository.findByUser_IdAndNameIgnoreCase(currentUser.getId(), request.getName())
                .ifPresent(existingWishlist -> {
                    if (!existingWishlist.getId().equals(wishlistId)) {
                        throw new WishlistAlreadyExistsException("Wishlist with name '" + request.getName() + "' already exists.");
                    }
                });

        wishlist.setName(request.getName());
        wishlist.setDescription(request.getDescription());

        Wishlist updatedWishlist = wishlistRepository.save(wishlist);

        return WishlistMapper.toWishlistResponse(updatedWishlist, WishlistMapper.toWishlistItemResponses(updatedWishlist.getItems()),
                updatedWishlist.getItems().size(), 0, 1, false, false
        );
    }

    @Override
    public void removeItem(Long wishlistId, Long productId) {

        User currentUser = getCurrentUser();
        Wishlist wishlist = wishlistRepository.findByIdAndUser_Id(wishlistId, currentUser.getId())
                .orElseThrow(() -> new WishlistNotFoundException("Wishlist not found."));

        WishlistItem wishlistItem = wishlistItemRepository.findByWishlist_IdAndProduct_Id(wishlist.getId(), productId)
                .orElseThrow(() -> new WishlistItemNotFoundException("Product not found in wishlist."));

        wishlistItemRepository.delete(wishlistItem);
    }

    @Override
    public void deleteWishlist(Long wishlistId) {

        User currentUser = getCurrentUser();

        Wishlist wishlist = wishlistRepository.findByIdAndUser_Id(wishlistId, currentUser.getId())
                .orElseThrow(() -> new WishlistNotFoundException("Wishlist not found."));

        if (Boolean.TRUE.equals(wishlist.getIsDefault())) {
            throw new DefaultWishlistModificationException("Default wishlists cannot be deleted.");
        }
        wishlistRepository.delete(wishlist);
    }

    @Override
    public void clearWishlist(Long wishlistId) {

        User currentUser = getCurrentUser();

        Wishlist wishlist = wishlistRepository.findByIdAndUser_Id(wishlistId, currentUser.getId())
                .orElseThrow(() -> new WishlistNotFoundException("Wishlist not found."));

        wishlistItemRepository.deleteAllByWishlist_Id(
                wishlist.getId()
        );
    }
}