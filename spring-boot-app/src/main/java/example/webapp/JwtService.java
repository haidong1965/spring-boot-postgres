package example.webapp;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.IOException;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.security.PublicKey;

@Service
public class JwtService {

	private final ResourceLoader resourceLoader;

	private final JwtProperties jwtProperties;

	private PrivateKey signingKey;

	public PrivateKey getSigningKey() {
		return signingKey;
	}

	public JwtService(ResourceLoader resourceLoader, JwtProperties jwtProperties) throws Exception {
		this.resourceLoader = resourceLoader;
		this.jwtProperties = jwtProperties;
		this.signingKey = loadPrivateKeyFromPem();
		System.out.println("+++++++++++++++ expiration:" + jwtProperties.getExpiration());
	}

	public String extractSubject(String token) {
		Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token);

		Claims claims = claimsJws.getBody();
		return claims.getSubject();
	}

	public String extractExpiration(String token) {
		Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token);

		Claims claims = claimsJws.getBody();
		return claims.getExpiration().toGMTString();
	}

	private PrivateKey loadPrivateKeyFromPem() throws Exception {
		URL resourceURL = getClass().getClassLoader().getResource("authz.pem");
		Resource privateKeyResource = resourceLoader.getResource(resourceURL.toString());
		String pemContent = privateKeyResource.getContentAsString(StandardCharsets.UTF_8);

		// 2. Use Regex to capture ONLY the Base64 payload between BEGIN and END markers
		// This regex assumes standard PKCS#8 format.
		Pattern pattern = Pattern.compile("-----BEGIN (PRIVATE|PUBLIC) KEY-----([\\s\\S]*?)-----END \\1 KEY-----",
				Pattern.DOTALL);
		Matcher matcher = pattern.matcher(pemContent);

		if (!matcher.find()) {
			throw new IOException("Could not find standard PEM key markers in the file.");
		}

		// Group 2 contains the clean base64 payload, spread across multiple lines.
		String base64Payload = matcher.group(2).replaceAll("\\s+", ""); // Remove all whitespace (newlines/spaces)

		if (base64Payload.isEmpty()) {
			throw new IOException("Extracted Base64 payload is empty.");
		}

		// 3. Convert the clean Base64 string back into raw bytes
		byte[] keyBytes = java.util.Base64.getDecoder().decode(base64Payload);

		// 4. Initialize KeyFactory and generate the object (This part was correct,
		// assuming bytes are now pure)
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);

		return kf.generatePrivate(keySpec);
	}

	public String generateToken(String subject) {
		if (signingKey == null) {
			throw new IllegalStateException("Signing key has not been initialized correctly.");
		}

		Date now = new Date();
		String jwtToken = Jwts.builder().setSubject(subject) // e.g., User ID
				.setId(UUID.randomUUID().toString()).setIssuedAt(now)
				.setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
				// --- THE CRITICAL STEP: Signing with the loaded PrivateKey ---
				.signWith(signingKey, SignatureAlgorithm.RS256).compact();

		return jwtToken;
	}

	public boolean verifyToken(String token) throws Exception {
		URL resourceURL = getClass().getClassLoader().getResource("authz.pub");
		Resource publkicKeyResource = resourceLoader.getResource(resourceURL.toString());
		String pemContent = publkicKeyResource.getContentAsString(StandardCharsets.UTF_8);
		PublicKey publicKey = loadPublicKeyFromPem(pemContent);

		if (Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(token) != null) {
			Date exDate = new Date(extractExpiration(token));
			if (exDate.before(new Date()))
				return false;
			return true;
		}
		return false;
	}

	private PublicKey loadPublicKeyFromPem(String pemKeyString) throws Exception {
		String cleanBase64 = pemKeyString;

		cleanBase64 = pemKeyString.replaceAll("-----BEGIN PUBLIC KEY-----", "") // Remove start header
				.replaceAll("-----END PUBLIC KEY-----", "") // Remove end footer
				.replaceAll("\\s+", "");
		if (!cleanBase64.isEmpty()) {
			byte[] decodedKeyBytes = Base64.getDecoder().decode(cleanBase64);
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKeyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			return keyFactory.generatePublic(keySpec);
		}
		return null;
	}

	public static void main(String[] args) throws Exception { // --- MAIN EXECUTION BLOCK ---
		ResourceLoader mockResourceLoader = new ResourceLoader() {
			@Override
			public Resource getResource(String location) {
				String osPath = location.replace("file:/", ""); // Result:
																// "/C:/spring_courses/webapp/bin/main/authz.pem"
				// Note: Windows paths might need better sanitization depending on the OS
				// running the code!
				// Use Paths.get() which handles OS-specific path separators robustly.
				java.nio.file.Path javaPath = Paths.get(osPath);
				FileSystemResource fsResource2 = new FileSystemResource(javaPath.toString());
				return fsResource2;
			}

			@Override
			public ClassLoader getClassLoader() {
				// TODO Auto-generated method stub
				return null;
			}
		};

		JwtService service = new JwtService(mockResourceLoader, new JwtProperties());
		String userId = "hdo";
		// 2. Generate a token for a sample user String userId = "hdo"; String token
		String token = service.generateToken(userId);

		System.out.println("\n===============================================");
		System.out.println("✨ TOKEN GENERATION SUCCESSFUL ✨");
		System.out.println("User Subject: " + userId);
		System.out.println("Generated JWT (RS256):");
		System.out.println(token);
		System.out.println("===============================================");

		// 3. Demonstrate verification (requires public key setup)
		if (service.verifyToken(token)) {
			System.out.println("\n===============================================");
			System.out.println("✨ TOKEN VALIDATION SUCCESSFUL for subject : " + service.extractSubject(token) + " ✨");
			System.out.println("\n===============================================");
		}

	}

}