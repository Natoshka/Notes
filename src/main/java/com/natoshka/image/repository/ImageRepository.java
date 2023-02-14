package com.natoshka.image.repository;

import com.natoshka.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {

    Image getByPath(String path);
}
