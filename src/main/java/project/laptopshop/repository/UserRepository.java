package project.laptopshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.laptopshop.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
