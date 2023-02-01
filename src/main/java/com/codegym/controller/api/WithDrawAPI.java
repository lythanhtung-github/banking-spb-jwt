package com.codegym.controller.api;

import com.codegym.exception.DataInputException;
import com.codegym.model.Customer;
import com.codegym.model.Withdraw;
import com.codegym.model.dto.CustomerAvatarDTO;
import com.codegym.model.dto.WithdrawDTO;
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
@RequestMapping("/api/withdraws")
public class WithDrawAPI {
    @Autowired
    private ICustomerService customerService;

    @Autowired
    private AppUtils appUtils;

    @Autowired
    private ICustomerAvatarService customerAvatarService;

    @PostMapping
    public ResponseEntity<?> withdraw(@Validated @RequestBody WithdrawDTO withdrawDTO, BindingResult bindingResult) {
        new WithdrawDTO().validate(withdrawDTO, bindingResult);
        if (bindingResult.hasFieldErrors()) {
            return appUtils.mapErrorToResponse(bindingResult);
        }
        Long customerId =Long.parseLong(withdrawDTO.getCustomerId());
        Optional<Customer> customerOptional = customerService.findById(customerId);
        if (!customerOptional.isPresent()) {
            throw new DataInputException("ID khách hàng không hợp lệ");
        }
        Customer customer = customerOptional.get();
        BigDecimal balance = customer.getBalance();
        BigDecimal transactionAmount = new BigDecimal(withdrawDTO.getTransactionAmount());

        if (balance.compareTo(transactionAmount) < 0) {
            throw new DataInputException("Số tiền muốn rút lớn hơn số tiền hiện có trong tài khoản");
        }
        if (transactionAmount.compareTo(new BigDecimal(100000)) <= 0) {
            throw new DataInputException("Số tiền muốn rút ít nhất là 100.000 VNĐ");
        }

        if (transactionAmount.compareTo(new BigDecimal(1000000000)) >= 0) {
            throw new DataInputException("Số tiền muốn rút nhiều nhất là 1.000.000.000 VNĐ");
        }

        Withdraw withdraw = new Withdraw();
        withdraw.setTransactionAmount(transactionAmount);
        withdraw.setCustomer(customer);

        Customer newCustomer = customerService.withdraw(customer, withdraw);
        CustomerAvatarDTO customerAvatarDTO = customerAvatarService.getCustomerAvatarById(newCustomer.getId());
        return new ResponseEntity<>(customerAvatarDTO, HttpStatus.CREATED);
    }
}
