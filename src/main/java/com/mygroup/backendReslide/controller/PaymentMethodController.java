package com.mygroup.backendReslide.controller;

import com.mygroup.backendReslide.dto.PaymentMethodDto;
import com.mygroup.backendReslide.dto.response.GenericResponse;
import com.mygroup.backendReslide.exceptions.InternalError;
import com.mygroup.backendReslide.exceptions.notFound.PaymentMethodNotFoundException;
import com.mygroup.backendReslide.exceptions.alreadyExists.PaymentMethodExistsException;
import com.mygroup.backendReslide.service.GenericResponseService;
import com.mygroup.backendReslide.service.PaymentMethodService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment/method")
@AllArgsConstructor
public class PaymentMethodController {
    private final PaymentMethodService paymentMethodService;
    private final GenericResponseService responseService;

    @PostMapping("/create")
    public ResponseEntity create(@RequestBody PaymentMethodDto paymentMethodRequest){
        try{
            return new ResponseEntity(paymentMethodService.create(paymentMethodRequest), HttpStatus.CREATED);
        }catch (PaymentMethodExistsException e){
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.CONFLICT);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<GenericResponse>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/update")
    public ResponseEntity<GenericResponse> update(@RequestBody PaymentMethodDto paymentMethodRequest){
        try{
            paymentMethodService.update(paymentMethodRequest);
            return new ResponseEntity<GenericResponse>(responseService.buildInformation("Updated."), HttpStatus.OK);
        }catch (PaymentMethodExistsException | PaymentMethodNotFoundException e){
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.CONFLICT);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<GenericResponse>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/search")
    public ResponseEntity search(@RequestParam(required = false) String type){
        try{
            return new ResponseEntity<List<PaymentMethodDto>>(paymentMethodService.search(type), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<GenericResponse>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/get/{id}")
    public ResponseEntity get(@PathVariable(name = "id") Long id){
        try{
            return new ResponseEntity(paymentMethodService.get(id), HttpStatus.OK);
        }catch (PaymentMethodNotFoundException e){
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.NOT_FOUND);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<GenericResponse>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
