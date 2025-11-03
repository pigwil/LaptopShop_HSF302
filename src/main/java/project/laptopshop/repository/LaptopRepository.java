package project.laptopshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import project.laptopshop.entity.Laptop;

import java.util.List;

@Repository
public interface LaptopRepository extends JpaRepository<Laptop, Long> {
    @Query("SELECT l FROM Laptop l WHERE l.is_deleted = 0")
    List<Laptop> findAllActive();

    @Query("SELECT l FROM Laptop l WHERE l.laptopCode = ?1 AND l.is_deleted = 0")
    Laptop findActiveByLaptopCode(String laptopCode);

    boolean existsByLaptopCode(String laptopCode);

    Laptop findByIdAndIs_deleted(long id, int isDeleted);
}
