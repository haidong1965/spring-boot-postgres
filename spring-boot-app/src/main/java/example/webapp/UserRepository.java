package example.webapp;


import org.springframework.data.jpa.repository.JpaRepository;

interface UserRepository extends JpaRepository<User, Long> {
	User findByName(String name);

	User getUserById(Long id);
	
}
