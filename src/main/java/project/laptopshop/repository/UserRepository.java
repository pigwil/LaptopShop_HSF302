package project.laptopshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import project.laptopshop.entity.User;

import org.springframework.data.domain.Pageable; // <-- ĐÚNG
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query("SELECT u.fullName, SUM(od.totalPrice) AS totalSpent " +
            "FROM User u " +
            "JOIN u.orders o " +
            "JOIN o.orderDetails od " +
            "GROUP BY u.fullName " +
            "ORDER BY totalSpent DESC")
    List<Object[]> findTop5BestCustomers(Pageable pageable);

}
