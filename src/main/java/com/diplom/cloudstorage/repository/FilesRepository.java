package com.diplom.cloudstorage.repository;

import com.diplom.cloudstorage.entity.Files;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FilesRepository extends JpaRepository<Files, Long> {

    @Query("SELECT f FROM Files f WHERE f.user.id = :id ORDER BY f.createdAt DESC LIMIT :limit")
    List<Files> getFilesByUserWithLimit(long id, int limit);

    @Query("SELECT f FROM Files f WHERE f.user.id = :id and f.filename = :filename")
    Optional<Files> findFilesByUserIdAndFilename(long id, String filename);

    @Modifying
    @Query("UPDATE Files f SET f.filename = :file WHERE f.user.id = :id and f.filename = :fileName")
    int updateFileByFilename(String file, long id, String fileName);

    @Modifying
    @Query("DELETE FROM Files f WHERE f.user.id = :id and f.filename = :filename")
    int deleteFilesByFilename(long id, String filename);
}
