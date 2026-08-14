package hd.webapp;

import jakarta.persistence.*; 


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

    // --- Getters and Setters (Crucial for JPA/Hibernate to work) ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    // Optional: Override toString for easy debugging
    @Override
    public String toString() {
        return "User [id=" + id + ", name=" + name + ", role=" + role + ", password=" + password + "]";
    }
}

