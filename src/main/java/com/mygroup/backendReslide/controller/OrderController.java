package com.mygroup.backendReslide.controller;

import com.mygroup.backendReslide.dto.request.OrderRequest;
import com.mygroup.backendReslide.dto.response.GenericResponse;
import com.mygroup.backendReslide.exceptions.DiscountNotValidException;
import com.mygroup.backendReslide.exceptions.PaymentExceedsDebtException;
import com.mygroup.backendReslide.exceptions.PaymentQuantityException;
import com.mygroup.backendReslide.exceptions.ProductQuantityException;
import com.mygroup.backendReslide.exceptions.notFound.OrderNotFoundException;
import com.mygroup.backendReslide.exceptions.notFound.ProductNotFoundException;
import com.mygroup.backendReslide.service.GenericResponseService;
import com.mygroup.backendReslide.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

@RestController
@RequestMapping("/api/order")
@AllArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final GenericResponseService responseService;
    @PostMapping("/create")
    public ResponseEntity create(@RequestBody OrderRequest orderRequest){
        try{
            return new ResponseEntity(orderService.create(orderRequest), HttpStatus.CREATED);
        }catch (ProductNotFoundException | ProductQuantityException | DiscountNotValidException
                | PaymentQuantityException | PaymentExceedsDebtException e){
            return new ResponseEntity(responseService.buildError(e), HttpStatus.CONFLICT);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(responseService.buildError(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/update")
    public ResponseEntity update(@RequestBody OrderRequest orderRequest){
        try{
            orderService.update(orderRequest);
            return new ResponseEntity<GenericResponse>(responseService.buildInformation("Updated."), HttpStatus.OK);
        }catch (OrderNotFoundException e){
            return new ResponseEntity(responseService.buildError(e), HttpStatus.CONFLICT);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(responseService.buildError(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/search/provider")
    public ResponseEntity searchByProvider(@RequestParam String start, @RequestParam String end, @RequestParam String providerCode){
        try{
            return new ResponseEntity(orderService.searchByProvider(start, end, providerCode), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(responseService.buildError(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/search")
    public ResponseEntity search(@RequestParam String start_date, @RequestParam String end_date,
                                  @RequestParam(required = false) String start_expected_delivery_date, @RequestParam(required = false) String end_expected_delivery_date,
                                  @RequestParam(required = false) String start_actual_delivery_date, @RequestParam(required = false) String end_actual_delivery_date,
                                  @RequestParam String providerCode){
        try{
            return new ResponseEntity(orderService.search(start_date, end_date,start_expected_delivery_date,
                    end_expected_delivery_date,start_actual_delivery_date,end_actual_delivery_date,providerCode), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(responseService.buildError(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/get/{id}")
    public ResponseEntity get(@PathVariable Long id){
        try{
            return new ResponseEntity(orderService.get(id), HttpStatus.OK);
        }catch (OrderNotFoundException e){
            return new ResponseEntity(responseService.buildError(e), HttpStatus.CONFLICT);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(responseService.buildError(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/deliver")
    public ResponseEntity deliverAllProducts(@RequestBody OrderRequest orderRequest){
        try{
            orderService.deliverAllProducts(orderRequest);
            return new ResponseEntity(responseService.buildInformation("All products have been delivered."), HttpStatus.OK);
        }catch (OrderNotFoundException e){
            return new ResponseEntity(responseService.buildError(e), HttpStatus.CONFLICT);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(responseService.buildError(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/update/status")
    public ResponseEntity switchStatus(@RequestBody OrderRequest orderRequest){
        try{
            orderService.switchStatus(orderRequest);
            return new ResponseEntity(responseService.buildInformation("Order status has been changed."), HttpStatus.OK);
        }catch (OrderNotFoundException e){
            return new ResponseEntity(responseService.buildError(e), HttpStatus.CONFLICT);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(responseService.buildError(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/search/after-estimated-delivery-date")
    public ResponseEntity searchAfterEstimatedDeliveryDate(@RequestParam()String estimatedDeliveryDate ){
        try{
            return new ResponseEntity(orderService.searchAfterEstimatedDeliveryDate(estimatedDeliveryDate), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(responseService.buildError(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
