package com.mygroup.backendReslide.controller;

import com.mygroup.backendReslide.dto.request.OrderRequest;
import com.mygroup.backendReslide.exceptions.DiscountNotValidException;
import com.mygroup.backendReslide.exceptions.PaymentExceedsDebtException;
import com.mygroup.backendReslide.exceptions.PaymentQuantityException;
import com.mygroup.backendReslide.exceptions.ProductQuantityException;
import com.mygroup.backendReslide.exceptions.notFound.ProductNotFoundException;
import com.mygroup.backendReslide.service.GenericResponseService;
import com.mygroup.backendReslide.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@AllArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final GenericResponseService responseService;
    @PostMapping("/create")
    public ResponseEntity create(@RequestBody OrderRequest orderRequest){
        try{
            orderService.create(orderRequest);
            return new ResponseEntity("Created", HttpStatus.OK);
        }catch (ProductNotFoundException | ProductQuantityException | DiscountNotValidException
                | PaymentQuantityException | PaymentExceedsDebtException e){
            return new ResponseEntity(responseService.buildError(e), HttpStatus.CONFLICT);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(responseService.buildError(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/search")
    public ResponseEntity search(@RequestParam String start, @RequestParam String end){
        try{
            return new ResponseEntity(orderService.search(start, end), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(responseService.buildError(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/search/client")
    public ResponseEntity searchByClient(@RequestParam String start, @RequestParam String end, @RequestParam String clientCode){
        try{
            return new ResponseEntity(orderService.searchByClient(start, end, clientCode), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(responseService.buildError(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/get/{id}")
    public ResponseEntity get(@PathVariable Long id){
        try{
            return new ResponseEntity(orderService.get(id), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(responseService.buildError(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
