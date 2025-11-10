# Spring Boot Backend - Complete Implementation Guide

## 📁 Directory Structure
```
SpringBootBackend/
├── pom.xml (already created)
├── src/main/
│   ├── java/com/yatrasathi/
│   │   ├── YatraSathiApplication.java (already created)
│   │   ├── model/
│   │   │   ├── User.java
│   │   │   └── Gallery.java
│   │   ├── repository/
│   │   │   ├── UserRepository.java
│   │   │   └── GalleryRepository.java
│   │   ├── controller/
│   │   │   ├── AuthController.java
│   │   │   ├── TravelController.java
│   │   │   └── GalleryController.java
│   │   ├── service/
│   │   │   ├── UserService.java
│   │   │   ├── AmadeusService.java
│   │   │   └── CloudinaryService.java
│   │   ├── config/
│   │   │   └── SecurityConfig.java
│   │   └── security/
│   │       └── JwtUtil.java
│   └── resources/
│       └── application.properties (already created)
└── README.md
```

---

## 📝 Create All Java Files

### 1. Model Classes

#### `src/main/java/com/yatrasathi/model/User.java`
```java
package com.yatrasathi.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String name;
    private String email;
    private String password;
}
```

#### `src/main/java/com/yatrasathi/model/Gallery.java`
```java
package com.yatrasathi.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "galleries")
public class Gallery {
    @Id
    private String id;
    private String username;
    private String caption;
    private String dham;
    private String imageUrl;
    private List<String> likes = new ArrayList<>();
    private LocalDateTime createdAt = LocalDateTime.now();
}
```

---

### 2. Repository Interfaces

#### `src/main/java/com/yatrasathi/repository/UserRepository.java`
```java
package com.yatrasathi.repository;

import com.yatrasathi.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
}
```

#### `src/main/java/com/yatrasathi/repository/GalleryRepository.java`
```java
package com.yatrasathi.repository;

import com.yatrasathi.model.Gallery;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface GalleryRepository extends MongoRepository<Gallery, String> {
    List<Gallery> findByDham(String dham);
}
```

---

### 3. Security & JWT

#### `src/main/java/com/yatrasathi/security/JwtUtil.java`
```java
package com.yatrasathi.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
```

#### `src/main/java/com/yatrasathi/config/SecurityConfig.java`
```java
package com.yatrasathi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors().and()
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            );
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
```

---

### 4. Services

#### `src/main/java/com/yatrasathi/service/UserService.java`
```java
package com.yatrasathi.service;

import com.yatrasathi.model.User;
import com.yatrasathi.repository.UserRepository;
import com.yatrasathi.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    public Map<String, Object> register(String name, String email, String password) {
        Map<String, Object> response = new HashMap<>();
        
        if (userRepository.findByEmail(email).isPresent()) {
            response.put("success", false);
            response.put("message", "User already exists");
            return response;
        }
        
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        
        response.put("success", true);
        response.put("message", "User registered successfully");
        return response;
    }
    
    public Map<String, Object> login(String email, String password) {
        Map<String, Object> response = new HashMap<>();
        
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "User not found");
            return response;
        }
        
        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            response.put("success", false);
            response.put("message", "Invalid credentials");
            return response;
        }
        
        String token = jwtUtil.generateToken(email);
        Map<String, String> userData = new HashMap<>();
        userData.put("name", user.getName());
        userData.put("email", user.getEmail());
        
        response.put("success", true);
        response.put("message", "Login successful");
        response.put("token", token);
        response.put("user", userData);
        return response;
    }
}
```

#### `src/main/java/com/yatrasathi/service/CloudinaryService.java`
```java
package com.yatrasathi.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {
    
    @Value("${cloudinary.cloud.name}")
    private String cloudName;
    
    @Value("${cloudinary.api.key}")
    private String apiKey;
    
    @Value("${cloudinary.api.secret}")
    private String apiSecret;
    
    private Cloudinary cloudinary;
    
    @PostConstruct
    public void init() {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", cloudName,
            "api_key", apiKey,
            "api_secret", apiSecret
        ));
    }
    
    public String uploadImage(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return uploadResult.get("url").toString();
    }
}
```

