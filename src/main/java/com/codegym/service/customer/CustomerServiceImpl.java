package com.codegym.service.customer;

import com.codegym.exception.DataInputException;
import com.codegym.model.*;
import com.codegym.model.dto.*;
import com.codegym.model.enums.FileType;
import com.codegym.repository.*;
import com.codegym.service.customerAvatar.ICustomerAvatarService;
import com.codegym.upload.IUploadService;
import com.codegym.utils.UploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class CustomerServiceImpl implements ICustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private LocationRegionRepository locationRegionRepository;

    @Autowired
    private DepositRepository depositRepository;

    @Autowired
    private WithdrawRepository withdrawRepository;

    @Autowired
    private TransferRepository transferRepository;


    @Autowired
    private ICustomerAvatarService customerAvatarService;

    @Autowired
    private IUploadService uploadService;

    @Autowired
    private UploadUtil uploadUtil;

    @Override
    public List<CustomerDTO> getAllCustomerDTO() {
        return customerRepository.getAllCustomerDTO();
    }

    @Override
    public List<CustomerAvatarDTO> getAllCustomerAvatarDTO() {
        return customerAvatarService.getAllCustomerAvatarDTO();
    }

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public Customer save(Customer customer) {
        locationRegionRepository.save(customer.getLocationRegion());
        return customerRepository.save(customer);
    }

    @Override
    public void remove(Long id) {

    }

    @Override
    public Customer getById(Long id) {
        return null;
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    public void softDelete(long customerId) {
        customerRepository.softDelete(customerId);
    }

    @Override
    public List<Customer> findAllByIdNot(long id) {
        return customerRepository.findAllByIdNot(id);
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    @Override
    public Optional<Customer> findByEmailAndIdIsNot(String email, Long id) {
        return customerRepository.findByEmailAndIdIsNot(email, id);
    }

    @Override
    public Optional<CustomerDTO> getByEmailDTO(String email) {
        return customerRepository.getByEmailDTO(email);
    }

    @Override
    public List<RecipientDTO> getAllRecipientDTO(long senderId) {
        return customerRepository.getAllRecipientDTO(senderId);
    }

    @Override
    public Customer deposit(Customer customer, Deposit deposit) {
        BigDecimal currentBalance = customer.getBalance();
        BigDecimal transactionAmount = deposit.getTransactionAmount();

        try {
            customerRepository.incrementBalance(transactionAmount, customer.getId());

            deposit.setId(0L);
            deposit.setCustomer(customer);
            depositRepository.save(deposit);

            Optional<Customer> newCustomer = customerRepository.findById(customer.getId());
            return newCustomer.get();
        } catch (Exception e) {
            customer.setBalance(currentBalance);
            return customer;
        }
    }

    @Override
    public Customer withdraw(Customer customer, Withdraw withdraw) {
        BigDecimal currentBalance = customer.getBalance();
        BigDecimal transactionAmount = withdraw.getTransactionAmount();

        try {
            customerRepository.reduceBalance(transactionAmount, customer.getId());

            withdraw.setId(0L);
            withdraw.setCustomer(customer);
            withdrawRepository.save(withdraw);

            Optional<Customer> newCustomer = customerRepository.findById(customer.getId());

            return newCustomer.get();
        } catch (Exception e) {
            e.printStackTrace();
            customer.setBalance(currentBalance);
            return customer;
        }
    }

    @Override
    public Customer transfer(Transfer transfer) {
        customerRepository.reduceBalance(transfer.getTransactionAmount(), transfer.getSender().getId());
        customerRepository.incrementBalance(transfer.getTransferAmount(), transfer.getRecipient().getId());
        transferRepository.save(transfer);
        Optional<Customer> newSender = customerRepository.findById(transfer.getSender().getId());

        return newSender.get();
    }

    @Override
    public CustomerAvatar createWithAvatar(CustomerAvatarCreateDTO customerAvatarCreateDTO, LocationRegion locationRegion) {

        locationRegion = locationRegionRepository.save(locationRegion);
        Customer customer = customerAvatarCreateDTO.toCustomer(locationRegion);
        customer = customerRepository.save(customer);

        String fileType = customerAvatarCreateDTO.getFile().getContentType();
        assert fileType != null;
        fileType = fileType.substring(0, 5);

        CustomerAvatar customerAvatar = new CustomerAvatar();
        customerAvatar.setCustomer(customer).setFileType(fileType);
        customerAvatar = customerAvatarService.save(customerAvatar);

        CustomerAvatar newCustomerAvatar = new CustomerAvatar();
        if (fileType.equals(FileType.IMAGE.getValue())) {
            newCustomerAvatar = uploadAndSaveCustomerImage(customerAvatarCreateDTO.getFile(), customerAvatar, customer);
        }
        return newCustomerAvatar;
    }

    private CustomerAvatar uploadAndSaveCustomerImage(MultipartFile file, CustomerAvatar customerAvatar, Customer customer) {
        try {
            Map uploadResult = uploadService.uploadImage(file, uploadUtil.buildImageUploadParams(customerAvatar));
            String fileUrl = (String) uploadResult.get("secure_url");
            String fileFormat = (String) uploadResult.get("format");

            customerAvatar.setFileName(customerAvatar.getId() + "." + fileFormat);
            customerAvatar.setFileUrl(fileUrl);
            customerAvatar.setFileFolder(UploadUtil.IMAGE_UPLOAD_FOLDER);
            customerAvatar.setCloudId(customerAvatar.getFileFolder() + "/" + customerAvatar.getId());
            customerAvatar.setCustomer(customer);
            return customerAvatarService.save(customerAvatar);
        } catch (IOException e) {
            e.printStackTrace();
            throw new DataInputException("Upload hình ảnh thất bại");
        }
    }

    @Override
    public CustomerAvatar saveWithAvatar(CustomerUpdateDTO customerUpdateDTO, MultipartFile file, LocationRegion locationRegion) {

        locationRegion = locationRegionRepository.save(locationRegion);
        Customer customer = customerUpdateDTO.toCustomer(locationRegion);
        customer = customerRepository.save(customer);

        CustomerAvatar oldCustomerAvatar = customerAvatarService.getCustomerAvatarById(customer.getId()).toCustomerAvatar();
        customerAvatarService.delete(oldCustomerAvatar.getId());

        String fileType = file.getContentType();
        assert fileType != null;
        fileType = fileType.substring(0, 5);

        CustomerAvatar customerAvatar = new CustomerAvatar();
        customerAvatar.setCustomer(customer).setFileType(fileType);
        customerAvatar = customerAvatarService.save(customerAvatar);

        CustomerAvatar newCustomerAvatar = new CustomerAvatar();
        if (fileType.equals(FileType.IMAGE.getValue())) {
            newCustomerAvatar = uploadAndSaveCustomerImage(file, customerAvatar, customer);
        }
        return newCustomerAvatar;
    }
}
