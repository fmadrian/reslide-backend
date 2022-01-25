package com.mygroup.backendReslide.service;

import com.mygroup.backendReslide.dto.request.OrderDetailRequest;
import com.mygroup.backendReslide.dto.request.OrderRequest;
import com.mygroup.backendReslide.dto.response.OrderResponse;
import com.mygroup.backendReslide.exceptions.OrderDetailStatusException;
import com.mygroup.backendReslide.exceptions.notFound.OrderNotFoundException;
import com.mygroup.backendReslide.mapper.OrderMapper;
import com.mygroup.backendReslide.model.Order;
import com.mygroup.backendReslide.model.OrderDetail;
import com.mygroup.backendReslide.model.Payment;
import com.mygroup.backendReslide.model.status.OrderDetailStatus;
import com.mygroup.backendReslide.model.status.OrderStatus;
import com.mygroup.backendReslide.repository.OrderDetailRepository;
import com.mygroup.backendReslide.repository.OrderRepository;
import com.mygroup.backendReslide.repository.PaymentRepository;
import com.mygroup.backendReslide.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final TransactionRepository transactionRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final OrderMapper orderMapper;
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final OrderDetailService orderDetailService;
    private final AuthService authService;

    @Transactional
    public OrderResponse create(OrderRequest orderRequest) {
        // Map the order.
        // Set the users.
        Order order = orderMapper.mapToEntity(orderRequest);
        order.getTransaction().setUser(authService.getCurrentUser());
        List<OrderDetail> details = order.getDetails();
        List<Payment> payments = order.getTransaction().getPayments();
        // Validate the order details.
        details = orderDetailService.validateOrderDetails(details);
        // Get the total.
        BigDecimal total = details.stream()
                .map(orderDetail -> orderDetail.getTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotal(total);

        // Setting initial values.
        order.setPaid(BigDecimal.ZERO);
        order.setOwed(total);
        order.setStatus(OrderStatus.ACTIVE);

        // Validate payments.
        payments = paymentService.validateOrderPayments(payments, order);
        // Save the payment.
        if(!payments.isEmpty()) {
            paymentRepository.saveAll(payments);
        }
        // Get paid and owed amounts and set them.
        BigDecimal paid = payments.stream()
                            .map(payment -> payment.getPaid())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal owed = total.subtract(paid);
        order.setPaid(paid);
        order.setOwed(owed);
        // Save the transaction
        transactionRepository.save(order.getTransaction());
        // Save the order details.
        orderDetailRepository.saveAll(details);
        // Save the order.
        order = orderRepository.save(order);
        return this.orderMapper.mapToDto(order);
    }
    // Updates order without updating order details.
    public void update(OrderRequest orderRequest){
        // Maps the order that will have the modifications.
        Order modifiedOrder = orderMapper.mapToEntity(orderRequest);
        // Find the order that will be modified.
        Order order = orderRepository.findById(orderRequest.getId())
                .orElseThrow(()->new OrderNotFoundException(orderRequest.getId()));
        // Change the transaction and order details.
        order.getTransaction().setDate(modifiedOrder.getTransaction().getDate());
        order.getTransaction().setNotes(modifiedOrder.getTransaction().getNotes());
        order.getTransaction().setUser(authService.getCurrentUser()); // Last user who manipulated the order.
        order.setActualDeliveryDate(modifiedOrder.getActualDeliveryDate());
        order.setExpectedDeliveryDate(modifiedOrder.getExpectedDeliveryDate());
        order.setStatus(modifiedOrder.getStatus());
        order.setProvider(modifiedOrder.getProvider());

        // Save the changes.
        transactionRepository.save(order.getTransaction());
        orderRepository.save(order);
    }
    @Transactional(readOnly = true)
    public List<OrderResponse> searchByProvider(String start, String end, String providerCode) {
        return orderRepository.findByDateAndProviderCode(Instant.parse(start), Instant.parse(end),providerCode)
                .stream()
                .map(orderMapper :: mapToDto)
                .map(this :: hideOrderDetails) // Hide the invoice details.
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public OrderResponse get(Long id) {
        return orderMapper.mapToDto(orderRepository.findById(id)
                .orElseThrow(()-> new OrderNotFoundException(id)));
    }
    @Transactional
    public void switchStatus(OrderRequest orderRequest){
        // Find the order that will be modified.
        Order order = orderRepository.findById(orderRequest.getId())
                .orElseThrow(()->new OrderNotFoundException(orderRequest.getId()));
        // Change the status
        if(order.getStatus().equals(OrderStatus.ACTIVE)){
            order.setStatus(OrderStatus.DELETED);
        }else{
            order.setStatus(OrderStatus.ACTIVE);
        }
        orderRepository.save(order);
    }
    // Delivers all the details in 1 order.
    @Transactional
    public void deliverAllProducts(OrderRequest orderRequest) {
        // Search order
        Order order = orderRepository.findById(orderRequest.getId())
                .orElseThrow(() -> new OrderNotFoundException(orderRequest.getId()));
        // Deliver all the undelivered products
        // Get rid of the delivered and returned lines.
        order.getDetails().stream()
                .filter(orderDetail -> !orderDetail.getStatus().equals(OrderDetailStatus.DELIVERED))
                .forEach(orderDetail -> {
                    try {
                        // Save the changes.
                        orderDetailService.updateStatus(orderDetail, OrderDetailStatus.DELIVERED);
                    } catch (OrderDetailStatusException e) {
                        // Don't do anything if there is an exception.
                    }
                });
    }
    private OrderResponse hideOrderDetails(OrderResponse orderResponse){
        orderResponse.setDetails(null);
        return orderResponse;
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> search(String start_date, String end_date,
                          String start_expected_delivery_date, String end_expected_delivery_date,
                          String start_actual_delivery_date, String end_actual_delivery_date,
                          String providerCode) {

        List<Order> orders = null;
        if(start_expected_delivery_date != null && end_expected_delivery_date != null){
            if(start_actual_delivery_date != null && end_actual_delivery_date != null){
                // 1. Search with all the parameters.
                orders = orderRepository.findOrderByDateExpectedDateActualDateProviderCode(
                        Instant.parse(start_date),Instant.parse(end_date),
                        Instant.parse(start_expected_delivery_date),Instant.parse(end_expected_delivery_date),
                        Instant.parse(start_actual_delivery_date),Instant.parse(end_actual_delivery_date),providerCode
                );
            }else{
                // 2. Search with code + date + expected delivery date + provider
                orders = orderRepository.findOrderByDateExpectedDateProviderCode(
                        Instant.parse(start_date),Instant.parse(end_date),
                        Instant.parse(start_expected_delivery_date),Instant.parse(end_expected_delivery_date),
                        providerCode
                );
            }
        }else if(start_actual_delivery_date != null && end_actual_delivery_date != null){
            // 3. Search with code + date + actual delivery date + provider
            orders = orderRepository.findOrderByDateActualDateProviderCode(
                    Instant.parse(start_date),Instant.parse(end_date),
                    Instant.parse(start_actual_delivery_date),Instant.parse(end_actual_delivery_date)
                    ,providerCode
            );
        }else{
            // 4. Search by date and provider
            orders = orderRepository.findByDateAndProviderCode(
                    Instant.parse(start_date),Instant.parse(end_date),providerCode
            );
        }
        // Return the orders.
        return orders.stream()
                .map(orderMapper::mapToDto)
                .map(this::hideOrderDetails)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public List<OrderResponse> searchAfterEstimatedDeliveryDate(String estimatedDeliveryDate) {
        return orderRepository.
                findByExpectedDeliveryDateGreaterThanEqualAndStatus(Instant.parse(estimatedDeliveryDate), OrderStatus.ACTIVE)
                .stream()
                .map(orderMapper::mapToDto)
                .map(this::hideOrderDetails)
                .collect(Collectors.toList());
    }
}
