package com.diplom.cloudstorage.repository;

import com.diplom.cloudstorage.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FilesRepository extends JpaRepository<File, Long> {

    @Query("SELECT f FROM File f WHERE f.user.id = :id ORDER BY f.createdAt DESC LIMIT :limit")
    List<File> getFilesByUserWithLimit(long id, int limit);

    @Query("SELECT f FROM File f WHERE f.user.id = :id and f.filename = :filename")
    Optional<File> findFilesByUserIdAndFilename(long id, String filename);

    @Modifying
    @Query("UPDATE File f SET f.filename = :file WHERE f.user.id = :id and f.filename = :newFileName")
    int updateFileByFilename(String file, long id, String newFileName);

    @Modifying
    @Query("DELETE FROM File f WHERE f.user.id = :id and f.filename = :filename")
    int deleteFilesByFilename(long id, String filename);
}
