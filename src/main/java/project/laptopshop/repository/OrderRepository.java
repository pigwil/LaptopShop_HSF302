package project.laptopshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import project.laptopshop.entity.Order;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findAllByOrderStatusNotOrderByCreatedDateDesc(Order.Status status);
    List<Order> findAllByOrderStatusOrderByCreatedDateDesc(Order.Status status);
    List<Order> findByCreatedByIdAndOrderStatus(Long userId, Order.Status status);
}
