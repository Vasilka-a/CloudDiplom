package com.diplom.cloudstorage.service;

import com.diplom.cloudstorage.entity.File;
import com.diplom.cloudstorage.entity.User;
import com.diplom.cloudstorage.exceptions.CrudExceptions;
import com.diplom.cloudstorage.exceptions.InputDataException;
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

    public void uploadFile(User user, String fileName, MultipartFile file) {
        if (filesRepository.findFilesByUserIdAndFilename(user.getId(), fileName).isPresent()) {
            throw new InputDataException("Error input data", user.getId());
        }
        try {
            File newFile = File.builder()
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

    public void deleteFile(User user, String fileName) {
        if (getFileFromRepository(user.getId(), fileName) != null) {
            if (filesRepository.deleteFilesByFilename(user.getId(), fileName) == 0) {
                throw new CrudExceptions("Error delete file", user.getId());
            }
        }
    }

    public byte[] downloadFile(User user, String fileName) {
        File file = getFileFromRepository(user.getId(), fileName);
        if (file != null) {
            byte[] fileContent = file.getFileContent();
            if (fileContent != null) {
                return fileContent;
            }
        }
        throw new CrudExceptions("Error download file", user.getId());
    }

    public void updateFile(User user, String fileName, String newFileName) {
        if (getFileFromRepository(user.getId(), fileName) != null) {
            if (filesRepository.updateFileByFilename(newFileName, user.getId(), fileName) == 0) {
                throw new CrudExceptions("Error update file", user.getId());
            }
        }
    }

    public List<File> getAllFiles(User user, int limit) {
        return filesRepository.getFilesByUserWithLimit(user.getId(), limit);
    }

    private File getFileFromRepository(Long userId, String fileName) {
        return filesRepository.findFilesByUserIdAndFilename(userId, fileName)
                .orElseThrow(() -> new InputDataException("Error input data", userId));
    }
}
