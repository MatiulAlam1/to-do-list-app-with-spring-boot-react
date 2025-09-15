package com.valmet.watermark.controller;

import com.valmet.watermark.service.AddWaterMarkToPdfService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.authentication.AuthenticationManager;

import java.io.IOException;
import java.util.List;

@RestController
//@RequestMapping("/api")
public class AddWaterMarkToPdfController {
    private AddWaterMarkToPdfService addWaterMarkToPdfService = null;

//    private AuthenticationManager authenticationManager;
//    public AddWaterMarkToPdfController(AddWaterMarkToPdfService addWatermarkService, AuthenticationManager authenticationManager) {
//        this.addWaterMarkToPdfService = addWatermarkService;
//        this.authenticationManager = authenticationManager;
//    }

    @PostMapping(value = "/watermark2", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFiles(@RequestParam("files") List<MultipartFile> files,
                                         @RequestParam(value = "userName", required = false) String strUserName) throws IOException {
        System.out.println("Line 20 Calll.............file size=" + files.size() + "," + strUserName);
        if (files.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return addWaterMarkToPdfService.getWatermarkedPdf(files, strUserName);

    }
}
