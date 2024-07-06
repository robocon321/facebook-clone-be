package com.example.demo;

import java.sql.Timestamp;

import com.example.demo.type.FileStatusType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FileResponse {
	private Integer fileId;
	private String name;
	private String type;
	private Timestamp createTime;
	private Long size;
	private FileStatusType status;
}
