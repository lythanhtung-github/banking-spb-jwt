package com.codegym.repository;

import com.codegym.model.CustomerAvatar;
import com.codegym.model.dto.CustomerAvatarDTO;
import com.codegym.model.dto.CustomerDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerAvatarRepository extends JpaRepository<CustomerAvatar, String> {
    @Query("SELECT NEW com.codegym.model.dto.CustomerAvatarDTO (" +
            "ca.id, " +
            "ca.fileName, " +
            "ca.fileFolder, " +
            "ca.fileUrl, " +
            "ca.fileType, " +
            "ca.cloudId, " +
            "ca.ts, " +
            "ca.customer" +
            ") FROM CustomerAvatar AS ca " +
            "JOIN Customer AS c " +
            "ON ca.customer.id = c.id " +
            " WHERE c.deleted = false "
    )
    List<CustomerAvatarDTO> getAllCustomerAvatarDTO();

    @Query("SELECT NEW com.codegym.model.dto.CustomerAvatarDTO (" +
            "ca.id, " +
            "ca.fileName, " +
            "ca.fileFolder, " +
            "ca.fileUrl, " +
            "ca.fileType, " +
            "ca.cloudId, " +
            "ca.ts, " +
            "ca.customer " +
            ") FROM CustomerAvatar AS ca " +
            "JOIN Customer AS c " +
            "ON ca.customer.id = c.id " +
            "WHERE c.deleted = false " +
            "AND c.id = :customerId"
    )
    CustomerAvatarDTO getCustomerAvatarById(@Param("customerId") long customerId);
}
