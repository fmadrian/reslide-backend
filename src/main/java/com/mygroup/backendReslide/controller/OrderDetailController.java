package com.mygroup.backendReslide.controller;

import com.mygroup.backendReslide.dto.request.OrderDetailRequest;
import com.mygroup.backendReslide.dto.response.GenericResponse;
import com.mygroup.backendReslide.exceptions.*;
import com.mygroup.backendReslide.exceptions.notFound.OrderDetailNotFoundException;
import com.mygroup.backendReslide.exceptions.notFound.OrderNotFoundException;
import com.mygroup.backendReslide.exceptions.notFound.ProductNotFoundException;
import com.mygroup.backendReslide.service.GenericResponseService;
import com.mygroup.backendReslide.service.OrderDetailService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order/detail")
@AllArgsConstructor
public class OrderDetailController {
    private final GenericResponseService responseService;
    private final OrderDetailService orderDetailService;
    @PostMapping("/create")
    public ResponseEntity create(@RequestBody OrderDetailRequest orderDetailRequest){
        try{
            orderDetailService.create(orderDetailRequest);
            return new ResponseEntity<GenericResponse>(responseService.buildInformation("Created."), HttpStatus.CREATED);
        }catch (ProductNotFoundException | OrderNotFoundException | OrderProductQuantityException |
                PaymentQuantityException | PaymentExceedsDebtException e){
            return new ResponseEntity(responseService.buildError(e), HttpStatus.CONFLICT);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(responseService.buildError(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/update/status")
    public ResponseEntity updateStatus(@RequestBody OrderDetailRequest orderDetailRequest){
        try{
            orderDetailService.updateStatus(orderDetailRequest);
            return new ResponseEntity<GenericResponse>(responseService.buildInformation("Updated."), HttpStatus.OK);
        }catch (OrderDetailNotFoundException | OrderNotFoundException | ProductNotFoundException |
                OrderAndDetailDoNotMatchException | OrderDetailStatusException e){
            return new ResponseEntity(responseService.buildError(e), HttpStatus.CONFLICT);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(responseService.buildError(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/update/quantity")
    public ResponseEntity updateQuantity(@RequestBody OrderDetailRequest orderDetailRequest){
        try{
            orderDetailService.updateQuantity(orderDetailRequest);
            return new ResponseEntity<GenericResponse>(responseService.buildInformation("Updated."), HttpStatus.OK);
        }catch (OrderDetailNotFoundException | OrderNotFoundException | ProductNotFoundException |
                OrderAndDetailDoNotMatchException e){
            return new ResponseEntity(responseService.buildError(e), HttpStatus.CONFLICT);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(responseService.buildError(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/delete")
    public ResponseEntity delete(@RequestBody OrderDetailRequest orderDetailRequest){
        try{
            orderDetailService.delete(orderDetailRequest);
            return new ResponseEntity<GenericResponse>(responseService.buildInformation("Deleted."), HttpStatus.OK);
        }catch (OrderDetailNotFoundException | OrderNotFoundException | ProductNotFoundException |
                OrderDetailDeleteException | PaymentQuantityException | PaymentExceedsDebtException e){
            return new ResponseEntity(responseService.buildError(e), HttpStatus.CONFLICT);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(responseService.buildError(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}