package com.teamwork.api.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.teamwork.api.model.Order;
import com.teamwork.api.model.DTO.SalesReportRowDTO;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByUserId(Long userId);

    List<Order> findAllByUserId(Long userId);

    @Query("""
                    select new com.teamwork.api.model.DTO.SalesReportRowDTO(
                        i.product.name,
                        i.quantity,
                        i.product.price,
                                i.product.height,
            i.product.width
                    )
                    from Order o
                    join o.payment p
                    join o.orderItems i
                    where p.status = com.teamwork.api.model.Enum.PaymentStatus.SUCCEEDED
                      and p.paymentDate between :from and :to
                    order by i.product.name asc
                """)
    List<SalesReportRowDTO> findSalesReport(LocalDateTime from, LocalDateTime to);
}
