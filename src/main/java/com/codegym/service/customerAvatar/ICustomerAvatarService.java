package com.codegym.service.customerAvatar;

import com.codegym.model.CustomerAvatar;
import com.codegym.model.dto.CustomerAvatarDTO;
import com.codegym.service.IGeneralService;

import java.util.List;

public interface ICustomerAvatarService extends IGeneralService<CustomerAvatar> {
    void delete(String id);

    List<CustomerAvatarDTO> getAllCustomerAvatarDTO();
    CustomerAvatarDTO getCustomerAvatarById(long customerId);
}
