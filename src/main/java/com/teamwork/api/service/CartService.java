package com.teamwork.api.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teamwork.api.exception.ResourceNotFoundException;
import com.teamwork.api.model.Cart;
import com.teamwork.api.model.CartItem;
import com.teamwork.api.model.Product;
import com.teamwork.api.model.User;
import com.teamwork.api.model.DTO.AddItemToCartRequestDTO;
import com.teamwork.api.model.DTO.CartDTO;
import com.teamwork.api.repository.CartItemRepository;
import com.teamwork.api.repository.CartRepository;
import com.teamwork.api.repository.ProductRepository;
import com.teamwork.api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * Получает или создает корзину для указанного пользователя.
     * Этот метод является приватным, так как он является внутренней логикой
     * сервиса.
     *
     * @param username Имя пользователя.
     * @return Сущность Cart.
     */
    @Transactional
    private Cart getOrCreateCart(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь " + username + " не найден"));

        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });
    }

    /**
     * Возвращает содержимое корзины пользователя в виде DTO.
     *
     * @param username Имя текущего пользователя.
     * @return CartDTO с информацией о товарах и общей стоимости.
     */
    @Transactional(readOnly = true)
    public CartDTO getCartDTO(String username) {
        Cart cart = getOrCreateCart(username);
        return CartDTO.fromCart(cart);
    }

    /**
     * Добавляет товар в корзину или обновляет его количество, если он уже там.
     *
     * @param username   Имя текущего пользователя.
     * @param requestDTO DTO с ID продукта и количеством.
     * @return Обновленное DTO корзины.
     */
    @Transactional
    public CartDTO addItemToCart(String username, AddItemToCartRequestDTO requestDTO) {
        Cart userCart = getOrCreateCart(username);
        Product product = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Продукт с ID " + requestDTO.getProductId() + " не найден"));

        // Проверяем, есть ли уже такой товар в корзине
        Optional<CartItem> existingItemOpt = userCart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            // Если товар уже есть, обновляем количество
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + requestDTO.getQuantity());
            cartItemRepository.save(existingItem);
        } else {
            // Если товара нет, создаем новый CartItem
            CartItem newItem = new CartItem();
            newItem.setCart(userCart);
            newItem.setProduct(product);
            newItem.setQuantity(requestDTO.getQuantity());
            userCart.getCartItems().add(cartItemRepository.save(newItem));
        }

        // Сохраняем корзину, чтобы обновить связь, если был добавлен новый элемент
        return CartDTO.fromCart(cartRepository.save(userCart));
    }

    @Transactional
    public CartDTO updateItemQuantity(String username, Long cartItemId, int newQuantity) {


        Cart cart = getOrCreateCart(username);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Элемент корзины с ID " + cartItemId + " не найден"));

        // Проверяем, что элемент корзины принадлежит текущему пользователю
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new SecurityException("Попытка изменить чужой элемент корзины");
        }

        cartItem.setQuantity(newQuantity);
        cartItemRepository.save(cartItem);

        return CartDTO.fromCart(cartRepository.save(cart));
    }

    /**
     * Удаляет товар из корзины по его ID (cartItemId).
     *
     * @param username   Имя текущего пользователя.
     * @param cartItemId ID элемента корзины для удаления.
     */
    @Transactional
    public void removeItemFromCart(String username, Long cartItemId) {
        Cart cart = getOrCreateCart(username);

        CartItem itemToRemove = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Элемент корзины с ID " + cartItemId + " не найден"));

        if (!itemToRemove.getCart().getId().equals(cart.getId())) {
            throw new SecurityException("Попытка удалить чужой элемент корзины");
        }

        cart.getCartItems().removeIf(ci -> ci.getId().equals(cartItemId));
    }


    /**
     * Полностью очищает корзину пользователя.
     *
     * @param username Имя текущего пользователя.
     */
    @Transactional
    public void clearCart(String username) {
        Cart cart = getOrCreateCart(username);

        cart.getCartItems().clear();

        cartRepository.save(cart);
    }
}
