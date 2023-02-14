package com.natoshka.image.controller;

import com.natoshka.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ImageController {

    public final ImageService imageService;

    @PostMapping("/api/upload")
    public String handleImageUpload(@RequestParam("image") MultipartFile image) {
        return imageService.upload(image);
    }
}
