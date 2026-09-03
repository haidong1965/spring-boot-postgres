package hd.webapp;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;

interface UserFoodRepository extends JpaRepository<UserFood, Long> {
	List<UserFood> findByUserId(Long userId);
	
	@Modifying
    // Notice we use "uf.userId" (Java field), NOT "uf.user_id" (DB column)
    @Query("DELETE FROM UserFood uf WHERE uf.userId = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);

}
