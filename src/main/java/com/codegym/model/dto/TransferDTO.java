package com.codegym.model.dto;

import lombok.*;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TransferDTO implements Validator {
    private long id;

    @NotEmpty(message = "Thông tin người gửi là bắt buộc")
    @Pattern(regexp = "^\\d+$", message = "ID người gửi không hợp lệ")
    private String senderId;

    @NotEmpty(message = "Thông tin người nhận là bắt buộc")
    @Pattern(regexp = "^\\d+$", message = "ID người nhận không hợp lệ")
    private String recipientId;

    private String transferAmount;

    @Override
    public boolean supports(Class<?> aClass) {
        return TransferDTO.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        TransferDTO transferDTO = (TransferDTO) target;

        String transferAmount = transferDTO.getTransferAmount();

        if (transferAmount != null && transferAmount.length() > 0) {
            if (transferAmount.length() > 9){
                errors.rejectValue("transferAmount", "transferAmount.max", "Số tiền chuyển khoản tối đa là 1.000.000.000 VNĐ");
                return;
            }

            if (transferAmount.length() < 6){
                errors.rejectValue("transactionAmount", "transactionAmount.min.length", "Số tiền chuyển khoản thấp nhất là 100.000 VNĐ");
                return;
            }

            if (!transferAmount.matches("(^$|[0-9]*$)")){
                errors.rejectValue("transferAmount", "transferAmount.number", "Chỉ chấp nhận số tiền chuyển khoản là ký tự số");
                return;
            }

            float transactionAmountFloat= Float.parseFloat(transferAmount);

            if (transactionAmountFloat % 10 > 0) {
                errors.rejectValue("transferAmount", "transferAmount.decimal", "Số tiền chuyển khoản phải là số chẵn chia hết cho 10");
            }
        } else {
            errors.rejectValue("transferAmount",  "transferAmount.null", "Số tiền chuyển khoản là bắt buộc");
        }
    }
}
