package com.mygroup.backendReslide.controller;

import com.mygroup.backendReslide.dto.response.GenericResponse;
import com.mygroup.backendReslide.dto.IndividualDto;
import com.mygroup.backendReslide.exceptions.InternalError;
import com.mygroup.backendReslide.exceptions.alreadyExists.IndividualCodeExistsException;
import com.mygroup.backendReslide.exceptions.notFound.IndividualNotFoundException;
import com.mygroup.backendReslide.service.GenericResponseService;
import com.mygroup.backendReslide.service.IndividualService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/individual")
@AllArgsConstructor
public class IndividualController {
    private final GenericResponseService responseService;
    private final IndividualService individualService;
    @PostMapping("/create")
    public ResponseEntity create(@RequestBody IndividualDto individualDto){
        try{
            return new ResponseEntity(individualService.create(individualDto), HttpStatus.CREATED);
        }catch(IndividualCodeExistsException e){
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.CONFLICT);
        }
        catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/update")
    public ResponseEntity<GenericResponse> update(@RequestBody IndividualDto individualDto){
        try{
            individualService.update(individualDto);
            return new ResponseEntity<GenericResponse>(responseService.buildInformation("Updated."), HttpStatus.CREATED);
        }catch(IndividualCodeExistsException |IndividualNotFoundException e){
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.CONFLICT);
        }catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/search")
    public ResponseEntity search(@RequestParam(required = false) String query){
        try{
            return new ResponseEntity<List<IndividualDto>>(individualService.search(query), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<GenericResponse>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/get")
    public ResponseEntity getIndividual(@RequestParam Long id){
        try{
            return new ResponseEntity<IndividualDto>(individualService.getIndividual(id), HttpStatus.OK);
        }catch(IndividualNotFoundException e){
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.CONFLICT);
        }catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
