package com.valmet.watermark.service;

import com.valmet.watermark.service.impl.AddWaterMarkToPdfServiceImpl;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Configuration
public class AddWaterMarkToPdfService {
    @Value("${watermark.file.upload.directory}")
    private String uploadDir;
    @Value("${watermark.file.limit}")
    private int fileLimit;
    @Value("${watermark.downloader.message}")
    private String downloadMessage;
    private AddWaterMarkToPdfServiceImpl addWaterMarkToPdfService = null;

    public AddWaterMarkToPdfService(AddWaterMarkToPdfServiceImpl addWaterMarkToPdfService) {
        this.addWaterMarkToPdfService = addWaterMarkToPdfService;
    }

    public ResponseEntity<?> getWatermarkedPdf(List<MultipartFile> files, String strUserName) {
        try {
            File fileLogoPath = new ClassPathResource("/static/images/valmet_logo.png").getFile();
            System.out.println("Logo path:" + fileLogoPath.getPath());
            File uploadDirFile = new File(uploadDir);
            if (!uploadDirFile.exists() && !uploadDirFile.mkdirs()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
         //   purgeDirectory(new File(uploadDir));



            if (files.size() == 1) {
                MultipartFile multipartFile = files.get(0);
                //Save the original file
                File originalFile = new File(uploadDir + "watermark_" + multipartFile.getOriginalFilename());
                multipartFile.transferTo(originalFile);
                //add watermark
                addWaterMarkToPdfService.addWatermarkToExistingPdf(originalFile.getPath(), uploadDir + multipartFile.getOriginalFilename(), fileLogoPath.getPath(), strUserName, downloadMessage);
                // Prepare PDF response
                File watermarkedFile = new File(uploadDir + multipartFile.getOriginalFilename());
                FileSystemResource fileResource = new FileSystemResource(watermarkedFile);
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + watermarkedFile.getName());
                headers.setContentType(MediaType.APPLICATION_PDF);

                return ResponseEntity.ok()
                        .headers(headers)
                        .contentLength(watermarkedFile.length())
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(fileResource);
            } else {
                if (files.size() > fileLimit) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot upload more than 100 files.");
                }
                String[] fileNames2 = (String[]) files.stream().map(file -> addWaterMarkToPdfService.transferFileToServerpath(file, uploadDir)).toArray();
                System.out.println("Line 60 fileNames==" + fileNames2);
                
                List<String>fileNames=Arrays.asList(fileNames2);

                for (String fileName : fileNames) {
                    addWaterMarkToPdfService.addWatermarkToExistingPdf(uploadDir + fileName, uploadDir + "watermarked_" + fileName, fileLogoPath.getPath(), strUserName, downloadMessage);
                }

                ByteArrayOutputStream zipOutput = new ByteArrayOutputStream();
                try (ZipOutputStream zipOutputStream = new ZipOutputStream(zipOutput)) {
                    for (String filename : fileNames) {
                        File file = new File(uploadDir + "watermarked_" + filename);
                        System.out.println("Line 49 AddWaterMarkToPdfController:: " + file.getAbsolutePath());
                        zipOutputStream.putNextEntry(new ZipEntry(filename));
                        FileInputStream fileInputStream = new FileInputStream(file);
                        IOUtils.copy(fileInputStream, zipOutputStream);
                        fileInputStream.close();
                        zipOutputStream.closeEntry();
                    }
                    zipOutputStream.finish();
                }
                InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(zipOutput.toByteArray()));

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=files.zip")
                        .contentType(MediaType.parseMediaType("application/zip"))
                        .body(resource);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }


    void purgeDirectory(File dir) {
        for (File file: dir.listFiles()) {
            if (file.isDirectory())
                purgeDirectory(file);
            file.delete();
        }
    }
}
