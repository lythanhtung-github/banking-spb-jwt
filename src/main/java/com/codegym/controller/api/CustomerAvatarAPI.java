package com.codegym.controller.api;

import com.codegym.exception.DataInputException;
import com.codegym.model.Customer;
import com.codegym.model.CustomerAvatar;
import com.codegym.model.dto.CustomerAvatarDTO;
import com.codegym.service.customer.ICustomerService;
import com.codegym.service.customerAvatar.ICustomerAvatarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/customer-avatars")
public class CustomerAvatarAPI {
    @Autowired
    private ICustomerAvatarService customerAvatarService;

    @Autowired
    private ICustomerService customerService;

    @GetMapping("/{customerId}")
    public ResponseEntity<?> getByCustomerId(@PathVariable String customerId) {
        long cid;
        try {
            cid = Long.parseLong(customerId);
        } catch (NumberFormatException e) {
            throw new DataInputException("ID Khách hàng không hợp lệ.");
        }

        Optional<Customer> customerOptional = customerService.findById(cid);

        if (!customerOptional.isPresent()) {
            throw new DataInputException("ID Khách hàng không hợp lệ.");
        }

        CustomerAvatarDTO newCustomerAvatar = customerAvatarService.getCustomerAvatarById(cid);

        return new ResponseEntity<>(newCustomerAvatar, HttpStatus.OK);
    }

}
