package com.mygroup.backendReslide.service;

import com.mygroup.backendReslide.dto.request.OrderRequest;
import com.mygroup.backendReslide.dto.response.OrderResponse;
import com.mygroup.backendReslide.exceptions.notFound.OrderNotFoundException;
import com.mygroup.backendReslide.mapper.OrderMapper;
import com.mygroup.backendReslide.model.Order;
import com.mygroup.backendReslide.model.OrderDetail;
import com.mygroup.backendReslide.model.Payment;
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

    @Transactional
    public void create(OrderRequest orderRequest) {
        // Map the order.
        Order order = orderMapper.mapToEntity(orderRequest);
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
        orderRepository.save(order);
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

        order.setActualDeliveryDate(modifiedOrder.getActualDeliveryDate());
        order.setExpectedDeliveryDate(modifiedOrder.getExpectedDeliveryDate());
        order.setStatus(modifiedOrder.getStatus());
        order.setProvider(modifiedOrder.getProvider());

        // Save the changes.
        transactionRepository.save(order.getTransaction());
        orderRepository.save(order);
    }
    @Transactional(readOnly = true)
    public List<OrderResponse> search(String start, String end) {
        return orderRepository.findByDate(Instant.parse(start), Instant.parse(end))
                .stream()
                .map(orderMapper :: mapToDto)
                .map(this :: hideOrderDetails) // Hide the invoice details.
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public List<OrderResponse> searchByClient(String start, String end, String clientCode) {
        return orderRepository.findByDateAndProviderCode(Instant.parse(start), Instant.parse(end),clientCode)
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
    private OrderResponse hideOrderDetails(OrderResponse orderResponse){
        orderResponse.setDetails(null);
        return orderResponse;
    }
}
