package hd.webapp;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter; 

@Setter
@Getter
@Entity
@Table(name = "users") // Explicitly names the table 'users'
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrementing primary key
    private Long id;

    // Maps to 'name' column
    @Column(nullable = false)
    private String name; 

    // Maps to 'role' column (e.g., ADMIN, USER, GUEST)
    @Column(nullable = false)
    private String role; 

    // This MUST store the HASHED password!
    @Column(nullable = false)
    private String password;

	// --- Constructors ---
    public User() {}
    
    // Constructor for easy object creation (optional, but helpful)
    public User(String name, String role, String password) {
        this.name = name;
        this.role = role;
        this.password = password; // This should already be hashed!
    }

    // Optional: Override toString for easy debugging
    @Override
    public String toString() {
        return "User [id=" + id + ", name=" + name + ", role=" + role + ", password=" + password + "]";
    }
}

