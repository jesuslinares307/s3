package com.demo.s3.controller;

import com.demo.s3.service.AWSS3Service;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.util.List;

@RestController
@RequestMapping("/s3")
public class UploadFileController {

    private final AWSS3Service awss3Service;

    public UploadFileController(AWSS3Service awss3Service) {
        this.awss3Service = awss3Service;
    }

    @PostMapping(value = "/upload")
    public ResponseEntity<String> uploadFile(@RequestPart(value = "file") MultipartFile file) {
        awss3Service.uploadFile(file);
        String response = "El archivo " + file.getOriginalFilename() + " fue cargado exitosamente a S3";
        return new ResponseEntity<String>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/list")
    public ResponseEntity<List<String>> listFiles() {
        return new ResponseEntity<List<String>>(awss3Service.getObjectFromS3(), HttpStatus.OK);
    }
    @GetMapping(value = "/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("key") String key) {
        InputStreamResource resource = new InputStreamResource(awss3Service.downloadFile(key));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment: filename=\""+key+"\"").body(resource);
    }
}
