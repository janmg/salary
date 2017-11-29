package com.janmg.salary;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.janmg.salary.model.TimeEntry;
import com.janmg.salary.model.TimeRepository;

@Controller
public class WebController {

    @Autowired
    private TimeRepository timeRepository;

    @PostConstruct
    public void init() throws IOException {
    	//make sure the databaseloader has done it's job
    }

    @RequestMapping(value = "/time", method = RequestMethod.GET)
    public String list(Model model) {
        model.addAttribute("entries", timeRepository.findAll());
        return "entries";
    }
}
