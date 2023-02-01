package com.codegym.repository;

import com.codegym.model.Transfer;
import com.codegym.model.dto.TransferHistoryDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {
    @Query("SELECT NEW com.codegym.model.dto.TransferHistoryDTO (" +
            "t.id, " +
            "t.createdAt, " +
            "t.sender.id, " +
            "t.sender.fullName, " +
            "t.recipient.id, " +
            "t.recipient.fullName, " +
            "t.transferAmount, " +
            "t.fees, " +
            "t.feesAmount" +
            ") " +
            "FROM Transfer AS t "
    )
    List<TransferHistoryDTO> getAllHistories();


    @Query("SELECT SUM(t.feesAmount) FROM Transfer AS t")
    BigDecimal getSumFeesAmount();
}
