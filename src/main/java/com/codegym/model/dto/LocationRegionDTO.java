package com.codegym.model.dto;

import com.codegym.model.LocationRegion;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LocationRegionDTO {
    private long id;
    private String provinceId;
    private String provinceName;
    private String districtId;
    private String districtName;
    private String wardId;
    private String wardName;
    @NotEmpty(message = "Vui lòng nhập địa chỉ")
    private String address;

    public LocationRegion toLocationRegion(){
        return new LocationRegion()
                .setId(id)
                .setProvinceId(provinceId)
                .setProvinceName(provinceName)
                .setDistrictId(districtId)
                .setDistrictName(districtName)
                .setWardId(wardId)
                .setWardName(wardName)
                .setAddress(address);
    }
}
