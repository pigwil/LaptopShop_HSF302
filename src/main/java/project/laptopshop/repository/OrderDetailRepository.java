package project.laptopshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import project.laptopshop.entity.OrderDetail;

import org.springframework.data.domain.Pageable; // <-- ĐÚNG
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    @Query("SELECT od.laptop.laptopName, SUM(od.quantity) AS totalSold " +
            "FROM OrderDetail od " +
            "GROUP BY od.laptop.laptopName " +
            "ORDER BY totalSold DESC")
    List<Object[]> findTop5BestSellingProducts(Pageable pageable);
}


