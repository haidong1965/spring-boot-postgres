package hd.webapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@RestController
@RequestMapping("/file")
//@CrossOrigin(origins = {"https://2.106.164.55:5173"}) 
public class FileController {
	
	private static final Logger log = LoggerFactory.getLogger(FileController.class);

	// Define where you want to save the files
	private static final String UPLOAD_DIR = "uploads/";

	private final Path baseDir = Paths.get("uploads").toAbsolutePath().normalize();

	private final JwtService jwtService;

	public FileController(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@GetMapping("/list")
	public List<String> getFileList() throws IOException {
		log.info("++++++++++++++ show files under {} +++++++++++++", UPLOAD_DIR);
		Path folder = Paths.get(UPLOAD_DIR);
		List<String> files = new ArrayList<>();
		// Optional: Create the directory if it doesn't exist yet
		if (!Files.exists(folder)) {
			Files.createDirectories(folder);
		}

		// Use Java NIO Streams to read the folder
		try (Stream<Path> paths = Files.list(folder)) {
			files = paths.filter(Files::isRegularFile) // Filter out sub-directories
					.map(Path::getFileName) // Get just the file name
					.map(Path::toString) // Convert to String
					.sorted() // Sort alphabetically
					.toList();
			return files;
		}
	}

	@PostMapping("/upload")
	public ResponseEntity<String> uploadFile(@RequestHeader(name = "Authorization", required = false) String authHeader,
			@RequestParam("myFile") MultipartFile file) throws Exception {
		String token = null;
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			token = authHeader.substring(7);
		}
		if (token == null) {
			return ResponseEntity.status(500).body("");
		}
		if (!jwtService.verifyToken(token)) {
			return ResponseEntity.status(500).body("");
		}
		Map<String, String> response = new HashMap<>();

		// 1. Check if the file is empty
		if (file.isEmpty()) {
			response.put("message", "Please select a file to upload.");
			return ResponseEntity.badRequest().body("");
		}

		try {
			// 2. Create the upload directory if it doesn't exist
			Path uploadPath = Paths.get(UPLOAD_DIR);
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			// 3. Save the file to the disk
			// We use the original filename, but in production, you should generate a unique
			// name (e.g., UUID)
			String fileName = file.getOriginalFilename();
			Path filePath = uploadPath.resolve(fileName);

			Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
			log.info("++++++++++++++ {} uploaded ++++++++++++++", fileName);
			return ResponseEntity.ok("File uploaded successfully : " + fileName);

		} catch (IOException e) {
			return ResponseEntity.status(500).body("");
		}
	}

	@DeleteMapping("/delete/{fileName}")
	public ResponseEntity<String> deleteUser(@RequestHeader(name = "Authorization", required = false) String authHeader,
			@PathVariable String fileName) throws Exception {
		log.info("++++++++++++++ delete file {} ++++++++++++", fileName);
		String token = null;
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			token = authHeader.substring(7);
		}
		if (token == null) {
			return ResponseEntity.status(500).body("");
		}
		if (!jwtService.verifyToken(token)) {
			return ResponseEntity.status(500).body("");
		}
		Path filePath = baseDir.resolve(fileName).normalize();
		if (!filePath.startsWith(baseDir)) {
			return ResponseEntity.badRequest().build(); // 400 Bad Request
		}
		boolean deleted = Files.deleteIfExists(filePath);
		if (deleted) {
            // 204 No Content is the REST standard for a successful DELETE
            return ResponseEntity.noContent().build(); 
        }
        return ResponseEntity.notFound().build(); 
	}
}