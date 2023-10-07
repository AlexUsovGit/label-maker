package com.uaa.labelmaker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class DefaultStorageService implements StorageService
{
    Logger logger = LoggerFactory.getLogger(DefaultStorageService.class);

    @Override
    public InputStream upload(MultipartFile file)
    {
        try
        {
            return file.getInputStream();

        }
        catch (IOException e)
        {
            logger.warn("Can not upload file");
            return InputStream.nullInputStream();
        }
    }
}
