package com.codegym.model.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TransferHistoryWithSumFeesAmountDTO {

    private List<TransferHistoryDTO> transferHistories;
    private BigDecimal sumFeesAmount;
}
