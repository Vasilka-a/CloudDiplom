package com.diplom.cloudstorage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "files")
public class File {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String filename;

    @UpdateTimestamp
    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(nullable = false)
    private int size;

    @Column
    private byte[] fileContent;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}

