package com.udemy.log.controller;

import com.udemy.log.entity.Log;
import com.udemy.log.service.ElasticLogService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logs")
public class LogSearchController {

    private final ElasticLogService elasticLogService;

    public LogSearchController(ElasticLogService elasticLogService) {
        this.elasticLogService = elasticLogService;
    }

    @GetMapping("/search")
    public Page<Log> search(
            @RequestParam(required = false) String service,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String message,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return elasticLogService.search(service, level, message, from, to, page, size);
    }
}