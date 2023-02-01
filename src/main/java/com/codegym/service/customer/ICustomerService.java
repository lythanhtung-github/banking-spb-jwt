package com.codegym.service.customer;

import com.codegym.model.*;
import com.codegym.model.dto.*;
import com.codegym.service.IGeneralService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ICustomerService extends IGeneralService<Customer> {
    List<CustomerDTO> getAllCustomerDTO();

//    Customer save(Customer customer, LocationRegion locationRegion);

    List<CustomerAvatarDTO> getAllCustomerAvatarDTO();

    void softDelete(long customerId);

    List<Customer> findAllByIdNot(long id);

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByEmailAndIdIsNot(String email, Long id);

    Optional<CustomerDTO> getByEmailDTO(String email);

    List<RecipientDTO> getAllRecipientDTO(long senderId);

    Customer deposit(Customer customer, Deposit deposit);

    Customer withdraw(Customer customer, Withdraw withdraw);

    Customer transfer(Transfer transfer);

    CustomerAvatar createWithAvatar(CustomerAvatarCreateDTO customerAvatarCreateDTO, LocationRegion locationRegion);


    CustomerAvatar saveWithAvatar(CustomerUpdateDTO customerUpdateDTO, MultipartFile file, LocationRegion locationRegion);
}
