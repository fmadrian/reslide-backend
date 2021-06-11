package com.mygroup.backendReslide.controller;

import com.mygroup.backendReslide.dto.ContactTypeDto;
import com.mygroup.backendReslide.dto.response.GenericResponse;
import com.mygroup.backendReslide.exceptions.InternalError;
import com.mygroup.backendReslide.exceptions.alreadyExists.ContactTypeExistsException;
import com.mygroup.backendReslide.exceptions.notFound.ContactTypeNotFoundException;
import com.mygroup.backendReslide.service.ContactTypeService;
import com.mygroup.backendReslide.service.GenericResponseService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@AllArgsConstructor
public class ContactTypeController {

    private final ContactTypeService contactTypeService;
    private final GenericResponseService responseService;
    @PostMapping("/create")
    public ResponseEntity<GenericResponse> create(@RequestBody ContactTypeDto contactTypeRequest){
        try{
            contactTypeService.create(contactTypeRequest);
            return new ResponseEntity<GenericResponse>(responseService.buildInformation("Created."), HttpStatus.CREATED);
        }catch (ContactTypeExistsException e){
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.CONFLICT);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<GenericResponse>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/update")
    public ResponseEntity<GenericResponse> update(@RequestBody ContactTypeDto contactTypeRequest){
        try{
            contactTypeService.update(contactTypeRequest);
            return new ResponseEntity<GenericResponse>(responseService.buildInformation("Updated."), HttpStatus.OK);
        }catch (ContactTypeExistsException | ContactTypeNotFoundException e){
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.CONFLICT);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<GenericResponse>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/search")
    public ResponseEntity search(@RequestParam(required = false) String type){
        try{
            return new ResponseEntity<List<ContactTypeDto>>(contactTypeService.search(type), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<GenericResponse>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
