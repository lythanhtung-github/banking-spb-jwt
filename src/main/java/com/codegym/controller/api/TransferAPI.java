package com.codegym.controller.api;

import com.codegym.exception.DataInputException;
import com.codegym.model.Customer;
import com.codegym.model.Transfer;
import com.codegym.model.dto.*;
import com.codegym.service.customer.ICustomerService;
import com.codegym.service.customerAvatar.ICustomerAvatarService;
import com.codegym.service.transfer.ITransferService;
import com.codegym.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/transfers")
public class TransferAPI {

    @Autowired
    private AppUtils appUtils;

    @Autowired
    private ITransferService transferService;

    @Autowired
    private ICustomerService customerService;

    @Autowired
    private ICustomerAvatarService customerAvatarService;

    @PostMapping
    public ResponseEntity<?> transfer(@Validated @RequestBody TransferDTO transferDTO, BindingResult bindingResult) {
        new TransferDTO().validate(transferDTO, bindingResult);
        if (bindingResult.hasFieldErrors()) {
            return appUtils.mapErrorToResponse(bindingResult);
        }

        Optional<Customer> senderOptional = customerService.findById(Long.parseLong(transferDTO.getSenderId()));
        if (!senderOptional.isPresent()) {
            throw new DataInputException("Thông tin người gửi không hợp lệ");
        }

        Customer sender = senderOptional.get();

        Optional<Customer> recipientOptional = customerService.findById(Long.parseLong(transferDTO.getRecipientId()));
        if (!recipientOptional.isPresent()) {
            throw new DataInputException("Thông tin người nhận không hợp lệ");
        }

        Customer recipient = recipientOptional.get();

        BigDecimal currentBalanceSender = sender.getBalance();
        BigDecimal currentBalanceRecipient = recipient.getBalance();
        String transferAmountStr = transferDTO.getTransferAmount();
        BigDecimal transferAmount = new BigDecimal(transferAmountStr);
        int fees = 10;
        BigDecimal feesAmount = transferAmount.multiply(BigDecimal.valueOf(fees)).divide(BigDecimal.valueOf(100L));
        BigDecimal transactionAmount = transferAmount.add(feesAmount);

        if (currentBalanceSender.compareTo(transactionAmount) < 0) {
            throw new DataInputException("Số dư người gửi không đủ thực hiện giao dịch");
        }

        BigDecimal maxBalance = new BigDecimal(999999999999L);
        if (currentBalanceRecipient.add(transferAmount).compareTo(maxBalance) > 0){
            throw new DataInputException("Số tiền thêm vào khiến tài khoản người nhận vượt quá ngưỡng cho phép. Bạn chỉ có thể gửi dưới " + maxBalance.subtract(currentBalanceRecipient) + " VNĐ");
        }
            try {
                Transfer transfer = new Transfer();
                transfer.setId(0L);
                transfer.setSender(sender);
                transfer.setRecipient(recipientOptional.get());
                transfer.setTransferAmount(transferAmount);
                transfer.setFees(fees);
                transfer.setFeesAmount(feesAmount);
                transfer.setTransactionAmount(transactionAmount);

                Customer newSender = customerService.transfer(transfer);

                Optional<Customer> newRecipientDTO = customerService.findById(Long.parseLong(transferDTO.getRecipientId()));
                Customer newRecipient = newRecipientDTO.get();

                Map<String, CustomerAvatarDTO> results = new HashMap<>();

                CustomerAvatarDTO senderAvatarDTO = customerAvatarService.getCustomerAvatarById(newSender.getId());
                CustomerAvatarDTO recipientAvatarDTO = customerAvatarService.getCustomerAvatarById(newRecipient.getId());

                results.put("senderAvatar", senderAvatarDTO);
                results.put("recipientAvatar", recipientAvatarDTO);

                return new ResponseEntity<>(results, HttpStatus.CREATED);
            } catch (Exception e) {
                throw new DataInputException("Vui lòng liên hệ Administrator");
            }
    }


    @GetMapping("/get-all-histories")
    public ResponseEntity<?> getAllHistories() {

        List<TransferHistoryDTO> transferHistoryDTOS = transferService.getAllHistories();

        return new ResponseEntity<>(transferHistoryDTOS, HttpStatus.OK);
    }

    @GetMapping("/get-sum-fees-amount")
    public ResponseEntity<?> getSumFeesAmount() {

        BigDecimal getAllFeesAmount = transferService.getSumFeesAmount();

        return new ResponseEntity<>(getAllFeesAmount, HttpStatus.OK);
    }

    @GetMapping("/get-all-histories-with-sum-fees-amount")
    public ResponseEntity<?> getAllHistoriesWithSumFeesAmount() {

        List<TransferHistoryDTO> transferHistoryDTOS = transferService.getAllHistories();

        BigDecimal getAllFeesAmount = transferService.getSumFeesAmount();

        TransferHistoryWithSumFeesAmountDTO transferHistoryWithSumFeesAmountDTO = new TransferHistoryWithSumFeesAmountDTO();
        transferHistoryWithSumFeesAmountDTO.setTransferHistories(transferHistoryDTOS);
        transferHistoryWithSumFeesAmountDTO.setSumFeesAmount(getAllFeesAmount);

        return new ResponseEntity<>(transferHistoryWithSumFeesAmountDTO, HttpStatus.OK);
    }
}
