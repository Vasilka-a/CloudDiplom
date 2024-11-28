package com.diplom.cloudstorage.controller;

import com.diplom.cloudstorage.dtos.FileDto;
import com.diplom.cloudstorage.entity.Files;
import com.diplom.cloudstorage.service.FilesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
public class FilesController {
    private final FilesService filesService;

    public FilesController(FilesService filesService) {
        this.filesService = filesService;
    }

    @PostMapping("/file")
    public ResponseEntity<?> addFile(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String filename, @RequestBody MultipartFile file) {
        filesService.uploadFile(authToken, filename, file);
        log.info("Success added file: {}", filename);
        return ResponseEntity.ok("Success added");
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String filename) {
        filesService.deleteFile(authToken, filename);
        log.info("Success deleted file: {}", filename);
        return ResponseEntity.ok("Success deleted");
    }

    @GetMapping("/file")
    public ResponseEntity<?> downloadFile(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String filename) {
        log.info("Download file: {}", filename);
        return ResponseEntity.ok(filesService.downloadFile(authToken, filename));
    }

    @PutMapping("/file")
    public ResponseEntity<?> updateFile(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String filename, @RequestBody FileDto file) {
        filesService.updateFile(authToken, filename, file);
        log.info("Success updated file: {}", filename);
        return ResponseEntity.ok("Success updated");
    }

    @GetMapping("/list")
    public ResponseEntity<List<Files>> getAllFiles(@RequestHeader("auth-token") String authToken, @RequestParam("limit") int limit) {
        List<Files> files = filesService.getAllFiles(authToken, limit);
        log.info("Get list of files with limit: {}", limit);
        return new ResponseEntity<>(files, HttpStatus.OK);
    }
}

