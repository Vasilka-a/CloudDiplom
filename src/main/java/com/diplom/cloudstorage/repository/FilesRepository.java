package com.diplom.cloudstorage.repository;

import com.diplom.cloudstorage.entity.Files;
import com.diplom.cloudstorage.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FilesRepository extends JpaRepository<Files, Long> {

    List<Files> getAllFilesByUser(User user);

    @Query("SELECT f FROM Files f WHERE f.user = :user ORDER BY f.createdAt DESC LIMIT :limit")
    List<Files> getFilesByUserWithLimit(User user, int limit);

    @Query("SELECT f FROM Files f WHERE f.user = :user and f.filename = :filename")
    Files findFilesByUserAndFilename(User user, String filename);

    @Modifying
    @Query("UPDATE Files f SET f.filename = :file WHERE f.user = :user and f.filename = :fileName")
    int updateFileByFilename(String file, User user, String fileName);

    @Modifying
    @Query("DELETE FROM Files f WHERE f.user = :user and f.filename = :filename")
    int deleteFilesByFilename(User user, String filename);
}
