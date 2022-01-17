package com.mygroup.backendReslide.controller;

import com.mygroup.backendReslide.dto.ProductDto;
import com.mygroup.backendReslide.dto.response.GenericResponse;
import com.mygroup.backendReslide.exceptions.InternalError;
import com.mygroup.backendReslide.exceptions.PriceNotValidException;
import com.mygroup.backendReslide.exceptions.QuantityNotValidException;
import com.mygroup.backendReslide.exceptions.alreadyExists.ProductBrandExistsException;
import com.mygroup.backendReslide.exceptions.alreadyExists.ProductExistsException;
import com.mygroup.backendReslide.exceptions.notFound.MeasurementTypeNotFoundException;
import com.mygroup.backendReslide.exceptions.notFound.ProductBrandNotFoundException;
import com.mygroup.backendReslide.exceptions.notFound.ProductNotFoundException;
import com.mygroup.backendReslide.exceptions.notFound.ProductTypeNotFoundException;
import com.mygroup.backendReslide.mapper.ProductMapper;
import com.mygroup.backendReslide.service.GenericResponseService;
import com.mygroup.backendReslide.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/product")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final GenericResponseService responseService;
    @PostMapping("/create")
    public ResponseEntity<GenericResponse> create(@RequestBody ProductDto productRequest){
        try{
            return new ResponseEntity(productService.create(productRequest), HttpStatus.CREATED);
        }catch (ProductExistsException | ProductBrandNotFoundException | ProductTypeNotFoundException
                | PriceNotValidException | QuantityNotValidException | MeasurementTypeNotFoundException e){
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.CONFLICT);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<GenericResponse>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/update")
    public ResponseEntity<GenericResponse> update(@RequestBody ProductDto productRequest){
        try{
            productService.update(productRequest);
            return new ResponseEntity<GenericResponse>(responseService.buildInformation("Updated."), HttpStatus.CREATED);
        }catch (ProductExistsException | PriceNotValidException | QuantityNotValidException |
                ProductNotFoundException | ProductBrandNotFoundException | ProductTypeNotFoundException
                | MeasurementTypeNotFoundException e){
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.CONFLICT);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<GenericResponse>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search")
    public ResponseEntity search(@RequestParam(required = false) String name, @RequestParam(required = false) String code,
                                @RequestParam(required = false) String brand, @RequestParam(required = false) String type,
                                 @RequestParam(required = false) String status){
        try{
            return new ResponseEntity<List<ProductDto>>(productService.search(name,code,brand,type,status),HttpStatus.OK);
        }catch (ProductBrandNotFoundException | ProductTypeNotFoundException e){
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.CONFLICT);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<GenericResponse>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/get")
    public ResponseEntity get(@RequestParam(required = false) Long id){
        try{
            return new ResponseEntity<ProductDto>(productService.getProduct(id),HttpStatus.OK);
        }catch (ProductNotFoundException e ){
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.CONFLICT);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<GenericResponse>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/search-less-or-equal")
    public ResponseEntity searchLessOrEqual(@RequestParam BigDecimal quantity){
        try{
            return new ResponseEntity<List<ProductDto>>(productService.searchLessOrEqual(quantity),HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<GenericResponse>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/search-all")
    public ResponseEntity searchAll(){
        try{
            return new ResponseEntity<List<ProductDto>>(productService.searchAll(),HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<GenericResponse>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
