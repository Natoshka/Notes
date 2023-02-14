package com.natoshka.image.service;

import com.natoshka.image.entity.Image;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    String upload(MultipartFile image);

    Image getMetadata(String fileName);

}
