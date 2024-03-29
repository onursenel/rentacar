package com.etiya.rentACar.business.concretes;

import com.etiya.rentACar.business.abstracts.CarService;
import com.etiya.rentACar.business.abstracts.CustomerService;
import com.etiya.rentACar.business.abstracts.RentalService;
import com.etiya.rentACar.business.dtos.requests.rentals.ChangeRentedCarRequest;
import com.etiya.rentACar.business.dtos.requests.rentals.CreateRentalRequest;
import com.etiya.rentACar.business.dtos.requests.rentals.UpdateRentalRequest;
import com.etiya.rentACar.business.dtos.responses.cars.GetCarListResponse;
import com.etiya.rentACar.business.dtos.responses.customers.GetCustomerListResponse;
import com.etiya.rentACar.business.dtos.responses.rentals.*;
import com.etiya.rentACar.business.rules.RentalBusinessRules;
import com.etiya.rentACar.core.utilities.mapping.ModelMapperService;
import com.etiya.rentACar.dataAccess.abstracts.RentalRepository;
import com.etiya.rentACar.entities.Car;
import com.etiya.rentACar.entities.Customer;
import com.etiya.rentACar.entities.Rental;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class RentalManager implements RentalService {
    //Rules için burada if görmemeliyiz
    private RentalRepository rentalRepository;//bu ifadeyi newlemektense constructorda çağırarak dependecy injection yapılır
    private ModelMapperService modelMapperService;
    private CarService carService;
    private RentalBusinessRules rentalBusinessRules;
    private CustomerService customerService;

    @Override
    public List<GetRentalListResponse> getAll() {
        List<Rental> rentals = rentalRepository.findAll();
        List<GetRentalListResponse> rentalResponse = rentals.stream()
                .map(rental ->this.modelMapperService.forResponse()
                        .map(rental, GetRentalListResponse.class)).collect(Collectors.toList());
        return rentalResponse;
    }

    @Override
    public GetRentalResponse getById(int id) {
        Rental rental = rentalRepository.findById(id).orElseThrow();//id db'de yoksa hata atıyor
        GetRentalResponse rentalResponse = modelMapperService.forResponse().map(rental, GetRentalResponse.class);
        return rentalResponse;
    }

    @Override
    public CreatedRentalResponse add(CreateRentalRequest createRentalRequest) {
        rentalBusinessRules.checkIfCarState(createRentalRequest.getCarId());
        GetCarListResponse response = carService.getByCarId(createRentalRequest.getCarId());
        Car car = new Car();
        car.setId(createRentalRequest.getCarId());
        carService.getById(createRentalRequest.getCarId());


        Customer customer = new Customer();
        customer.setId(createRentalRequest.getCustomerId());

        Rental mappedRental = new Rental();
        mappedRental.setStartDate(createRentalRequest.getStartDate());
        mappedRental.setEndDate(createRentalRequest.getEndDate());
        mappedRental.setStartKilometer(response.getKilometer());
        mappedRental.setCar(car);
        mappedRental.setCustomer(customer);
        Rental createdRental = rentalRepository.save(mappedRental);
        //carService.updateCarStateForCustomer(createRentalRequest.getCarId(),createRentalRequest.getCustomerId(),2);

        carService.updateCarState(createRentalRequest.getCarId(),2);

        CreatedRentalResponse createdRentalResponse = modelMapperService.forResponse()
                .map(createdRental,CreatedRentalResponse.class);
        return createdRentalResponse;
    }

    @Override
    public ChangedRentedCarResponse change(ChangeRentedCarRequest changeRentedCarRequest) {
        Rental oldRental = rentalRepository.findById(changeRentedCarRequest.getId()).orElseThrow();

        carService.updateCarState(oldRental.getCar().getId(),1);
        carService.updateCarState(changeRentedCarRequest.getCarId(),2);

        Rental rental = new Rental();
        rental.setId(changeRentedCarRequest.getId());
        Car car =new Car();
        car.setId(changeRentedCarRequest.getCarId());
        rental.setCar(car);

        Rental createdRental = rentalRepository.save(rental);




        ChangedRentedCarResponse changedRentedResponse = modelMapperService.forResponse()
                .map(createdRental,ChangedRentedCarResponse.class);
        return changedRentedResponse;

    }



    @Override
    public UpdatedRentalResponse update(UpdateRentalRequest updateRentalRequest) {

        Rental getRental = rentalRepository.findById(updateRentalRequest.getId()).get();
        Rental rental = modelMapperService.forRequest().map(updateRentalRequest, Rental.class);

        rentalBusinessRules.checkIfCarIdIsSame(updateRentalRequest.getId(),updateRentalRequest.getCarId());

        rental.setCreatedDate(getRental.getCreatedDate());


        carService.updateCarKilometer(updateRentalRequest.getCarId(), updateRentalRequest.getEndKilometer());

        carService.updateCarState(updateRentalRequest.getCarId(), 1);

        Rental updatedRental = rentalRepository.save(rental);
        UpdatedRentalResponse updatedRentalResponse = modelMapperService.forResponse().map(updatedRental, UpdatedRentalResponse.class);
        return updatedRentalResponse;
    }

    @Override
    public void delete(int id) {
        rentalRepository.deleteById(id);
    }


}
