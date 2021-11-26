package com.mygroup.backendReslide.service;

import com.mygroup.backendReslide.dto.PaymentMethodDto;
import com.mygroup.backendReslide.exceptions.alreadyExists.PaymentMethodExistsException;
import com.mygroup.backendReslide.exceptions.notFound.PaymentMethodNotFoundException;
import com.mygroup.backendReslide.mapper.PaymentMethodMapper;
import com.mygroup.backendReslide.model.PaymentMethod;
import com.mygroup.backendReslide.repository.PaymentMethodRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PaymentMethodService {

    private final PaymentMethodMapper paymentMethodMapper;
    private final PaymentMethodRepository paymentMethodRepository;
    @Transactional
    public PaymentMethodDto create(PaymentMethodDto paymentTypeRequest) {
        if(paymentMethodRepository.findByNameIgnoreCase(paymentTypeRequest.getName()).isPresent()){
            throw new PaymentMethodExistsException(paymentTypeRequest.getName());
        }
        PaymentMethod paymentMethod =paymentMethodMapper.mapToEntity(paymentTypeRequest);
        // Enabled by default.
        paymentMethod.setEnabled(true);
        paymentMethod = paymentMethodRepository.save(paymentMethod);
        return paymentMethodMapper.mapToDto(paymentMethod);
    }
    @Transactional
    public void update(PaymentMethodDto paymentTypeRequest) {
        // Find the payment method.
        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentTypeRequest.getId())
                .orElseThrow(()->new PaymentMethodNotFoundException(paymentTypeRequest.getId()));
        // Check whether the name exists or not
        if(paymentMethodRepository.findByNameIgnoreCase(paymentTypeRequest.getName()).isPresent()
           && !paymentTypeRequest.getName().equals(paymentMethod.getName())){
            throw new PaymentMethodExistsException(paymentTypeRequest.getName());
        }
        paymentMethod.setName(paymentTypeRequest.getName());
        paymentMethod.setNotes(paymentTypeRequest.getNotes());
        paymentMethodRepository.save(paymentMethod);
    }

    @Transactional(readOnly = true)
    public List<PaymentMethodDto> search(String name) {
        // If name is null, returns every active method.
        if(name == null){
            return paymentMethodRepository.findByEnabled(true)
                    .stream()
                    .map(paymentMethodMapper :: mapToDto)
                    .collect(Collectors.toList());
        }else {
            return paymentMethodRepository.findByNameIgnoreCaseContainsAndEnabled(name, true)
                    .stream()
                    .map(paymentMethodMapper::mapToDto)
                    .collect(Collectors.toList());
        }
    }
    @Transactional(readOnly = true)
    public PaymentMethodDto getPaymentMethod(String name){
        return paymentMethodMapper.mapToDto(
                paymentMethodRepository.findByNameIgnoreCase(name)
                .orElseThrow(()->new PaymentMethodNotFoundException(name))
        );
    }
    @Transactional(readOnly = true)
    public PaymentMethod getPaymentMethod_Entity(String name) {
        return paymentMethodRepository.findByNameIgnoreCase(name)
                .orElseThrow(()->new PaymentMethodNotFoundException(name));
    }

    public PaymentMethodDto get(Long id) {
        // Search the payment method, map it to DTO and then return it
        PaymentMethod paymentMethod = paymentMethodRepository.findById(id)
                                    .orElseThrow(()-> new PaymentMethodNotFoundException(id));
        return paymentMethodMapper.mapToDto(paymentMethod);
    }
}
