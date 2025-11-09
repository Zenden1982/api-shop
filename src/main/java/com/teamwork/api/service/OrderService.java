package com.teamwork.api.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teamwork.api.model.OptionChoice;
import com.teamwork.api.model.Order;
import com.teamwork.api.model.OrderItem;
import com.teamwork.api.model.Payment;
import com.teamwork.api.model.User;
import com.teamwork.api.model.DTO.OrderCreateUpdateDTO;
import com.teamwork.api.model.DTO.OrderItemDTO;
import com.teamwork.api.model.DTO.OrderReadDTO;
import com.teamwork.api.model.Enum.OrderStatus;
import com.teamwork.api.repository.OptionChoiceRepository;
import com.teamwork.api.repository.OrderItemRepository;
import com.teamwork.api.repository.OrderRepository;
import com.teamwork.api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PaymentService paymentService;
    private final OrderItemRepository orderItemService;

    private final OptionChoiceRepository optionChoiceRepository;

    @Transactional
    public Page<OrderReadDTO> findAll(int page, int size) {
        return orderRepository.findAll(PageRequest.of(page, size))
                .map(OrderReadDTO::fromOrder);
    }

    @Transactional
    public Optional<OrderReadDTO> findById(Long id) {
        return orderRepository.findById(id)
                .map(OrderReadDTO::fromOrder);
    }

    @Transactional
    public OrderReadDTO findByUserId(Long userId) {
        return OrderReadDTO.fromOrder(orderRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Order not found for userId: " + userId)));
    }

    @Transactional
    public OrderReadDTO create(OrderCreateUpdateDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + dto.getUserId()));

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setShippingAddress(dto.getShippingAddress());
        order.setPhoneNumber(dto.getPhoneNumber());
        order.setItems(new ArrayList<>());

        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderItemDTO itemDTO : dto.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);

            // Найдём выбранные опции и посчитаем цену позиции
            BigDecimal itemPrice = BigDecimal.ZERO;
            if (itemDTO.getSelectedOptionIds() != null) {
                var choices = optionChoiceRepository.findAllById(itemDTO.getSelectedOptionIds());
                // set selected options
                orderItem.setSelectedOptions(Set.copyOf(choices));
                for (OptionChoice c : choices) {
                    if (c.getPrice() != null)
                        itemPrice = itemPrice.add(c.getPrice());
                }
            }
            orderItem.setPrice(itemPrice);
            order.getItems().add(orderItem);
            totalPrice = totalPrice.add(itemPrice);
        }

        order.setTotalPrice(totalPrice);

        Order savedOrder = orderRepository.save(order);

        Payment payment = paymentService.createPayment(savedOrder.getId());
        savedOrder.setPayment(payment);

        return OrderReadDTO.fromOrder(savedOrder);
    }

    public void delete(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Order not found: " + id);
        }
        orderRepository.deleteById(id);
    }

    @Transactional
    public OrderReadDTO update(Long id, OrderCreateUpdateDTO dto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
        // Обновляем только те поля, которые переданы в DTO
        if (dto.getShippingAddress() != null && !dto.getShippingAddress().isBlank()) {
            order.setShippingAddress(dto.getShippingAddress());
        }

        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().isBlank()) {
            order.setPhoneNumber(dto.getPhoneNumber());
        }

        if (dto.getStatus() != null) {
            order.setStatus(dto.getStatus());
        }

        // Обработка позиций: если список items не передан — пропускаем изменения по
        // позициям
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            for (OrderItemDTO itemDTO : dto.getItems()) {
                if (itemDTO.getId() != null) {
                    // Обновляем существующую позицию
                    OrderItem orderItem = orderItemService.findById(itemDTO.getId())
                            .orElseThrow(() -> new RuntimeException("OrderItem not found: " + itemDTO.getId()));

                    // Если переданы выбранные опции — заменим их и пересчитаем цену
                    BigDecimal itemPrice = BigDecimal.ZERO;
                    if (itemDTO.getSelectedOptionIds() != null) {
                        var choices = optionChoiceRepository.findAllById(itemDTO.getSelectedOptionIds());
                        orderItem.setSelectedOptions(Set.copyOf(choices));
                        for (OptionChoice c : choices) {
                            if (c.getPrice() != null)
                                itemPrice = itemPrice.add(c.getPrice());
                        }
                        orderItem.setPrice(itemPrice);
                    }
                } else {
                    // Создаём новую позицию и добавляем в заказ
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    BigDecimal itemPrice = BigDecimal.ZERO;
                    if (itemDTO.getSelectedOptionIds() != null) {
                        var choices = optionChoiceRepository.findAllById(itemDTO.getSelectedOptionIds());
                        orderItem.setSelectedOptions(Set.copyOf(choices));
                        for (OptionChoice c : choices) {
                            if (c.getPrice() != null)
                                itemPrice = itemPrice.add(c.getPrice());
                        }
                    }
                    orderItem.setPrice(itemPrice);
                    order.getItems().add(orderItem);
                }
            }

            // Пересчитываем итоговую сумму заказа после изменений позиций
            BigDecimal newTotal = BigDecimal.ZERO;
            List<OrderItem> items = order.getItems();
            if (items != null) {
                for (OrderItem oi : items) {
                    if (oi.getPrice() != null) {
                        newTotal = newTotal.add(oi.getPrice());
                    }
                }
            }
            order.setTotalPrice(newTotal);
        }

        Order updatedOrder = orderRepository.save(order);
        return OrderReadDTO.fromOrder(updatedOrder);
    }
}
