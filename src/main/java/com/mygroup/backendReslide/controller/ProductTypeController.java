package com.mygroup.backendReslide.controller;

import com.mygroup.backendReslide.dto.request.ProductTypeRequest;
import com.mygroup.backendReslide.dto.response.ErrorResponse;
import com.mygroup.backendReslide.exceptions.InternalError;
import com.mygroup.backendReslide.exceptions.alreadyExists.ProductTypeExistsException;
import com.mygroup.backendReslide.service.ErrorService;
import com.mygroup.backendReslide.service.ProductTypeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/product/type")
@AllArgsConstructor
public class ProductTypeController {

    private final ProductTypeService productTypeService;
    private final ErrorService errorService;

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody ProductTypeRequest productTypeRequest){
        try{
            productTypeService.register(productTypeRequest);
            return new ResponseEntity("Product type registered", HttpStatus.OK);
        }catch (ProductTypeExistsException e){
            return new ResponseEntity<ErrorResponse>(errorService.buildError(e), HttpStatus.UNAUTHORIZED);
        }catch (Exception e){
            return new ResponseEntity<ErrorResponse>(errorService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
