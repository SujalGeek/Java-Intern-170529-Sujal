package com.codingshuttle.youtube.hospitalManagement.controller;

import com.codingshuttle.youtube.hospitalManagement.dto.DoctorResponseDto;
import com.codingshuttle.youtube.hospitalManagement.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class HospitalController {

    private final DoctorService doctorService;

    @GetMapping("/doctors")
    public ResponseEntity<List<DoctorResponseDto>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }
}
