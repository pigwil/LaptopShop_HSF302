package project.laptopshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.laptopshop.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
}
