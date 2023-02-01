package com.codegym.controller.api;

import com.codegym.exception.DataInputException;
import com.codegym.model.Customer;
import com.codegym.model.CustomerAvatar;
import com.codegym.model.Deposit;
import com.codegym.model.dto.CustomerAvatarDTO;
import com.codegym.model.dto.DepositDTO;
import com.codegym.service.customer.ICustomerService;
import com.codegym.service.customerAvatar.ICustomerAvatarService;
import com.codegym.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/deposits")
public class DepositAPI {

    @Autowired
    private AppUtils appUtils;

    @Autowired
    private ICustomerService customerService;

    @Autowired
    private ICustomerAvatarService customerAvatarService;

    @PostMapping
    public ResponseEntity<?> deposit(@Validated @RequestBody DepositDTO depositDTO, BindingResult bindingResult) {
        new DepositDTO().validate(depositDTO, bindingResult);
        if (bindingResult.hasFieldErrors()) {
            return appUtils.mapErrorToResponse(bindingResult);
        }
        Long customerId = Long.parseLong(depositDTO.getCustomerId());
        Optional<Customer> customerOptional = customerService.findById(customerId);
        if (!customerOptional.isPresent()) {
            throw new DataInputException("ID khách hàng không hợp lệ");
        }

        Customer customer = customerOptional.get();
        BigDecimal balance = customer.getBalance();

        BigDecimal transactionAmount = new BigDecimal(depositDTO.getTransactionAmount());
        BigDecimal maxBalance = new BigDecimal(999999999999L);
        if (balance.add(transactionAmount).compareTo(maxBalance) > 0) {
            throw new DataInputException("Số tiền thêm vào khiến tài khoản vượt quá ngưỡng cho phép. Bạn chỉ có thể nạp thêm " + maxBalance.subtract(balance) + " VNĐ");
        }

        Deposit deposit = new Deposit();
        deposit.setTransactionAmount(transactionAmount);
        deposit.setCustomer(customerOptional.get());

        Customer newCustomer = customerService.deposit(customer, deposit);
        CustomerAvatarDTO customerAvatarDTO = customerAvatarService.getCustomerAvatarById(newCustomer.getId());
        return new ResponseEntity<>(customerAvatarDTO, HttpStatus.CREATED);
    }
}
