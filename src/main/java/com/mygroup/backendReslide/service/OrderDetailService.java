package com.mygroup.backendReslide.service;

import com.mygroup.backendReslide.dto.request.OrderDetailRequest;
import com.mygroup.backendReslide.exceptions.OrderDetailStatusException;
import com.mygroup.backendReslide.exceptions.OrderProductQuantityException;
import com.mygroup.backendReslide.exceptions.notFound.OrderDetailNotFoundException;
import com.mygroup.backendReslide.mapper.OrderDetailMapper;
import com.mygroup.backendReslide.model.OrderDetail;
import com.mygroup.backendReslide.model.Product;
import com.mygroup.backendReslide.model.status.OrderDetailStatus;
import com.mygroup.backendReslide.repository.OrderDetailRepository;
import com.mygroup.backendReslide.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderDetailService {
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final OrderDetailMapper orderDetailMapper;
    @Transactional
    public List<OrderDetail> validateOrderDetails(List<OrderDetail> details){
        return details.stream()
                .map(this::validateOrderDetail)
                .collect(Collectors.toList());
    }
    private OrderDetail validateOrderDetail(OrderDetail detail){
        if(detail.getQuantity().compareTo(BigDecimal.ZERO) != 1){
            throw new OrderProductQuantityException(detail.getQuantity());
        }
        detail.setTotal(detail.getPriceByUnit().multiply(detail.getQuantity()));
        detail.setStatus(OrderDetailStatus.NOT_DELIVERED);
        return detail;
    }
    @Transactional
    public void update(OrderDetailRequest orderDetailRequest){
        OrderDetail newDetail = orderDetailMapper.mapToEntity(orderDetailRequest);
        // Search the order detail.
        Long id = newDetail.getId();
        OrderDetail currentDetail = orderDetailRepository.findById(id)
                .orElseThrow(()-> new OrderDetailNotFoundException(id));

        currentDetail.setNotes(newDetail.getNotes());
        if(currentDetail.getStatus().equals(OrderDetailStatus.NOT_DELIVERED) ||
                currentDetail.getStatus().equals(OrderDetailStatus.RETURNED)) {
            currentDetail.setQuantity(newDetail.getQuantity());
            currentDetail = validateOrderDetail(currentDetail);
        }else{
            // Calculates the total for the new detail and then, adds them up.
            newDetail.setPriceByUnit(currentDetail.getPriceByUnit()); // Uses the old price.
            newDetail = validateOrderDetail(newDetail);
            Product product = currentDetail.getProduct();
            // Calculates the difference to add it or subtract it from the total.
            BigDecimal difference = currentDetail.getQuantity().subtract(newDetail.getQuantity());
            currentDetail.setQuantity(currentDetail.getQuantity().subtract(difference));
            currentDetail.setTotal(currentDetail.getTotal().subtract(difference));
            // Updates the quantity
            product.setQuantityAvailable(product.getQuantityAvailable().subtract(difference));
            productRepository.save(product);
        }
        orderDetailRepository.save(currentDetail);
    }

    @Transactional
    public void updateStatus(OrderDetailRequest orderDetailRequest){
        OrderDetail newDetail = orderDetailMapper.mapToEntity(orderDetailRequest);
        // Search the order detail.
        Long id = newDetail.getId();
        OrderDetail currentDetail = orderDetailRepository.findById(id)
                .orElseThrow(()-> new OrderDetailNotFoundException(id));

        OrderDetailStatus currentStatus = currentDetail.getStatus();
        OrderDetailStatus newStatus = newDetail.getStatus();
        // Updates the detail and saves the quantity
        Product product = newDetail.getProduct();
        if(currentStatus.equals(OrderDetailStatus.NOT_DELIVERED) || currentStatus.equals(OrderDetailStatus.RETURNED)){
            // Cancel or deliver
            if(newStatus.equals(OrderDetailStatus.DELIVERED)){
                product.setQuantityAvailable(product.getQuantityAvailable().add(newDetail.getQuantity()));
                productRepository.save(product);
            }else{
                throw new OrderDetailStatusException(id);
            }
        }
        else if(currentStatus.equals(OrderDetailStatus.DELIVERED)){
            // Return
            if(newStatus.equals(OrderDetailStatus.RETURNED) || newStatus.equals(OrderDetailStatus.CANCELED)){
                product.setQuantityAvailable(product.getQuantityAvailable().subtract(newDetail.getQuantity()));
                productRepository.save(product);
            }else{
                throw new OrderDetailStatusException(id);
            }
        }
        else if(currentStatus.equals(OrderDetailStatus.CANCELED)){
            // No more changes are allowed.
            throw new OrderDetailStatusException(id);
        }
        currentDetail.setStatus(newStatus);
        orderDetailRepository.save(currentDetail);
    }

}
