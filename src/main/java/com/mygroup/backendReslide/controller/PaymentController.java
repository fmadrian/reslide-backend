package com.mygroup.backendReslide.controller;

import com.mygroup.backendReslide.dto.PaymentDto;
import com.mygroup.backendReslide.dto.PaymentMethodDto;
import com.mygroup.backendReslide.dto.response.GenericResponse;
import com.mygroup.backendReslide.exceptions.InternalError;
import com.mygroup.backendReslide.exceptions.PaymentExceedsDebtException;
import com.mygroup.backendReslide.exceptions.PaymentQuantityException;
import com.mygroup.backendReslide.exceptions.TransactionDoesNotMatchException;
import com.mygroup.backendReslide.exceptions.alreadyExists.PaymentMethodExistsException;
import com.mygroup.backendReslide.exceptions.notFound.PaymentMethodNotFoundException;
import com.mygroup.backendReslide.exceptions.notFound.PaymentNotFoundException;
import com.mygroup.backendReslide.exceptions.notFound.TransactionNotFoundException;
import com.mygroup.backendReslide.service.GenericResponseService;
import com.mygroup.backendReslide.service.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@AllArgsConstructor
public class PaymentController {
    private final GenericResponseService responseService;
    private final PaymentService paymentService;
    @PostMapping("/create")
    public ResponseEntity<GenericResponse> create(@RequestBody PaymentDto paymentRequest){
        try{
            paymentService.create(paymentRequest);
            return new ResponseEntity<GenericResponse>(responseService.buildInformation("Created."), HttpStatus.CREATED);
        }catch (TransactionNotFoundException | PaymentExceedsDebtException | TransactionDoesNotMatchException e){
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.CONFLICT);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<GenericResponse>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/update")
    public ResponseEntity<GenericResponse> update(@RequestBody PaymentDto paymentRequest){
        try{
            paymentService.update(paymentRequest);
            return new ResponseEntity<GenericResponse>(responseService.buildInformation("Updated."), HttpStatus.OK);
        }catch (PaymentNotFoundException | PaymentExceedsDebtException |
                TransactionDoesNotMatchException | TransactionNotFoundException | PaymentQuantityException e ){
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.CONFLICT);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<GenericResponse>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/overturn")
    public ResponseEntity<GenericResponse> overturn(@RequestBody PaymentDto paymentRequest){
        try{
            paymentService.overturn(paymentRequest);
            return new ResponseEntity<GenericResponse>(responseService.buildInformation("Updated."), HttpStatus.OK);
        }catch (TransactionDoesNotMatchException | TransactionNotFoundException e ){
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.CONFLICT);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<GenericResponse>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
