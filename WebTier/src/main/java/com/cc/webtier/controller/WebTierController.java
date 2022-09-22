package com.cc.webtier.controller;

import com.cc.webtier.service.WebTierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/")
public class WebTierController {

    private final WebTierService webTierService;

    @PostMapping("/imageUpload")
    public ResponseEntity<Map<String, String>> imageUpload(@RequestParam("images") MultipartFile[] images) {
        log.info("Received Images for Classification");
        return ResponseEntity.ok().body(webTierService.processImages(images));
    }

    @GetMapping("/results")
    public ResponseEntity<Map<String, String>> getResults() {
        return ResponseEntity.ok().body(webTierService.getResults());
    }
}
