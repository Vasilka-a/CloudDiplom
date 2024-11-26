package com.diplom.cloudstorage.controller;

import com.diplom.cloudstorage.dtos.FileDto;
import com.diplom.cloudstorage.entity.Files;
import com.diplom.cloudstorage.service.FilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController

public class FilesController {

    private final FilesService filesService;

    @Autowired
    public FilesController(FilesService filesService) {
        this.filesService = filesService;
    }

    @PostMapping("/file")
    public ResponseEntity<?> addFile(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String filename, @RequestBody MultipartFile file) {
        filesService.uploadFile(authToken, filename, file);
        return ResponseEntity.ok("Success added");
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String filename) {
        filesService.deleteFile(authToken, filename);
        return ResponseEntity.ok("Success deleted");
    }

    @GetMapping("/file")
    public ResponseEntity<?> downloadFile(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String filename) {
        return ResponseEntity.ok(filesService.downloadFile(authToken, filename));
    }

    @PutMapping("/file")
    public ResponseEntity<?> updateFile(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String filename, @RequestBody FileDto file) {
        filesService.updateFile(authToken, filename, file);
        return ResponseEntity.ok("Success updated");
    }

    @GetMapping("/list")
    public ResponseEntity<List<Files>> getAllFiles(@RequestHeader("auth-token") String authToken, @RequestParam("limit") int limit) {
        List<Files> files = filesService.getAllFiles(authToken, limit);
        return new ResponseEntity<>(files, HttpStatus.OK);
    }
}

