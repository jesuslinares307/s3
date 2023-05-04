package com.demo.s3.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.PutObjectRequest;

@Service
public class ASSServiceImpl implements AWSS3Service {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(ASSServiceImpl.class);

    private final AmazonS3 amazonS3;
    @Value("${aws.s3.bucket}")
    private String bucketName;

    public ASSServiceImpl(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    @Override
    public void uploadFile(MultipartFile file) {
        File mainFile = new File(file.getOriginalFilename());
            try (FileOutputStream stream =  new FileOutputStream(mainFile)){
                stream.write(file.getBytes());
                String newFileName =  System.currentTimeMillis() + "_" +  mainFile.getName();
                LOGGER.info("Subiendo el archivo con el nombre..."+newFileName);
                PutObjectRequest request =  new PutObjectRequest(bucketName,newFileName,mainFile);
                //nuestro bean le pasamos el request armado del sdk de aws
                amazonS3.putObject(request);
            } catch (IOException exception){
                LOGGER.error(exception.getMessage(), exception);
            }
    }

    @Override
    public List<String> getObjectFromS3() {
        ListObjectsV2Result result = amazonS3.listObjectsV2(bucketName);
        List<S3ObjectSummary> objects =  result.getObjectSummaries();
        List<String> list = objects.stream().map(item -> {
            return item.getKey();
        }).collect(Collectors.toList());
        return list;
    }

    @Override
    public InputStream downloadFile(String key) {
        S3Object object = amazonS3.getObject(bucketName, key);
        return object.getObjectContent();
    }
}
