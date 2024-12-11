package com.diplom.cloudstorage.controller;

import com.diplom.cloudstorage.dtos.FileDto;
import com.diplom.cloudstorage.entity.File;
import com.diplom.cloudstorage.entity.User;
import com.diplom.cloudstorage.jwt.JwtUtils;
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
    private final JwtUtils jwtUtils;

    public FilesController(FilesService filesService, JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
        this.filesService = filesService;
    }

    @PostMapping("/file")
    public ResponseEntity<?> addFile(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String filename, @RequestBody MultipartFile file) {
        User user = jwtUtils.getAuthenticatedUser(authToken);
        filesService.uploadFile(user, filename, file);
        log.info("Success added file: {}", filename);
        return ResponseEntity.ok("Success added");
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String filename) {
        User user = jwtUtils.getAuthenticatedUser(authToken);
        filesService.deleteFile(user, filename);
        log.info("Success deleted file: {}", filename);
        return ResponseEntity.ok("Success deleted");
    }

    @GetMapping("/file")
    public ResponseEntity<?> downloadFile(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String filename) {
        User user = jwtUtils.getAuthenticatedUser(authToken);
        log.info("Download file: {}", filename);
        return ResponseEntity.ok(filesService.downloadFile(user, filename));
    }

    @PutMapping("/file")
    public ResponseEntity<?> updateFile(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String filename, @RequestBody FileDto file) {
        User user = jwtUtils.getAuthenticatedUser(authToken);
        filesService.updateFile(user, filename, file.getFilename());
        log.info("Success updated file: {}", filename);
        return ResponseEntity.ok("Success updated");
    }

    @GetMapping("/list")
    public ResponseEntity<List<File>> getAllFiles(@RequestHeader("auth-token") String authToken, @RequestParam("limit") int limit) {
        User user = jwtUtils.getAuthenticatedUser(authToken);
        List<File> files = filesService.getAllFiles(user, limit);
        log.info("Get list of files with limit: {}", limit);
        return new ResponseEntity<>(files, HttpStatus.OK);
    }
}

