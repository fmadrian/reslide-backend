package com.mygroup.backendReslide.controller;

import com.mygroup.backendReslide.dto.response.GenericResponse;
import com.mygroup.backendReslide.service.GenericResponseService;
import com.mygroup.backendReslide.service.InstallationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/installation")
@AllArgsConstructor
public class InstallationController {
    private InstallationService installationService;
    private GenericResponseService responseService;
    @GetMapping("/setup")
    public ResponseEntity<GenericResponse> setup(){
        try{
            this.installationService.setup();
            return new ResponseEntity<GenericResponse>(responseService.buildInformation("Installation complete."), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
