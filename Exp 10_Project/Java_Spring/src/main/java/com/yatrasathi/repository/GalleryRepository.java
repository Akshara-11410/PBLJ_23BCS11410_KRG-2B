package com.yatrasathi.repository;

import com.yatrasathi.model.Gallery;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface GalleryRepository extends MongoRepository<Gallery, String> {
    List<Gallery> findByDham(String dham);
}