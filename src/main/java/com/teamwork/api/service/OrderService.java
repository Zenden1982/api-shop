package com.teamwork.api.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teamwork.api.model.Order;
import com.teamwork.api.model.User;
import com.teamwork.api.model.DTO.OrderCreateUpdateDTO;
import com.teamwork.api.model.DTO.OrderReadDTO;
import com.teamwork.api.repository.OrderRepository;
import com.teamwork.api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PaymentService paymentService;

    public Page<OrderReadDTO> findAll(int page, int size) {
        return orderRepository.findAll(PageRequest.of(page, size))
                .map(OrderReadDTO::fromOrder);
    }

    public Optional<OrderReadDTO> findById(Long id) {
        return orderRepository.findById(id)
                .map(OrderReadDTO::fromOrder);
    }


    @Transactional
    public OrderReadDTO create(OrderCreateUpdateDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + dto.getUserId()));


        Order order = OrderCreateUpdateDTO.toOrder(dto, user, dto.getItems());
        Order savedOrder = orderRepository.save(order);

        // Заглушка для создания оплаты
        //Payment payment = paymentService.createPlaceholderPayment(savedOrder);
        //savedOrder.setPayment(payment);

        return OrderReadDTO.fromOrder(savedOrder);
    }


    public void delete(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Order not found: " + id);
        }
        orderRepository.deleteById(id);
    }
}
