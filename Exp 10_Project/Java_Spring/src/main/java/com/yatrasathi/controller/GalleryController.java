
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