#### `src/main/java/com/yatrasathi/service/AmadeusService.java`
```java
package com.yatrasathi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class AmadeusService {
    
    @Value("${amadeus.client.id}")
    private String clientId;
    
    @Value("${amadeus.client.secret}")
    private String clientSecret;
    
    private String accessToken;
    private long tokenExpiry;
    
    private void authenticate() {
        if (accessToken != null && System.currentTimeMillis() < tokenExpiry) {
            return;
        }
        
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://test.api.amadeus.com/v1/security/oauth2/token";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
            Map<String, Object> responseBody = response.getBody();
            accessToken = (String) responseBody.get("access_token");
            tokenExpiry = System.currentTimeMillis() + 1800000;
        } catch (Exception e) {
            throw new RuntimeException("Failed to authenticate with Amadeus", e);
        }
    }
    
    public List<Map<String, Object>> searchFlights(String origin, String destination, String date) {
        authenticate();
        
        RestTemplate restTemplate = new RestTemplate();
        String url = String.format(
            "https://test.api.amadeus.com/v2/shopping/flight-offers?originLocationCode=%s&destinationLocationCode=%s&departureDate=%s&adults=1&max=10",
            origin, destination, date
        );
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            Map<String, Object> body = response.getBody();
            List<Map<String, Object>> offers = (List<Map<String, Object>>) body.get("data");
            
            List<Map<String, Object>> flights = new ArrayList<>();
            for (Map<String, Object> offer : offers) {
                Map<String, Object> flight = new HashMap<>();
                List<Map<String, Object>> itineraries = (List<Map<String, Object>>) offer.get("itineraries");
                Map<String, Object> firstSegment = (Map<String, Object>) ((List<Map<String, Object>>) itineraries.get(0).get("segments")).get(0);
                
                flight.put("airline", firstSegment.get("carrierCode"));
                flight.put("departure", ((Map<String, Object>) firstSegment.get("departure")).get("at"));
                flight.put("arrival", ((Map<String, Object>) firstSegment.get("arrival")).get("at"));
                flight.put("price", ((Map<String, Object>) offer.get("price")).get("total"));
                flights.add(flight);
            }
            return flights;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
```

---

### 5. Controllers

#### `src/main/java/com/yatrasathi/controller/AuthController.java`
```java
package com.yatrasathi.controller;

import com.yatrasathi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(userService.register(
            request.get("name"),
            request.get("email"),
            request.get("password")
        ));
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(userService.login(
            request.get("email"),
            request.get("password")
        ));
    }
}
```

#### `src/main/java/com/yatrasathi/controller/TravelController.java`
```java
package com.yatrasathi.controller;

import com.yatrasathi.service.AmadeusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/travel")
@RequiredArgsConstructor
public class TravelController {
    
    private final AmadeusService amadeusService;
    
    private static final Map<String, String> DHAM_AIRPORTS = Map.of(
        "Badrinath", "DED",
        "Dwarka", "JGA",
        "Puri", "BBI",
        "Rameswaram", "TRZ"
    );
    
    @GetMapping("/flights")
    public ResponseEntity<?> getFlights(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam String date) {
        
        String destination = DHAM_AIRPORTS.getOrDefault(to, to);
        return ResponseEntity.ok(amadeusService.searchFlights(from, destination, date));
    }
    
    @GetMapping("/trains")
    public ResponseEntity<?> getTrains(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam String date) {
        
        Map<String, List<Map<String, Object>>> trains = Map.of(
            "Badrinath", List.of(
                Map.of("name", "Dehradun Express", "trainNo", "12001", "price", 650, "from", from, "to", to, "date", date, "link", "https://www.irctc.co.in/"),
                Map.of("name", "Uttarakhand Sampark Kranti", "trainNo", "15035", "price", 780, "from", from, "to", to, "date", date, "link", "https://www.irctc.co.in/")
            ),
            "Dwarka", List.of(
                Map.of("name", "Dwarka Express", "trainNo", "15636", "price", 900, "from", from, "to", to, "date", date, "link", "https://www.irctc.co.in/")
            ),
            "Puri", List.of(
                Map.of("name", "Puri Express", "trainNo", "18410", "price", 980, "from", from, "to", to, "date", date, "link", "https://www.irctc.co.in/")
            ),
            "Rameswaram", List.of(
                Map.of("name", "Rameswaram Express", "trainNo", "16779", "price", 1150, "from", from, "to", to, "date", date, "link", "https://www.irctc.co.in/")
            )
        );
        
        return ResponseEntity.ok(trains.getOrDefault(to, List.of()));
    }
    
    @GetMapping("/buses")
    public ResponseEntity<?> getBuses(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam String date) {
        
        Map<String, List<Map<String, Object>>> buses = Map.of(
            "Badrinath", List.of(
                Map.of("operator", "Uttarakhand Travels", "busNo", "UK01-EXP", "price", 550, "from", from, "to", to, "date", date, "link", "https://www.redbus.in/")
            ),
            "Dwarka", List.of(
                Map.of("operator", "Saurashtra Express", "busNo", "GJ01-DWK", "price", 980, "from", from, "to", to, "date", date, "link", "https://www.redbus.in/")
            ),
            "Puri", List.of(
                Map.of("operator", "Odisha Travels", "busNo", "OD01-PUR", "price", 700, "from", from, "to", to, "date", date, "link", "https://www.redbus.in/")
            ),
            "Rameswaram", List.of(
                Map.of("operator", "TNSTC", "busNo", "TN63-RMM", "price", 850, "from", from, "to", to, "date", date, "link", "https://www.redbus.in/")
            )
        );
        
        return ResponseEntity.ok(buses.getOrDefault(to, List.of()));
    }
}
```

