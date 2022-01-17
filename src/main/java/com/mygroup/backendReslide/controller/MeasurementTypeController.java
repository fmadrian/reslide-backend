package com.mygroup.backendReslide.controller;

import com.mygroup.backendReslide.dto.MeasurementTypeDto;
import com.mygroup.backendReslide.dto.response.GenericResponse;
import com.mygroup.backendReslide.exceptions.InternalError;
import com.mygroup.backendReslide.exceptions.alreadyExists.MeasurementTypeExistsException;
import com.mygroup.backendReslide.exceptions.notFound.MeasurementTypeNotFoundException;
import com.mygroup.backendReslide.service.GenericResponseService;
import com.mygroup.backendReslide.service.MeasurementTypeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/measurement-type/")
@AllArgsConstructor
public class MeasurementTypeController {

    private final MeasurementTypeService measurementTypeService;
    private final GenericResponseService responseService;

    @PostMapping("/create")
    public ResponseEntity<GenericResponse> create(@RequestBody MeasurementTypeDto measurementTypeRequest){
        try{
            return new ResponseEntity(measurementTypeService.create(measurementTypeRequest), HttpStatus.CREATED);
        }catch (MeasurementTypeNotFoundException | MeasurementTypeExistsException e){
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.CONFLICT);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<GenericResponse>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/update")
    public ResponseEntity<GenericResponse> update(@RequestBody MeasurementTypeDto measurementTypeRequest){
        try {
            measurementTypeService.update(measurementTypeRequest);
            return new ResponseEntity<GenericResponse>(responseService.buildInformation("Updated."), HttpStatus.OK);
        } catch (MeasurementTypeNotFoundException | MeasurementTypeExistsException e) {
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.CONFLICT);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<GenericResponse>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/switchStatus")
    public ResponseEntity<GenericResponse> switchStatus(@RequestBody MeasurementTypeDto measurementTypeRequest){
        try {
            measurementTypeService.switchStatus(measurementTypeRequest);
            return new ResponseEntity<GenericResponse>(responseService.buildInformation("Deactivated."), HttpStatus.OK);
        } catch (MeasurementTypeNotFoundException e) {
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.CONFLICT);
        } catch (Exception e) {     
            e.printStackTrace();
            return new ResponseEntity<GenericResponse>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/search")
    public ResponseEntity search(@RequestParam(required = false) String text){
        try {
            return new ResponseEntity<List<MeasurementTypeDto>>(measurementTypeService.search(text), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<GenericResponse>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/get/{id}")
    public ResponseEntity get(@PathVariable Long id){
        try{
            return new ResponseEntity(measurementTypeService.get(id), HttpStatus.OK);
        } catch (MeasurementTypeNotFoundException e) {
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<GenericResponse>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
