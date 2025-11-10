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