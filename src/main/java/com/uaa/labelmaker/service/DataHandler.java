package com.uaa.labelmaker.service;

import com.uaa.labelmaker.model.LabelDto;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

public interface DataHandler
{
    List<LabelDto> readBook(InputStream inputStream);

    ResponseEntity<InputStreamResource> handle(MultipartFile file);

    ByteArrayInputStream create(List<LabelDto> labels);

}
