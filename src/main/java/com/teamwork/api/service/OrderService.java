package com.teamwork.api.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teamwork.api.exception.CartIsEmptyException;
import com.teamwork.api.exception.ResourceNotFoundException;
import com.teamwork.api.model.Cart;
import com.teamwork.api.model.Order;
import com.teamwork.api.model.OrderItem;
import com.teamwork.api.model.Payment;
import com.teamwork.api.model.User;
import com.teamwork.api.model.DTO.OrderCreateUpdateDTO;
import com.teamwork.api.model.DTO.OrderReadDTO;
import com.teamwork.api.model.DTO.OrderUpdateAdminDTO;
import com.teamwork.api.model.Enum.OrderStatus;
import com.teamwork.api.repository.CartRepository;
import com.teamwork.api.repository.OrderRepository;
import com.teamwork.api.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;

    private final PaymentService paymentService;

    /**
     * Создает заказ на основе корзины, инициирует платеж и очищает корзину.
     * 
     * @param username Имя пользователя для создания заказа.
     * @param dto      DTO с адресом доставки и номером телефона.
     * @return DTO созданного и сохраненного заказа.
     */
    @Transactional
    public OrderReadDTO createFromCart(String username, OrderCreateUpdateDTO dto) {
        // --- Шаг 1: Поиск пользователя и его корзины ---
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь " + username + " не найден"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Корзина для пользователя " + username + " не найдена"));

        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new CartIsEmptyException("Нельзя создать заказ из пустой корзины");
        }

        // --- Шаг 2: Создание и наполнение объекта Order ---
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING); // Начальный статус
        order.setShippingAddress(dto.getShippingAddress());
        order.setPhoneNumber(dto.getPhoneNumber());

        // Преобразуем CartItem в OrderItem
        List<OrderItem> orderItems = cart.getCartItems().stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setOrder(order); // Устанавливаем обратную связь
            return orderItem;
        }).collect(Collectors.toList());
        order.setOrderItems(orderItems);

        // Рассчитываем общую стоимость заказа
        BigDecimal totalPrice = orderItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalPrice(totalPrice);

        // --- Шаг 3: Сохранение заказа в БД ---
        // Используем исходный объект 'order', т.к. он находится в управляемом состоянии
        Order savedOrder = orderRepository.save(order);

        // --- Шаг 4: Создание платежа для заказа ---
        try {
            Payment payment = paymentService.createPayment(savedOrder.getId());
            savedOrder.setPayment(payment);
        } catch (Exception e) {
            // Логируем ошибку, но не прерываем процесс. Заказ останется в статусе PENDING.
            log.error("Не удалось создать платеж для заказа ID: {}. Ошибка: {}", order.getId(), e.getMessage());
        }

        // --- Шаг 5: Очистка корзины ---
        cart.getCartItems().clear();
        cartRepository.save(cart);

        // --- Шаг 6: Возвращаем DTO ---
        // Используем оригинальный, полностью заполненный объект 'order' для создания
        // DTO.
        // Это решает проблему с NullPointerException.
        return OrderReadDTO.fromOrder(order);
    }

    /**
     * Обновление заказа (для администратора).
     */
    @Transactional
    public OrderReadDTO update(Long id, OrderUpdateAdminDTO dto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Заказ с ID " + id + " не найден"));

        if (dto.getStatus() != null) {
            order.setStatus(dto.getStatus());
        }
        if (dto.getShippingAddress() != null && !dto.getShippingAddress().isBlank()) {
            order.setShippingAddress(dto.getShippingAddress());
        }
        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().isBlank()) {
            order.setPhoneNumber(dto.getPhoneNumber());
        }

        Order updatedOrder = orderRepository.save(order);
        return OrderReadDTO.fromOrder(updatedOrder);
    }

    @Transactional(readOnly = true)
    public Page<OrderReadDTO> findAll(int page, int size) {
        return orderRepository.findAll(PageRequest.of(page, size))
                .map(OrderReadDTO::fromOrder);
    }

    @Transactional(readOnly = true)
    public Optional<OrderReadDTO> findById(Long id) {
        return orderRepository.findById(id)
                .map(OrderReadDTO::fromOrder);
    }

    // ДОБАВЛЕН НОВЫЙ МЕТОД ДЛЯ КОНТРОЛЛЕРА
    @Transactional(readOnly = true)
    public List<OrderReadDTO> findAllByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь " + username + " не найден"));

        return orderRepository.findAllByUserId(user.getId()).stream()
                .map(OrderReadDTO::fromOrder)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderReadDTO> findAllByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Пользователь с ID " + userId + " не найден");
        }
        return orderRepository.findAllByUserId(userId).stream()
                .map(OrderReadDTO::fromOrder)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Заказ с ID " + id + " не найден");
        }
        orderRepository.deleteById(id);
    }
}
