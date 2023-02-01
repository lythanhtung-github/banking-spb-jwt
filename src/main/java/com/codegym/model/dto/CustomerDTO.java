package com.codegym.model.dto;

import com.codegym.model.Customer;
import com.codegym.model.LocationRegion;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CustomerDTO {
    private Long id;

    @NotEmpty(message = "Vui lòng nhập tên khách hàng")
    @Size(min = 5, max = 100, message = "Họ tên có độ dài nằm trong khoảng 5 - 100 ký tự")
    private String fullName;

    @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Email không đúng định dạng")
    @NotEmpty(message = "Vui lòng nhập email")
    private String email;

    @NotEmpty(message = "Vui lòng nhập số điện thoại")
    private String phone;

    @Pattern(regexp = "^\\d+$", message = "Số tiền gửi phải là số")
    private String balance;

    @Valid
    private LocationRegionDTO locationRegion;

    public CustomerDTO(Long id, String fullName, String email, String phone, BigDecimal balance, LocationRegion locationRegion) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.balance = balance.toString();
        this.locationRegion = locationRegion.toLocationRegionDTO();
    }

    public Customer toCustomer(){
        return new Customer()
                .setId(id)
                .setFullName(fullName)
                .setEmail(email)
                .setPhone(phone)
                .setBalance(new BigDecimal(Long.parseLong(balance)))
                .setLocationRegion(locationRegion.toLocationRegion());
    }
}
