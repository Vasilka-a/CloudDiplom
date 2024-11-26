package com.diplom.cloudstorage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
public class Files {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private String filename;
    @UpdateTimestamp
    @Column(name = "created_at")
    private Date createdAt;
    @Column(nullable = false)
    private int size;
    @Column
    private byte[] fileContent;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public Files(String fileName, Date date, int size, byte[] bytes, User user) {
        this.filename = fileName;
        this.createdAt = date;
        this.size = size;
        this.fileContent = bytes;
        this.user = user;
    }
}

