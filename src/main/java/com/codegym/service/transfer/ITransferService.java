package com.codegym.service.transfer;

import com.codegym.model.Transfer;
import com.codegym.model.dto.TransferHistoryDTO;
import com.codegym.service.IGeneralService;

import java.math.BigDecimal;
import java.util.List;

public interface ITransferService extends IGeneralService<Transfer> {
    List<TransferHistoryDTO> getAllHistories();

    BigDecimal getSumFeesAmount();
}
