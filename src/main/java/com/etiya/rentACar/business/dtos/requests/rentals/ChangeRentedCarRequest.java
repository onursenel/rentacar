package com.etiya.rentACar.business.dtos.requests.rentals;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeRentedCarRequest {
    private int id;
    private int carId;
    //private int customerId;
}
