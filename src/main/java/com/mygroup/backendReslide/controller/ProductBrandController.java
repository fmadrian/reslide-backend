package com.mygroup.backendReslide.controller;

import com.mygroup.backendReslide.dto.ProductBrandDto;
import com.mygroup.backendReslide.dto.response.GenericResponse;
import com.mygroup.backendReslide.exceptions.InternalError;
import com.mygroup.backendReslide.exceptions.alreadyExists.ProductBrandExistsException;
import com.mygroup.backendReslide.exceptions.alreadyExists.ProductTypeExistsException;
import com.mygroup.backendReslide.exceptions.notFound.ProductBrandNotFoundException;
import com.mygroup.backendReslide.exceptions.notFound.ProductTypeNotFoundException;
import com.mygroup.backendReslide.service.GenericResponseService;
import com.mygroup.backendReslide.service.ProductBrandService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

@RestController
@AllArgsConstructor
@RequestMapping("/api/product/brand")
public class ProductBrandController {

    private final ProductBrandService productBrandService;
    private final GenericResponseService responseService;

    @PostMapping("/create")
    public ResponseEntity create(@RequestBody ProductBrandDto productBrandDto){
        try{
            return new ResponseEntity(productBrandService.create(productBrandDto), HttpStatus.OK);
        }catch (ProductBrandNotFoundException | ProductBrandExistsException e){
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.CONFLICT);
        }catch (Exception e){
            return new ResponseEntity<GenericResponse>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/update")
    public ResponseEntity<GenericResponse> update(@RequestBody ProductBrandDto productBrandDto) {
        try {
            productBrandService.update(productBrandDto);
            return new ResponseEntity<GenericResponse>(responseService.buildInformation("Updated."), HttpStatus.OK);
        } catch (ProductBrandNotFoundException | ProductBrandExistsException e) {
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.CONFLICT);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<GenericResponse>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/deactivate")
    public ResponseEntity<GenericResponse> deactivate(@RequestBody ProductBrandDto productBrandDto){
        try{
            productBrandService.deactivate(productBrandDto);
            return new ResponseEntity(responseService.buildInformation("Deactivated."), HttpStatus.OK);
        } catch (ProductBrandNotFoundException e) {
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<GenericResponse>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search")
    public ResponseEntity getByName(@RequestParam(required = false) String name){
        try{
            return new ResponseEntity(productBrandService.search(name), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<GenericResponse>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/get/{id}")
    public ResponseEntity get(@PathVariable("id") Long id){
        try{
            return new ResponseEntity(productBrandService.get(id), HttpStatus.OK);
        } catch (ProductBrandNotFoundException e) {
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<GenericResponse>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
