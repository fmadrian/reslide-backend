package com.mygroup.backendReslide.controller;

import com.mygroup.backendReslide.dto.ProductTypeDto;
import com.mygroup.backendReslide.dto.response.GenericResponse;
import com.mygroup.backendReslide.exceptions.InternalError;
import com.mygroup.backendReslide.exceptions.alreadyExists.ProductTypeExistsException;
import com.mygroup.backendReslide.exceptions.notFound.ProductTypeNotFoundException;
import com.mygroup.backendReslide.service.GenericResponseService;
import com.mygroup.backendReslide.service.ProductTypeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product/type")
@AllArgsConstructor
public class ProductTypeController {

    private final ProductTypeService productTypeService;
    private final GenericResponseService responseService;

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody ProductTypeDto productTypeRequest){
        try{
            productTypeService.register(productTypeRequest);
            return new ResponseEntity(responseService.buildInformation("Product type registered."), HttpStatus.OK);
        }catch (ProductTypeExistsException e){
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.UNAUTHORIZED);
        }catch (Exception e){
            return new ResponseEntity<GenericResponse>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/update")
    public ResponseEntity update(@RequestBody ProductTypeDto productTypeRequest) {
        try {
            productTypeService.update(productTypeRequest);
            return new ResponseEntity(responseService.buildInformation("Product type updated."), HttpStatus.OK);
        } catch (ProductTypeNotFoundException | ProductTypeExistsException e) {
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<GenericResponse>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/deactivate")
    public ResponseEntity deactivate(@RequestBody ProductTypeDto productTypeRequest){
        try {
            productTypeService.deactivate(productTypeRequest);
            return new ResponseEntity(responseService.buildInformation("Product type deactivated."), HttpStatus.OK);
        } catch (ProductTypeNotFoundException | ProductTypeExistsException e) {
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<GenericResponse>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // Searches for every active product type.
    @GetMapping("/search")
    public ResponseEntity getAll(){
        try{
            return new ResponseEntity(productTypeService.getAll(), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<GenericResponse>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // Searches using a part of the type as parameter
    @GetMapping("/search/{type}")
    public ResponseEntity getByType(@PathVariable String type){
        try{
            return new ResponseEntity(productTypeService.getByType(type), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<GenericResponse>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