#### `src/main/java/com/yatrasathi/controller/GalleryController.java`
```java
package com.yatrasathi.controller;

import com.yatrasathi.model.Gallery;
import com.yatrasathi.repository.GalleryRepository;
import com.yatrasathi.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RestController
@RequestMapping("/api/gallery")
@RequiredArgsConstructor
public class GalleryController {
    
    private final GalleryRepository galleryRepository;
    private final CloudinaryService cloudinaryService;
    
    @GetMapping("/{dhamName}")
    public ResponseEntity<?> getGallery(@PathVariable String dhamName) {
        return ResponseEntity.ok(galleryRepository.findByDham(dhamName));
    }
    
    @PostMapping("/upload")
    public ResponseEntity<?> uploadPhoto(
            @RequestParam("image") MultipartFile file,
            @RequestParam("username") String username,
            @RequestParam("caption") String caption,
            @RequestParam("dham") String dham) {
        
        try {
            String imageUrl = cloudinaryService.uploadImage(file);
            
            Gallery gallery = new Gallery();
            gallery.setUsername(username);
            gallery.setCaption(caption);
            gallery.setDham(dham);
            gallery.setImageUrl(imageUrl);
            
            galleryRepository.save(gallery);
            return ResponseEntity.status(201).body(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    @PutMapping("/{photoId}/like")
    public ResponseEntity<?> likePhoto(
            @PathVariable String photoId,
            @RequestBody Map<String, String> request) {
        
        Gallery gallery = galleryRepository.findById(photoId).orElse(null);
        if (gallery == null) {
            return ResponseEntity.notFound().build();
        }
        
        String username = request.get("username");
        if (gallery.getLikes().contains(username)) {
            gallery.getLikes().remove(username);
        } else {
            gallery.getLikes().add(username);
        }
        
        galleryRepository.save(gallery);
        return ResponseEntity.ok(Map.of("success", true));
    }
}
```

---

## 🚀 Run the Application

### Build and Run
```bash
cd C:\Users\Nikhil\Documents\YatraSathi\SpringBootBackend
mvn clean install
mvn spring-boot:run
```

Or run from IDE (IntelliJ IDEA / Eclipse):
1. Open project
2. Run `YatraSathiApplication.java`

---

## 📱 Update Flutter to Use Spring Boot

Change in `Frontend/lib/services/api_service.dart`:
```dart
static const String baseUrl = "http://10.122.13.214:5000/api/auth";
```

Change in `Frontend/lib/Gallery_screen.dart`:
```dart
final String baseUrl = "http://10.122.13.214:5000/api/gallery";
```

---

## ✅ Quick Start

1. **Install Java 17** (if not installed)
2. **Install Maven** (or use Maven Wrapper)
3. **Copy all code** from this guide into respective files
4. **Run**: `mvn spring-boot:run`
5. **Test**: http://localhost:5000/api/auth/login

Done! Your Spring Boot backend is ready! 🎉
