package com.janmg.salary.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import com.janmg.salary.SalaryService;
import com.janmg.salary.repository.CalculatedRepository;
import com.janmg.salary.repository.EmployeeRepository;
import com.janmg.salary.repository.TimeRepository;

@Controller
public class WebController implements ErrorController {

    @Autowired private TimeRepository timeRepository;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private CalculatedRepository calculatedRepository;
    @Autowired private SalaryService sal;
    
    @GetMapping("/")
    public String index(Model model) {
    	model.addAttribute("content", "home");  
        return "index";
    }

    @GetMapping("/employees")
    public String employees(Model model) {
    	emptyWarning(model);
        model.addAttribute("employees", employeeRepository.findAll());
    	model.addAttribute("content", "employees");

        return "index";
    }

    // @RequestParam("persid") String persid
	@PostMapping("/newRate")
    public String newrate(Model model, WebRequest webRequest) {
		String persid = webRequest.getParameter("persid");
		String rate = webRequest.getParameter("rate");
		
    	employeeRepository.setRateByPersid(new Integer(persid), Double.parseDouble(rate));

        model.addAttribute("message", "rate for " +persid+ " is now updated");
        model.addAttribute("employees", employeeRepository.findAll());
    	model.addAttribute("content", "employees");  
        return "index";
    }

    @GetMapping("/time")
    public String time(Model model) {
    	emptyWarning(model);
    	model.addAttribute("entries", timeRepository.findAll());
    	model.addAttribute("content", "time");  
        return "index";
    }

    @GetMapping("/calculated")
    public String calculated(Model model) {
    	emptyWarning(model);
        sal.calculate();
        model.addAttribute("entries", calculatedRepository.findAll());
    	model.addAttribute("content", "calculated");
        return "index";
    }

    @PostMapping("/upload")
    public String upload(Model model, @RequestParam("file") MultipartFile file) {
        String message = "";
        if ( file.isEmpty() ) {
            message = "The uploaded file was empty";
        } else {
            try {
                sal.upload(file.getBytes());
                message = "You successfully uploaded " + file.getName();
                return time(model);
            } catch ( Exception e ) {
                message = "File upload failed => " + e.getMessage();
            }
        }
        model.addAttribute("message", message);
        return index(model);
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    @ResponseBody 
    public void download(Model model, HttpServletResponse response) throws IOException {
        byte[] file = sal.download(3, 2014);
        response.setContentType("text/csv");
        response.addHeader("Content-disposition", "attachment;filename=monthly-output.csv");
        response.setContentLength(file.length);
        response.getOutputStream().write(file);
        model.addAttribute("message", "");
        model.addAttribute("content", "calculated");
        return "index";
    }

    @GetMapping("/demo")
    public String demo(Model model) throws IOException {
        sal.upload(IOUtils.toByteArray(getClass().getResourceAsStream("/HourList201403.csv")));
        model.addAttribute("message", "Now the original demo file HourList201403.csv will be used");
        return time(model);
    }

    @GetMapping("/reset")
    public String reset(Model model) {
    	sal.reset();
        model.addAttribute("message", "Timesheet has been reset");
		return index(model);
    }
    
    @RequestMapping("/error")
    public String error(Model model) {
        model.addAttribute("message", "This is a sad moment for all of us, something didn't work");
    	model.addAttribute("content", "surullinen");
    	return "index";
    }
 
    @Override
    public String getErrorPath() {
        return "index";
    }
    
    private void emptyWarning(Model model) {
    	if (employeeRepository.count() == 0) {
        	model.addAttribute("message", "Timesheet is empty, go to home and add some entries, maybe you like to click the demo button?");
    	}
	}
}
