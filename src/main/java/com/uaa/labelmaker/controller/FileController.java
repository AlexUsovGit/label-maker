package com.uaa.labelmaker.controller;

import com.uaa.labelmaker.service.DataHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("files")
@CrossOrigin("http://localhost:4200")
public class FileController

{
    @Autowired
    DataHandler dataHandler;

    @PostMapping(value = "/upload", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> upload(@RequestParam("file") MultipartFile file)
    {
        return dataHandler.handle(file);
    }

}
