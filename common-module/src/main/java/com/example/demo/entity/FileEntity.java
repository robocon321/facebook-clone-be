package com.example.demo.entity;

import java.sql.Timestamp;

import com.example.demo.type.FileStatusType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "file_management")
public class FileEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer fileId;
	
	@Column(nullable = false, columnDefinition = "VARCHAR(100)")
	private String name;
	
	@Column(nullable = false)
	private Timestamp createTime;
	
	@Column(nullable = false)
	private Long size;
	
	@Transient
	private FileStatusType status;

	@Column(nullable = false, name = "status", columnDefinition = "CHAR(1)")
	private Character statusValue;

    @PrePersist
    @PreUpdate
    void fillGenderPersistent() {
        this.statusValue = status.getStatus();
    }

    @PostLoad
    void fillGenderTransient() {
        this.status = FileStatusType.of(statusValue);
    }	
}
