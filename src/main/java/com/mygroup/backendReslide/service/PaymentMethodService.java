package com.mygroup.backendReslide.service;

import com.mygroup.backendReslide.dto.PaymentMethodDto;
import com.mygroup.backendReslide.exceptions.alreadyExists.PaymentMethodExistsException;
import com.mygroup.backendReslide.exceptions.notFound.PaymentMethodNotFoundException;
import com.mygroup.backendReslide.mapper.PaymentMethodMapper;
import com.mygroup.backendReslide.model.PaymentMethod;
import com.mygroup.backendReslide.repository.PaymentMethodRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PaymentMethodService {

    private final PaymentMethodMapper paymentMethodMapper;
    private final PaymentMethodRepository paymentMethodRepository;

    public void create(PaymentMethodDto paymentTypeRequest) {
        if(paymentMethodRepository.findByNameIgnoreCase(paymentTypeRequest.getName()).isPresent()){
            throw new PaymentMethodExistsException(paymentTypeRequest.getName());
        }
        paymentMethodRepository.save(paymentMethodMapper.mapToEntity(paymentTypeRequest));
    }

    public void update(PaymentMethodDto paymentTypeRequest) {
        // Find the payment method.
        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentTypeRequest.getId())
                .orElseThrow(()->new PaymentMethodNotFoundException(paymentTypeRequest.getId()));
        // Check whether the name exists or not
        if(paymentMethodRepository.findByNameIgnoreCase(paymentTypeRequest.getName()).isPresent()
           && !paymentTypeRequest.getName().equals(paymentMethod.getName())){
            throw new PaymentMethodExistsException(paymentTypeRequest.getName());
        }
    }


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
}
