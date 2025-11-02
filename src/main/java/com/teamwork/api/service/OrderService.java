package com.teamwork.api.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teamwork.api.model.Order;
import com.teamwork.api.model.OrderItem;
import com.teamwork.api.model.Payment;
import com.teamwork.api.model.Product;
import com.teamwork.api.model.User;
import com.teamwork.api.model.DTO.OrderCreateUpdateDTO;
import com.teamwork.api.model.DTO.OrderItemDTO;
import com.teamwork.api.model.DTO.OrderReadDTO;
import com.teamwork.api.model.Enum.OrderStatus;
import com.teamwork.api.repository.OrderItemRepository;
import com.teamwork.api.repository.OrderRepository;
import com.teamwork.api.repository.ProductRepository;
import com.teamwork.api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PaymentService paymentService;
    private final OrderItemRepository orderItemService;

    private final ProductRepository productRepository;

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
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + itemDTO.getProductId()));

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setOrder(order);
            orderItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));

            order.getItems().add(orderItem);
            totalPrice = totalPrice.add(product.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));

            product.setStockQuantity(product.getStockQuantity() - itemDTO.getQuantity());
            productRepository.save(product);
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

                    Product product = productRepository.findById(itemDTO.getProductId())
                            .orElseThrow(() -> new RuntimeException("Product not found: " + itemDTO.getProductId()));

                    // Управление остатками: учтём разницу в количестве или смену продукта
                    int oldQty = orderItem.getQuantity();
                    Long oldProductId = orderItem.getProduct() != null ? orderItem.getProduct().getId() : null;
                    int newQty = itemDTO.getQuantity() != null ? itemDTO.getQuantity() : 0;

                    if (oldProductId != null && oldProductId.equals(product.getId())) {
                        int delta = newQty - oldQty;
                        product.setStockQuantity(product.getStockQuantity() - delta);
                        productRepository.save(product);
                    } else {
                        // Вернём старые остатки для предыдущего продукта
                        if (oldProductId != null) {
                            Product oldProduct = productRepository.findById(oldProductId)
                                    .orElse(null);
                            if (oldProduct != null) {
                                oldProduct.setStockQuantity(oldProduct.getStockQuantity() + oldQty);
                                productRepository.save(oldProduct);
                            }
                        }
                        // Уменьшим склад для нового продукта
                        product.setStockQuantity(product.getStockQuantity() - newQty);
                        productRepository.save(product);
                    }

                    orderItem.setProduct(product);
                    orderItem.setQuantity(newQty);
                    orderItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(newQty)));
                } else {
                    // Создаём новую позицию и добавляем в заказ
                    Product product = productRepository.findById(itemDTO.getProductId())
                            .orElseThrow(() -> new RuntimeException("Product not found: " + itemDTO.getProductId()));

                    OrderItem orderItem = new OrderItem();
                    orderItem.setProduct(product);
                    orderItem.setQuantity(itemDTO.getQuantity());
                    orderItem.setOrder(order);
                    orderItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));

                    order.getItems().add(orderItem);

                    product.setStockQuantity(product.getStockQuantity() - itemDTO.getQuantity());
                    productRepository.save(product);
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
