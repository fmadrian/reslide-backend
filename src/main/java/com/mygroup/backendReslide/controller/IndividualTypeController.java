package com.mygroup.backendReslide.controller;

import com.mygroup.backendReslide.dto.IndividualTypeDto;
import com.mygroup.backendReslide.dto.response.GenericResponse;
import com.mygroup.backendReslide.exceptions.alreadyExists.IndividualCodeExistsException;
import com.mygroup.backendReslide.service.GenericResponseService;
import com.mygroup.backendReslide.service.IndividualTypeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/individual/type")
@AllArgsConstructor
public class IndividualTypeController {
    private final GenericResponseService responseService;
    private final IndividualTypeService individualTypeService;
    /*  @PostMapping("/create")
    private ResponseEntity<GenericResponse> create(@RequestBody IndividualTypeDto individualTypeDto){
        throw new RuntimeException("Not implemented, yet.");
    }
    @PutMapping("/update")
    private ResponseEntity<GenericResponse> update(@RequestBody IndividualTypeDto individualTypeDto){
        throw new RuntimeException("Not implemented, yet.");
    }*/
    @GetMapping("/getAll")
    private ResponseEntity getAll(){
        try{
            return new ResponseEntity(individualTypeService.getAll(), HttpStatus.OK);
        }
        catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
