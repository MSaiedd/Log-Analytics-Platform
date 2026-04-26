package com.udemy.log.controller;

import com.udemy.log.entity.Log;
import com.udemy.log.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/log")
public class LogController {

    private final LogService logService;

    @Autowired
    public LogController(LogService logService) {
        this.logService = logService;
    }

    @PostMapping
    public Log addEmployee(@RequestBody Log log){
        this.logService.fetchLog(log);
        return log;
    }
}
