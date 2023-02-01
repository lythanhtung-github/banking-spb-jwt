package com.codegym.service.customerAvatar;

import com.codegym.model.CustomerAvatar;
import com.codegym.model.dto.CustomerAvatarDTO;
import com.codegym.repository.CustomerAvatarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerAvatarServiceImpl implements ICustomerAvatarService {

    @Autowired
    private CustomerAvatarRepository customerAvatarRepository;

    @Override
    public List<CustomerAvatar> findAll() {
        return null;
    }

    @Override
    public CustomerAvatar getById(Long id) {
        return null;
    }

    @Override
    public Optional<CustomerAvatar> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public CustomerAvatar save(CustomerAvatar customerAvatar) {
        return customerAvatarRepository.save(customerAvatar);
    }

    @Override
    public void remove(Long id) {

    }

    @Override
    public void delete(String id) {
        customerAvatarRepository.deleteById(id);
    }

    @Override
    public List<CustomerAvatarDTO> getAllCustomerAvatarDTO() {
        return customerAvatarRepository.getAllCustomerAvatarDTO();
    }

    @Override
    public CustomerAvatarDTO getCustomerAvatarById(long customerId){
        return customerAvatarRepository.getCustomerAvatarById(customerId);
    }
}
