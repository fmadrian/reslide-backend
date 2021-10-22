package com.mygroup.backendReslide.controller;

import com.mygroup.backendReslide.dto.request.InvoiceRequest;
import com.mygroup.backendReslide.exceptions.DiscountNotValidException;
import com.mygroup.backendReslide.exceptions.PaymentExceedsDebtException;
import com.mygroup.backendReslide.exceptions.PaymentQuantityException;
import com.mygroup.backendReslide.exceptions.ProductQuantityException;
import com.mygroup.backendReslide.exceptions.notFound.InvoiceNotFoundException;
import com.mygroup.backendReslide.exceptions.notFound.ProductNotFoundException;
import com.mygroup.backendReslide.model.Invoice;
import com.mygroup.backendReslide.service.GenericResponseService;
import com.mygroup.backendReslide.service.InvoiceService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/invoice")
@AllArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final GenericResponseService responseService;
    @PostMapping("/create")
    public ResponseEntity create(@RequestBody InvoiceRequest invoiceRequest){
        try{
            return new ResponseEntity(invoiceService.create(invoiceRequest), HttpStatus.OK);
        }catch (ProductNotFoundException | ProductQuantityException | DiscountNotValidException
        | PaymentQuantityException | PaymentExceedsDebtException e){
            return new ResponseEntity(responseService.buildError(e), HttpStatus.CONFLICT);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(responseService.buildError(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/update")
    public ResponseEntity update(@RequestBody InvoiceRequest invoiceRequest){
        try{
            invoiceService.update(invoiceRequest);
            return new ResponseEntity(responseService.buildInformation("Updated"), HttpStatus.OK);
        }catch (InvoiceNotFoundException e){
            return new ResponseEntity(responseService.buildError(e), HttpStatus.CONFLICT);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(responseService.buildError(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/search")
    public ResponseEntity search(@RequestParam String start, @RequestParam String end){
        try{
            return new ResponseEntity(invoiceService.search(start, end), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(responseService.buildError(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/search/client")
    public ResponseEntity searchByClient(@RequestParam String start, @RequestParam String end, @RequestParam String clientCode){
        try{
            return new ResponseEntity(invoiceService.searchByClient(start, end, clientCode), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(responseService.buildError(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/get/{id}")
    public ResponseEntity get(@PathVariable Long id){
        try{
            return new ResponseEntity(invoiceService.get(id), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(responseService.buildError(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/update/status")
    public ResponseEntity switchStatus(@RequestBody InvoiceRequest invoiceRequest){
        try{
            invoiceService.switchStatus(invoiceRequest);
            return new ResponseEntity(responseService.buildInformation("Status has been changed."), HttpStatus.OK);
        }catch (InvoiceNotFoundException e){
            return new ResponseEntity(responseService.buildError(e), HttpStatus.CONFLICT);
        }
    }
}
