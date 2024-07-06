package com.example.demo;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entity.FileEntity;
import com.example.demo.repository.FileRepository;
import com.example.demo.type.FileStatusType;

//Annotation
@RestController
@RequestMapping("api/v1/file")
public class FileController {
	@Autowired
	private FileRepository fileRepository;
	
	private final String PATH_RESOURCE = "src/main/resources/static/";
	
	// Uploading a file
	@PostMapping
	public FileResponse uploadFile(@RequestParam MultipartFile file) throws Exception{
		// Setting up the path of the file
		String generateName = FileUtils.generateRandomString(10);
		
		// Try block to check exceptions
		try(InputStream inputStream = file.getInputStream()) {
			String fileName = saveMultipart(file, PATH_RESOURCE , generateName);
			FileEntity entity = FileEntity.builder()
					.name(fileName)
					.type(file.getContentType())
					.createTime(new Timestamp(System.currentTimeMillis()))
					.size(file.getSize())
					.status(FileStatusType.ACTIVE)
					.build();
			FileEntity newEntity = fileRepository.save(entity);
			FileResponse response = new FileResponse();
			BeanUtils.copyProperties(newEntity, response);
			return response;
		}
	
		catch (Exception e) {
			throw e;
		}
	}
	
	@GetMapping
	public String sayHi() {
		return "Hello world";
	}
	
	@DeleteMapping("/{id}")
	public boolean deleteFile(@PathVariable Integer id) {
		Optional<FileEntity> fileOpt = fileRepository.findById(id);
		if(fileOpt.isEmpty()) return true;
		else {
			FileEntity entity = fileOpt.get();			
			File file = new File(PATH_RESOURCE + entity.getName());
			try {
				file.delete();
				fileRepository.delete(entity);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	private String saveMultipart(MultipartFile multipart, String path, String fileName) throws IOException {
		if (!multipart.isEmpty()) {			
			try {
				Path uploadPath = Paths.get(path);
				
				if (!Files.exists(uploadPath)) {
					Files.createDirectories(uploadPath);
				}
				
				try (InputStream inputStream = multipart.getInputStream()) {
					String result = fileName +"."+ getExtension(multipart);
					Path filePath = uploadPath.resolve(result);
					Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
					return result;
				} catch (IOException ex) {
					throw ex;
				}

			} catch (IOException e) {
				throw e;
			}	
		}
		throw new RuntimeException("File don't have data");
		
	}

	private String getExtension(MultipartFile multipart) {
		String[] multipartArr = multipart.getOriginalFilename().split("\\.");
		if(multipartArr.length == 0) throw new RuntimeException("File invalid");
		return multipartArr[multipartArr.length - 1];
	}
}