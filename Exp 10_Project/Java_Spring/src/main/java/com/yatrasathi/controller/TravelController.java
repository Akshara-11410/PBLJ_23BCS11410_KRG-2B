
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