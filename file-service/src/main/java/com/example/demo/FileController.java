package com.example.demo;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

//Annotation
@RestController
@RequestMapping("api/v1/file")
public class FileController {
	public final String PATH_RESOURCE = "src/main/resources/static/";
	
	// Uploading a file
	@PostMapping
	public String uploadFile(@RequestParam MultipartFile file, @RequestParam String uploadDir) throws Exception{
		// Setting up the path of the file
		String generateName = System.currentTimeMillis() + "_" + FileUtils.generateRandomString(10);
		
		// Try block to check exceptions
		try(InputStream inputStream = file.getInputStream()) {
			String fileName = saveMultipart(file, PATH_RESOURCE + uploadDir, generateName);
			return fileName;
			
		}
	
		// Catch block to handle exceptions
		catch (Exception e) {
			throw e;
		}
	}
	
	@GetMapping
	public String sayHi() {
		return "Hello world";
	}
	
	@DeleteMapping
	public boolean deleteFile(@RequestBody String path) {
		File file = new File(PATH_RESOURCE + path);
		return file.delete();
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
	
	private String saveFile(String uploadDir, String fileName, 
			MultipartFile multipartFile) throws IOException {
		Path uploadPath = Paths.get(uploadDir);
		
		if (!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}
		
		try (InputStream inputStream = multipartFile.getInputStream()) {
			String result = fileName +"."+ getExtension(multipartFile);
			Path filePath = uploadPath.resolve(result);
			Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
			return result;
		} catch (IOException ex) {
			throw ex;
		}
	}
	
	private String getExtension(MultipartFile multipart) {
		String[] multipartArr = multipart.getOriginalFilename().split("\\.");
		if(multipartArr.length == 0) throw new RuntimeException("File invalid");
		return multipartArr[multipartArr.length - 1];
	}
}