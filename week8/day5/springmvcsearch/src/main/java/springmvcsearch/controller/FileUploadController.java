package springmvcsearch.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileUploadController {
	
	@RequestMapping("/fileform")
	public String showUploadForm() {
		return "fileform";
	}
	
	@RequestMapping(value ="/uploadFile", method = RequestMethod.POST)
	public String fileUpload(@RequestParam("profile") MultipartFile file, Model model)
	{
		System.out.println(file.getOriginalFilename());
		System.out.println(file.getSize());
		System.out.println(file.getContentType());
		   try {
	            // Get the file bytes
	            byte[] fileData = file.getBytes();
	            
	            // Define the path where the file will be saved
	            String path = "C:/uploads/" + File.separator + file.getOriginalFilename();
	            File newFile = new File(path);
	            
	            // Use FileOutputStream to write the file data
	            FileOutputStream fos = new FileOutputStream(newFile);
	            fos.write(fileData);
	            fos.close();
	            
	            model.addAttribute("msg", "File uploaded successfully!");
	        } catch (IOException e) {
	            e.printStackTrace();
	            model.addAttribute("msg", "File upload failed: " + e.getMessage());
	        }

	        return "filesuccess";
	}

}
