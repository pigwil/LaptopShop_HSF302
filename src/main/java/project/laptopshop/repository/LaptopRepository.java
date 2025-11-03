package project.laptopshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.laptopshop.entity.Laptop;

@Repository
public interface LaptopRepository extends JpaRepository<Laptop, Long> {
}
