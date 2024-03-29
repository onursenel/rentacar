package com.etiya.rentACar.business.abstracts;

import com.etiya.rentACar.business.dtos.requests.rentals.ChangeRentedCarRequest;
import com.etiya.rentACar.business.dtos.requests.rentals.CreateRentalRequest;
import com.etiya.rentACar.business.dtos.requests.rentals.UpdateRentalRequest;
import com.etiya.rentACar.business.dtos.responses.rentals.*;

import java.util.List;

public interface RentalService {
    List<GetRentalListResponse> getAll();

    GetRentalResponse getById(int id);
    CreatedRentalResponse add(CreateRentalRequest createRentalRequest);

    ChangedRentedCarResponse change(ChangeRentedCarRequest changeRentedCarRequest);

    UpdatedRentalResponse update(UpdateRentalRequest updateRentalRequest);

    void delete(int id);

}
