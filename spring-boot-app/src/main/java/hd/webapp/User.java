package hd.webapp;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter; 

@Setter
@Getter
@Entity
@Table(name = "users") // Explicitly names the table 'users'
public class User implements UserDetails {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1118084838557296937L;

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

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + getRole()));
	}

	@Override
	public String getUsername() {
		
		return name;
	}
}

