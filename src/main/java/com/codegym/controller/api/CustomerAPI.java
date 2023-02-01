package com.codegym.controller.api;

import com.codegym.exception.DataInputException;
import com.codegym.exception.EmailExistsException;
import com.codegym.model.Customer;
import com.codegym.model.CustomerAvatar;
import com.codegym.model.LocationRegion;
import com.codegym.model.dto.*;
import com.codegym.service.customer.ICustomerService;
import com.codegym.service.customerAvatar.ICustomerAvatarService;
import com.codegym.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
public class CustomerAPI {
    @Autowired
    private AppUtils appUtils;

    @Autowired
    private ICustomerService customerService;

    @Autowired
    private ICustomerAvatarService customerAvatarService;

    @GetMapping
    public ResponseEntity<?> getAllByDeletedIsFalse() {
        List<CustomerAvatarDTO> customerAvatarDTOS = customerService.getAllCustomerAvatarDTO();
        if (customerAvatarDTOS.size() == 0) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(customerAvatarDTOS, HttpStatus.OK);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<?> getById(@PathVariable String customerId) {
        long cid;
        try {
            cid = Long.parseLong(customerId);
        } catch (NumberFormatException e) {
            throw new DataInputException("ID Khách hàng không hợp lệ.");
        }

        Optional<Customer> customerOptional = customerService.findById(cid);

        if (!customerOptional.isPresent()) {
            throw new DataInputException("ID Khách hàng không hợp lệ.");
        }

        return new ResponseEntity<>(customerOptional.get().toCustomerDTO(), HttpStatus.OK);
    }

//    @PostMapping
//    public ResponseEntity<?> create(@Validated @RequestBody CustomerDTO customerDTO, BindingResult bindingResult) {
//
//        if (bindingResult.hasFieldErrors()) {
//            return appUtils.mapErrorToResponse(bindingResult);
//        }
//
//        Optional<CustomerDTO> customerOptionalDTO = customerService.getByEmailDTO(customerDTO.getEmail());
//
//        if (customerOptionalDTO.isPresent()) {
//            throw new EmailExistsException("Email đã tồn tại trong hệ thống.");
//        }
//
//        Customer customer = customerDTO.toCustomer();
//        customer.getLocationRegion().setId(null);
//        customer.setId(null);
//        customer.setBalance(BigDecimal.ZERO);
//        Customer newCustomer = customerService.save(customer);
//
//        return new ResponseEntity<>(newCustomer, HttpStatus.CREATED);
//    }

    @PostMapping
    public ResponseEntity<?> create(@Validated CustomerAvatarCreateDTO customerAvatarCreateDTO, BindingResult bindingResult) {

        MultipartFile imageFile = customerAvatarCreateDTO.getFile();

        if (imageFile == null) {
            throw new DataInputException("Vui lòng chọn hình ảnh.");
        }
        if (bindingResult.hasFieldErrors()) {
            return appUtils.mapErrorToResponse(bindingResult);
        }

        Optional<Customer> customerOptional = customerService.findByEmail(customerAvatarCreateDTO.getEmail());

        if (customerOptional.isPresent()) {
            throw new EmailExistsException("Email đã tồn tại trong hệ thống.");
        }

        LocationRegion locationRegion = customerAvatarCreateDTO.toLocationRegion();
        locationRegion.setId(null);
        customerAvatarCreateDTO.setId(null);
        customerAvatarCreateDTO.setBalance(BigDecimal.ZERO);

        CustomerAvatar newCustomerAvatar = customerService.createWithAvatar(customerAvatarCreateDTO, locationRegion);

        return new ResponseEntity<>(newCustomerAvatar.toCustomerAvatarDTO(), HttpStatus.CREATED);
    }

    @PatchMapping("/{customerId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<?> update(@PathVariable Long customerId, MultipartFile file, @Validated CustomerUpdateDTO customerUpdateDTO, BindingResult bindingResult) {

        Optional<Customer> customerOptional = customerService.findById(customerId);
        if (!customerOptional.isPresent()) {
            throw new DataInputException("ID khách hàng không tồn tại.");
        }
        LocationRegion locationRegion = customerUpdateDTO.toLocationRegion();
        Customer customer = customerUpdateDTO.toCustomer(locationRegion);

        CustomerAvatarDTO customerAvatarDTO = new CustomerAvatarDTO();
        if (bindingResult.hasFieldErrors()) {
            return appUtils.mapErrorToResponse(bindingResult);
        }
        if (file == null) {
            customerService.save(customer);
            customerAvatarDTO = customerAvatarService.getCustomerAvatarById(customerId);
        } else {
           CustomerAvatar customerAvatar =  customerService.saveWithAvatar(customerUpdateDTO, file, locationRegion);
           customerAvatarDTO = customerAvatar.toCustomerAvatarDTO();
        }

        return new ResponseEntity<>(customerAvatarDTO, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{customerId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long customerId) {

        Optional<Customer> customerOptional = customerService.findById(customerId);

        if (!customerOptional.isPresent()) {
            throw new DataInputException("ID khách hàng không hợp lệ.");
        }

        try {
            customerService.softDelete(customerId);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (Exception e) {
            throw new DataInputException("Vui lòng liên hệ Administrator.");
        }
    }

    @GetMapping("/get-all-recipients-without-sender/{senderId}")
    public ResponseEntity<?> getAllRecipientsWithoutSender(@PathVariable long senderId) {

        Optional<Customer> senderOptional = customerService.findById(senderId);

        if (!senderOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<RecipientDTO> recipientDTOS = customerService.getAllRecipientDTO(senderId);

        return new ResponseEntity<>(recipientDTOS, HttpStatus.OK);
    }
}
