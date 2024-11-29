package com.diplom.cloudstorage;

import com.diplom.cloudstorage.dtos.FileDto;
import com.diplom.cloudstorage.entity.Files;
import com.diplom.cloudstorage.entity.User;
import com.diplom.cloudstorage.jwt.JwtUtils;
import com.diplom.cloudstorage.repository.FilesRepository;
import com.diplom.cloudstorage.service.FilesService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FilesServiceTest {

    private static final String TOKEN = "token";
    private static final String FILE_NAME = "text.txt";
    @Autowired
    private FilesService filesService;
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private FilesRepository filesRepository;
    private User user;
    private Files fileFound;
    @MockitoBean
    private JwtUtils jwtUtils;

    @BeforeEach
    public void init() {
        user = User.builder().id(1L).login("user@user.user").password("user").build();
        fileFound = Files.builder()
                .id(5L)
                .filename(FILE_NAME)
                .size(1)
                .createdAt(LocalDate.now())
                .fileContent("text".getBytes())
                .user(user)
                .build();
    }

    @Test
    void downloadFileTestSuccessful() {
        Mockito.when(jwtUtils.getAuthenticatedUser(TOKEN)).thenReturn(user);
        Mockito.when(filesRepository.findFilesByUserIdAndFilename(user.getId(), FILE_NAME))
                .thenReturn(Optional.ofNullable(fileFound));

        byte[] downloadFile = filesService.downloadFile(TOKEN, FILE_NAME);

        Assertions.assertEquals(fileFound.getFileContent(), downloadFile);
    }

    @Test
    void deleteFileTestSuccessful() {
        Mockito.when(jwtUtils.getAuthenticatedUser(TOKEN)).thenReturn(user);
        Mockito.when(filesRepository.save(fileFound)).thenReturn(fileFound);
        Mockito.when(filesRepository.findFilesByUserIdAndFilename(user.getId(), FILE_NAME))
                .thenReturn(Optional.of(fileFound));
        Mockito.when(filesRepository.deleteFilesByFilename(user.getId(), FILE_NAME)).thenReturn(1);

        filesService.deleteFile(TOKEN, FILE_NAME);

        verify(filesRepository).deleteFilesByFilename(user.getId(), FILE_NAME);
    }

    @Test
    void updateFileTestSuccessful() {
        FileDto newFileName = new FileDto("newTestName.txt");
        Mockito.when(jwtUtils.getAuthenticatedUser(TOKEN)).thenReturn(user);
        Mockito.when(filesRepository.save(fileFound)).thenReturn(fileFound);
        Mockito.when(filesRepository.findFilesByUserIdAndFilename(user.getId(), FILE_NAME))
                .thenReturn(Optional.of(fileFound));
        Mockito.when(filesRepository.updateFileByFilename(newFileName.getFilename(), user.getId(), FILE_NAME))
                .thenReturn(1);

        filesService.updateFile(TOKEN, FILE_NAME, newFileName);

        verify(filesRepository).updateFileByFilename(newFileName.getFilename(), user.getId(), FILE_NAME);
    }

    @Test
    void addFileTestSuccessful() throws IOException {
        MultipartFile multipartFile = new MockMultipartFile(FILE_NAME, FILE_NAME,
                MediaType.TEXT_PLAIN_VALUE, "My new text".getBytes());

        Mockito.when(jwtUtils.getAuthenticatedUser(TOKEN)).thenReturn(user);
        Mockito.when(filesRepository.findFilesByUserIdAndFilename(user.getId(), FILE_NAME))
                .thenReturn((Optional.empty()));

        Files createdFile = Files.builder()
                .filename(FILE_NAME)
                .createdAt(LocalDate.now())
                .size((int) multipartFile.getSize())
                .fileContent(multipartFile.getBytes())
                .user(user)
                .build();

        filesService.uploadFile(TOKEN, FILE_NAME, multipartFile);

        verify(filesRepository).save(createdFile);
    }
}