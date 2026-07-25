package com.amazonscale.cart.service.impl;

import com.amazonscale.cart.dto.AddToCartRequest;
import com.amazonscale.cart.dto.CartResponse;
import com.amazonscale.cart.dto.UpdateCartItemRequest;
import com.amazonscale.cart.entity.Cart;
import com.amazonscale.cart.entity.CartItem;
import com.amazonscale.cart.exception.CartItemNotFoundException;
import com.amazonscale.cart.exception.CartNotFoundException;
import com.amazonscale.cart.mapper.CartMapper;
import com.amazonscale.cart.repository.CartItemRepository;
import com.amazonscale.cart.repository.CartRepository;
import com.amazonscale.cart.service.CartService;
import com.amazonscale.inventory.exception.InsufficientStockException;
import com.amazonscale.product.entity.Product;
import com.amazonscale.product.exception.ProductNotFoundException;
import com.amazonscale.product.exception.ProductUnavailableException;
import com.amazonscale.product.repository.ProductRepository;
import com.amazonscale.user.entity.User;
import com.amazonscale.user.exception.UserNotFoundException;
import com.amazonscale.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;

    private Cart getCartEntity(Long userId) {
        return cartRepository.findByUser_Id(userId)
                .orElseThrow(() -> new CartNotFoundException(userId));
    }

    private Product getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        if (!product.getActive()) {
            throw new ProductUnavailableException(productId);
        }

        return product;
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUser_Id(user.getId())
                .orElseGet(() ->
                        cartRepository.save(
                                Cart.builder()
                                        .user(user)
                                        .build()
                        ));
    }

    private void validateStock(Product product, int quantity) {
        if (quantity > product.getStock()) {
            throw new InsufficientStockException(
                    "Insufficient stock for product " + product.getId());
        }
    }

    //Add Item to Cart after validation of user
    @Override
    public CartResponse addItemToCart(Long userId, AddToCartRequest request) {
        User user = getUser(userId);
        Product product = getProduct(request.getProductId());
        Cart cart = getOrCreateCart(user);

        // Checks if product already exists in cart

        Optional<CartItem> existingItem =
                cartItemRepository.findByCart_IdAndProduct_Id(
                        cart.getId(),
                        product.getId());

        CartItem cartItem;

        if (existingItem.isPresent()) {

            cartItem = existingItem.get();

            int newQuantity =
                    cartItem.getQuantity() + request.getQuantity();

            validateStock(product, newQuantity);

            cartItem.setQuantity(newQuantity);

        } else {

            validateStock(product, request.getQuantity());

            cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .priceAtAddition(product.getPrice())
                    .build();
        }

        cartItemRepository.save(cartItem);

        return cartMapper.toCartResponse(cart);
    }

    // updating cart items
    @Override
    public CartResponse updateCartItem(Long userId,
                                       Long productId,
                                       UpdateCartItemRequest request) {

        Cart cart = getCartEntity(userId);

        CartItem cartItem = cartItemRepository
                .findByCart_IdAndProduct_Id(cart.getId(), productId)
                .orElseThrow(() -> new CartItemNotFoundException(productId));

        // Validate stock
        Product product = cartItem.getProduct();

        validateStock(product, request.getQuantity());

        cartItem.setQuantity(request.getQuantity());

        cartItemRepository.save(cartItem);

        return cartMapper.toCartResponse(cart);
    }

    // removing cart items from cart
    @Override
    public void removeCartItem(Long userId, Long productId) {

        Cart cart = getCartEntity(userId);

        cartItemRepository.findByCart_IdAndProduct_Id(cart.getId(), productId)
                .orElseThrow(() -> new CartItemNotFoundException(productId));

        cartItemRepository.deleteByCart_IdAndProduct_Id(
                cart.getId(),
                productId
        );
    }

    // clear cart
    @Override
    public void clearCart(Long userId) {

        Cart cart = getCartEntity(userId);

        cartItemRepository.deleteByCart_Id(cart.getId());
    }

    // Fetch Cart with total and subtotal items
    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {

        Cart cart = getCartEntity(userId);

        return cartMapper.toCartResponse(cart);
    }
}