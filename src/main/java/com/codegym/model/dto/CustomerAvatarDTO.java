package com.codegym.model.dto;

import com.codegym.model.Customer;
import com.codegym.model.CustomerAvatar;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CustomerAvatarDTO {
    private String id;
    private String fileName;
    private String fileFolder;
    @NotEmpty(message = "Vui lòng chọn hình ảnh")
    private String fileUrl;
    private String fileType;
    private String cloudId;
    private Long ts;
    @Valid
    private CustomerDTO customer;

    public CustomerAvatarDTO(String id, String fileName, String fileFolder, String fileUrl, String fileType, String cloudId, Long ts, Customer customer) {
        this.id = id;
        this.fileName = fileName;
        this.fileFolder = fileFolder;
        this.fileUrl = fileUrl;
        this.fileType = fileType;
        this.cloudId = cloudId;
        this.ts = ts;
        this.customer = customer.toCustomerDTO();
    }

    public CustomerAvatar toCustomerAvatar() {
        return new CustomerAvatar()
                .setId(id)
                .setFileName(fileName)
                .setFileUrl(fileFolder)
                .setFileType(fileUrl)
                .setFileType(fileType)
                .setCloudId(cloudId)
                .setTs(ts)
                .setCustomer(customer.toCustomer());
    }
}
