package com.janmg.salary.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.janmg.salary.SalaryService;
import com.janmg.salary.repository.CalculatedRepository;
import com.janmg.salary.repository.EmployeeRepository;
import com.janmg.salary.repository.TimeRepository;

@Controller
public class WebController implements ErrorController {

    @Autowired
    private TimeRepository timeRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private CalculatedRepository calculatedRepository;
    @Autowired
    private SalaryService sal;
    
    @PostConstruct
    public void init() {
    	// TODO: nothing to init? remove method
    }
    
    @GetMapping("/")
    public String index(Model model) {
    	model.addAttribute("content", "home");  
        return "index";
    }

    @GetMapping("/employees")
    public String employees(Model model) {
        model.addAttribute("employees", employeeRepository.findAll());
    	model.addAttribute("content", "employees");  
        return "index";
    }

    @GetMapping("/time")
    public String time(Model model) {
        model.addAttribute("entries", timeRepository.findAll());
    	model.addAttribute("content", "time");  
        return "index";
    }

    @GetMapping("/calculated")
    public String calculated(Model model) {
    	sal.calculate();
        model.addAttribute("entries", calculatedRepository.findAll());
    	model.addAttribute("content", "calculated");
        return "index";
    }

    @PostMapping("/upload")
    public @ResponseBody String handleFileUpload(Model model, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
    	String message = "";
         if ( !file.isEmpty() ) {
             String name = file.getName();
             try {
                 sal.upload(file.getBytes());
                 message = "You successfully uploaded " + name;
             }
             catch ( Exception e ) {
                 message = "Upload failed for " + name + " => " + e.getMessage();
             }
         } else {
              message = "The uploaded file was empty";
         }
        return time(model);
     }

    @RequestMapping(value = "/demo", method = RequestMethod.GET)
    public String demo(Model model) throws IOException {
    	String message = "Now the original demo file HourList201403.csv will be used";
    	byte[] in = IOUtils.toByteArray(getClass().getResourceAsStream("/HourList201403.csv"));
        sal.upload(in);
        
        return time(model);
    }

    @RequestMapping("/error")
    public String error(Model model) {
    	model.addAttribute("content", "surullinen");
    	return "index";
    }
 
    @Override
    public String getErrorPath() {
        return "index";
    }
}
