package com.uaa.labelmaker.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface StorageService
{
    InputStream upload(MultipartFile file);
}
