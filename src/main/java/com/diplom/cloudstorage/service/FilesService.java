package com.diplom.cloudstorage.service;

import com.diplom.cloudstorage.dtos.FileDto;
import com.diplom.cloudstorage.entity.Files;
import com.diplom.cloudstorage.entity.User;
import com.diplom.cloudstorage.exceptions.CrudExceptions;
import com.diplom.cloudstorage.exceptions.InputDataException;
import com.diplom.cloudstorage.exceptions.UnauthorizedException;
import com.diplom.cloudstorage.jwt.JwtUtils;
import com.diplom.cloudstorage.repository.FilesRepository;
import com.diplom.cloudstorage.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class FilesService {

    private final FilesRepository filesRepository;
    private JwtUtils jwtUtils;
    private UserRepository userRepository;

    public void uploadFile(String authToken, String fileName, MultipartFile file) {
        User user = getAuthenticatedUser(authToken);
        if (filesRepository.findFilesByUserAndFilename(user, fileName) != null) {
            throw new InputDataException("Error input data");
        }
        try {
            Files newFile = new Files(fileName, new Date(), (int) file.getSize(), file.getBytes(), user);
            filesRepository.save(newFile);
        } catch (IOException e) {
            throw new CrudExceptions("Error upload file");
        }
    }

    public void deleteFile(String authToken, String fileName) {
        User user = getAuthenticatedUser(authToken);
        if (filesRepository.findFilesByUserAndFilename(user, fileName) == null) {
            throw new InputDataException("Error input data");
        }
        if (filesRepository.deleteFilesByFilename(user, fileName) == 0) {
            throw new CrudExceptions("Error delete file");
        }
    }

    public byte[] downloadFile(String authToken, String fileName) {
        User user = getAuthenticatedUser(authToken);
        Files file = filesRepository.findFilesByUserAndFilename(user, fileName);
        if (file == null) {
            throw new InputDataException("Error input data");
        }
        byte[] fileContent = file.getFileContent();
        if (fileContent == null) {
            throw new CrudExceptions("Error download file");
        }
        return fileContent;
    }

    public void updateFile(String authToken, String fileName, FileDto file) {
        User user = getAuthenticatedUser(authToken);
        if (filesRepository.findFilesByUserAndFilename(user, fileName) == null) {
            throw new InputDataException("Error input data");
        }
        if (filesRepository.updateFileByFilename(file.getFilename(), user, fileName) == 0) {
            throw new CrudExceptions("Error update file");
        }
    }

    public List<Files> getAllFiles(String authToken, int limit) {
        User user = getAuthenticatedUser(authToken);
        return (limit == 0) ? filesRepository.getAllFilesByUser(user) : filesRepository.getFilesByUserWithLimit(user, limit);
    }

    public User getAuthenticatedUser(String authToken) {
        if (authToken.startsWith("Bearer ")) {
            String tokenWithoutBearer = authToken.substring(7);
            String login = jwtUtils.extractUsername(tokenWithoutBearer);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = userRepository.findUsersByLogin(login).orElse(null);
            if (authentication.isAuthenticated() && authentication.getPrincipal().equals(user)) {
                return user;
            }
        }
        throw new UnauthorizedException("Unauthorized error");
    }
}
