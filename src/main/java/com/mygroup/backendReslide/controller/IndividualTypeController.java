package com.mygroup.backendReslide.controller;

import com.mygroup.backendReslide.dto.IndividualTypeDto;
import com.mygroup.backendReslide.dto.response.GenericResponse;
import com.mygroup.backendReslide.exceptions.UserNotAuthorizedException;
import com.mygroup.backendReslide.exceptions.alreadyExists.IndividualCodeExistsException;
import com.mygroup.backendReslide.exceptions.alreadyExists.IndividualTypeExistsException;
import com.mygroup.backendReslide.exceptions.notFound.IndividualTypeNotFoundException;
import com.mygroup.backendReslide.service.GenericResponseService;
import com.mygroup.backendReslide.service.IndividualTypeService;
import io.swagger.models.Response;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

@RestController
@RequestMapping("/api/individual/type")
@AllArgsConstructor
public class IndividualTypeController {
    private final GenericResponseService responseService;
    private final IndividualTypeService individualTypeService;
    @PostMapping("/create")
    private ResponseEntity<GenericResponse> create(@RequestBody IndividualTypeDto individualTypeDto){
        try{
            return new ResponseEntity(individualTypeService.create(individualTypeDto,false), HttpStatus.OK);
        }catch(UserNotAuthorizedException | IndividualTypeExistsException e){
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.CONFLICT);
        }
        catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/update")
    private ResponseEntity<GenericResponse> update(@RequestBody IndividualTypeDto individualTypeDto){
        try{
            individualTypeService.update(individualTypeDto);
            return new ResponseEntity(responseService.buildInformation("Updated."), HttpStatus.OK);
        }
        catch(UserNotAuthorizedException | IndividualTypeNotFoundException | IndividualTypeExistsException e){
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.CONFLICT);
        }
        catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/switch")
    private ResponseEntity<GenericResponse> deactivate(@RequestBody IndividualTypeDto individualTypeDto){
        try{
            individualTypeService.deactivate(individualTypeDto);
            return new ResponseEntity(responseService.buildInformation("Deactivated."), HttpStatus.OK);
        } catch(UserNotAuthorizedException | IndividualTypeNotFoundException e){
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.CONFLICT);
        }
        catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
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
    @GetMapping("/get/{id}")
    private ResponseEntity get(@PathVariable Long id){
        try{
            return new ResponseEntity(individualTypeService.get(id), HttpStatus.OK);
        }catch(IndividualTypeNotFoundException e){
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.CONFLICT);
        }
        catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
