package com.codegym.model.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TransferHistoryDTO {
    private long id;
    private Date createdAt;
    private long senderId;
    private String senderName;
    private long recipientId;
    private String recipientName;
    private BigDecimal transferAmount;
    private long fees;
    private BigDecimal feesAmount;
}
