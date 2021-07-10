package com.mygroup.backendReslide.service;

import com.mygroup.backendReslide.dto.request.OrderDetailRequest;
import com.mygroup.backendReslide.exceptions.OrderAndDetailDoNotMatchException;
import com.mygroup.backendReslide.exceptions.OrderDetailDeleteException;
import com.mygroup.backendReslide.exceptions.OrderDetailStatusException;
import com.mygroup.backendReslide.exceptions.OrderProductQuantityException;
import com.mygroup.backendReslide.exceptions.notFound.OrderDetailNotFoundException;
import com.mygroup.backendReslide.exceptions.notFound.OrderNotFoundException;
import com.mygroup.backendReslide.mapper.OrderDetailMapper;
import com.mygroup.backendReslide.model.Order;
import com.mygroup.backendReslide.model.OrderDetail;
import com.mygroup.backendReslide.model.Product;
import com.mygroup.backendReslide.model.status.OrderDetailStatus;
import com.mygroup.backendReslide.model.status.PaymentStatus;
import com.mygroup.backendReslide.repository.OrderDetailRepository;
import com.mygroup.backendReslide.repository.OrderRepository;
import com.mygroup.backendReslide.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderDetailService {
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final OrderDetailMapper orderDetailMapper;
    private final OrderRepository orderRepository;
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
    public void updateQuantity(OrderDetailRequest orderDetailRequest){
        OrderDetail newDetail = orderDetailMapper.mapToEntity(orderDetailRequest);
        // Search the order detail that will be updated.
        OrderDetail currentDetail = orderDetailRepository.findById(orderDetailRequest.getId())
                .orElseThrow(()-> new OrderDetailNotFoundException(orderDetailRequest.getId()));
        // Search the order.
        Order order = orderRepository.findById(orderDetailRequest.getOrderId())
                .orElseThrow(()-> new OrderNotFoundException(orderDetailRequest.getOrderId()));
        // Check whether the detail corresponds to the order.
        if(!order.getDetails().contains(currentDetail)){
            throw new OrderAndDetailDoNotMatchException(orderDetailRequest.getOrderId(), orderDetailRequest.getId());
        }
        // Update the detail.
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
        recalculateOrder(order);
    }

    @Transactional
    public void updateStatus(OrderDetailRequest orderDetailRequest){

        // Search the order detail that will be updated.
        OrderDetail currentDetail = orderDetailRepository.findById(orderDetailRequest.getId())
                .orElseThrow(()-> new OrderDetailNotFoundException(orderDetailRequest.getId()));
        // Search the order.
        Order order = orderRepository.findById(orderDetailRequest.getOrderId())
                .orElseThrow(()-> new OrderNotFoundException(orderDetailRequest.getOrderId()));
        // Check whether the detail corresponds to the order.
        if(!order.getDetails().contains(currentDetail)){
            throw new OrderAndDetailDoNotMatchException(orderDetailRequest.getOrderId(), orderDetailRequest.getId());
        }

        OrderDetailStatus currentStatus = currentDetail.getStatus();
        OrderDetailStatus newStatus = OrderDetailStatus.valueOf(orderDetailRequest.getStatus().toUpperCase(Locale.ROOT));
        // Updates the detail and saves the quantity
        Product product = currentDetail.getProduct();
        if(currentStatus.equals(OrderDetailStatus.NOT_DELIVERED) || currentStatus.equals(OrderDetailStatus.RETURNED)){
            // Adds product to stock
            if(newStatus.equals(OrderDetailStatus.DELIVERED)){
                product.setQuantityAvailable(product.getQuantityAvailable().add(currentDetail.getQuantity()));
                productRepository.save(product);
            }else{
                throw new OrderDetailStatusException(currentDetail.getId());
            }
        }
         else if(currentStatus.equals(OrderDetailStatus.DELIVERED)){
            // Returns product from stock
            if(newStatus.equals(OrderDetailStatus.RETURNED)){
                product.setQuantityAvailable(product.getQuantityAvailable().subtract(currentDetail.getQuantity()));
                productRepository.save(product);
            }else{
                throw new OrderDetailStatusException(currentDetail.getId());
            }
        }
        currentDetail.setStatus(newStatus);
        orderDetailRepository.save(currentDetail);
    }
    @Transactional
    public void create(OrderDetailRequest orderDetailRequest){
        // Validates the order detail.
        OrderDetail orderDetail = validateOrderDetail(orderDetailMapper.mapToEntity(orderDetailRequest));
        // Searches the order.
        Order order = orderRepository.findById(orderDetailRequest.getOrderId())
                .orElseThrow(()->new OrderNotFoundException(orderDetailRequest.getOrderId()));
        // Adds the detail to the order
        order.getDetails().add(orderDetail);
        orderDetailRepository.save(orderDetail);
        recalculateOrder(order);
    }

    @Transactional
    public void delete(OrderDetailRequest orderDetailRequest){
        // Search the order detail that will be updated.
        OrderDetail orderDetail = orderDetailRepository.findById(orderDetailRequest.getId())
                .orElseThrow(()-> new OrderDetailNotFoundException(orderDetailRequest.getId()));
        // Search the order.
        Order order = orderRepository.findById(orderDetailRequest.getOrderId())
                .orElseThrow(()-> new OrderNotFoundException(orderDetailRequest.getOrderId()));
        // Check whether the detail corresponds to the order.
        if(!order.getDetails().contains(orderDetail)){
            throw new OrderAndDetailDoNotMatchException(orderDetailRequest.getOrderId(), orderDetailRequest.getId());
        }
        // Don't delete it the item has been delivered.
        if(orderDetail.getStatus().equals(OrderDetailStatus.DELIVERED)){
            throw new OrderDetailDeleteException(orderDetailRequest.getOrderId());
        }else{
            order.getDetails().remove(orderDetail);
            orderDetailRepository.delete(orderDetail);
            // Recalculate order.
            recalculateOrder(order);
        }
    }

    @Transactional
    private void recalculateOrder(Order order){
        // Calculate amount owed.
        BigDecimal total = order.getDetails().stream()
                .map(orderDetail -> orderDetail.getTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // Calculate amount paid.
        BigDecimal paid = order.getTransaction().getPayments().stream()
                .filter(payment -> payment.getStatus().equals(PaymentStatus.ACTIVE))
                .map(payment -> payment.getPaid())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // Set the amounts.
        order.setTotal(total);
        order.setPaid(paid);
        order.setOwed(total.subtract(paid));
        orderRepository.save(order);
    }
}
