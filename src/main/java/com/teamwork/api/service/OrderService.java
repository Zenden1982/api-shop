package com.teamwork.api.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teamwork.api.model.DTO.OrderCreateUpdateDTO;
import com.teamwork.api.model.DTO.OrderReadDTO;
import com.teamwork.api.model.Enum.OrderStatus;
import com.teamwork.api.model.OptionChoice;
import com.teamwork.api.model.Order;
import com.teamwork.api.model.Payment;
import com.teamwork.api.model.User;
import com.teamwork.api.repository.OptionChoiceRepository;
import com.teamwork.api.repository.OrderRepository;
import com.teamwork.api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PaymentService paymentService;

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
        order.setSelectedOptionId(dto.getSelectedOptionId());
        BigDecimal totalPrice = optionChoiceRepository.findById(dto.getSelectedOptionId())
                .map(OptionChoice::getPrice)
                .orElseThrow(() -> new RuntimeException("Option not found"));
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

        if (dto.getSelectedOptionId() != null) {
            order.setSelectedOptionId(dto.getSelectedOptionId());
            BigDecimal totalPrice = optionChoiceRepository.findById(dto.getSelectedOptionId())
                    .map(OptionChoice::getPrice)
                    .orElseThrow(() -> new RuntimeException("Option not found"));
            order.setTotalPrice(totalPrice);
        }

        Order updatedOrder = orderRepository.save(order);
        return OrderReadDTO.fromOrder(updatedOrder);
    }
}
