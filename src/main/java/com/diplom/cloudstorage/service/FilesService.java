package com.diplom.cloudstorage.service;

import com.diplom.cloudstorage.dtos.FileDto;
import com.diplom.cloudstorage.entity.Files;
import com.diplom.cloudstorage.entity.User;
import com.diplom.cloudstorage.exceptions.CrudExceptions;
import com.diplom.cloudstorage.exceptions.InputDataException;
import com.diplom.cloudstorage.jwt.JwtUtils;
import com.diplom.cloudstorage.repository.FilesRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class FilesService {
    private final FilesRepository filesRepository;
    private final JwtUtils jwtUtils;

    public void uploadFile(String authToken, String fileName, MultipartFile file) {
        User user = jwtUtils.getAuthenticatedUser(authToken);
        if (filesRepository.findFilesByUserIdAndFilename(user.getId(), fileName).isPresent()) {
            throw new InputDataException("Error input data", user.getId());
        }
        try {
            Files newFile = Files.builder()
                    .filename(fileName)
                    .createdAt(LocalDate.now())
                    .size((int) file.getSize())
                    .fileContent(file.getBytes())
                    .user(user).build();
            filesRepository.save(newFile);
        } catch (IOException e) {
            throw new CrudExceptions("Error upload file", user.getId());
        }
    }

    public void deleteFile(String authToken, String fileName) {
        User user = jwtUtils.getAuthenticatedUser(authToken);
        if (getFileFromRepository(user.getId(), fileName) != null) {
            if (filesRepository.deleteFilesByFilename(user.getId(), fileName) == 0) {
                throw new CrudExceptions("Error delete file", user.getId());
            }
        }
    }

    public byte[] downloadFile(String authToken, String fileName) {
        User user = jwtUtils.getAuthenticatedUser(authToken);
        Files file = getFileFromRepository(user.getId(), fileName);
        if (file != null) {
            byte[] fileContent = file.getFileContent();
            if (fileContent != null) {
                return fileContent;
            }
        }
        throw new CrudExceptions("Error download file", user.getId());
    }

    public void updateFile(String authToken, String fileName, FileDto file) {
        User user = jwtUtils.getAuthenticatedUser(authToken);
        if (getFileFromRepository(user.getId(), fileName) != null) {
            if (filesRepository.updateFileByFilename(file.getFilename(), user.getId(), fileName) == 0) {
                throw new CrudExceptions("Error update file", user.getId());
            }
        }
    }

    public List<Files> getAllFiles(String authToken, int limit) {
        User user = jwtUtils.getAuthenticatedUser(authToken);
        return filesRepository.getFilesByUserWithLimit(user.getId(), limit);
    }

    private Files getFileFromRepository(Long userId, String fileName) {
        return filesRepository.findFilesByUserIdAndFilename(userId, fileName)
                .orElseThrow(() -> new InputDataException("Error input data", userId));
    }
}
