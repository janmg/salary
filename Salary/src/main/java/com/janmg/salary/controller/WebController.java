package com.janmg.salary.controller;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.janmg.salary.domain.Employee;
import com.janmg.salary.repository.EmployeeRepository;
import com.janmg.salary.repository.TimeRepository;

@Controller
public class WebController {

    @Autowired
    private TimeRepository timeRepository;
	@Autowired
	private EmployeeRepository employeeRepository;
	
	@PostConstruct
	public void init() {
		employeeRepository.save(new Employee(1, "Janet Java", 3.75));
		employeeRepository.save(new Employee(2, "Scott Scala", 3.75));
		employeeRepository.save(new Employee(3, "Larry Lolcode", 3.75));
	}
	
	@RequestMapping(value="/employees", method = RequestMethod.GET)
	public String employees(Model model) {
		model.addAttribute("employees", employeeRepository.findAll());
		return "employees";
	}

    @RequestMapping(value = "/time", method = RequestMethod.GET)
    public String time(Model model) {
        model.addAttribute("entries", timeRepository.findAll());
        return "entries";
    }
    
    @RequestMapping(value = "/calculated", method = RequestMethod.GET)
    public String calculated(Model model) {
        model.addAttribute("entries", timeRepository.findAll());
        return "entries";
    }
}