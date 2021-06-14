package com.mygroup.backendReslide.controller;

import com.mygroup.backendReslide.dto.AddressDto;
import com.mygroup.backendReslide.dto.ContactDto;
import com.mygroup.backendReslide.dto.response.GenericResponse;
import com.mygroup.backendReslide.exceptions.notFound.AddressNotFoundException;
import com.mygroup.backendReslide.service.AddressService;
import com.mygroup.backendReslide.service.GenericResponseService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/address")
@AllArgsConstructor
public class AddressController {
    private final AddressService addressService;
    private final GenericResponseService responseService;
    @DeleteMapping("/delete")
    public ResponseEntity<GenericResponse> delete(@RequestBody AddressDto addressRequest){
        try{
            addressService.delete(addressRequest);
            return new ResponseEntity<GenericResponse>(responseService.buildInformation("Deleted."), HttpStatus.OK);
        }catch(AddressNotFoundException e){
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.CONFLICT);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
