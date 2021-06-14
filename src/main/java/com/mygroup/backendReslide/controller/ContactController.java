package com.mygroup.backendReslide.controller;

import com.mygroup.backendReslide.dto.AddressDto;
import com.mygroup.backendReslide.dto.ContactDto;
import com.mygroup.backendReslide.dto.response.GenericResponse;
import com.mygroup.backendReslide.exceptions.notFound.ContactNotFoundException;
import com.mygroup.backendReslide.service.ContactService;
import com.mygroup.backendReslide.service.GenericResponseService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contact")
@AllArgsConstructor
public class ContactController {
    private final ContactService contactService;
    private final GenericResponseService responseService;
    @DeleteMapping("/delete")
    public ResponseEntity<GenericResponse> delete(@RequestBody ContactDto contactRequest){
        try{
            contactService.delete(contactRequest);
            return new ResponseEntity<GenericResponse>(responseService.buildInformation("Deleted."), HttpStatus.OK);
        }catch(ContactNotFoundException e){
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.CONFLICT);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

