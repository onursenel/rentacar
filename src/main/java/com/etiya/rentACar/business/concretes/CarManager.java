package com.etiya.rentACar.business.concretes;

import com.etiya.rentACar.business.abstracts.CarService;
import com.etiya.rentACar.business.abstracts.CustomerService;
import com.etiya.rentACar.business.dtos.responses.customers.GetCustomerListResponse;
import com.etiya.rentACar.core.utilities.mapping.ModelMapperService;
import com.etiya.rentACar.business.dtos.requests.cars.CreateCarRequest;
import com.etiya.rentACar.business.dtos.requests.cars.UpdateCarRequest;
import com.etiya.rentACar.business.dtos.responses.cars.CreatedCarResponse;
import com.etiya.rentACar.business.dtos.responses.cars.GetCarListResponse;
import com.etiya.rentACar.business.dtos.responses.cars.GetCarResponse;
import com.etiya.rentACar.business.dtos.responses.cars.UpdatedCarResponse;
import com.etiya.rentACar.business.rules.CarBusinessRules;
import com.etiya.rentACar.dataAccess.abstracts.CarRepository;
import com.etiya.rentACar.entities.Car;
import com.etiya.rentACar.entities.Customer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class CarManager implements CarService {

    private ModelMapperService modelMapperService;
    private CarRepository carRepository;
    private CarBusinessRules carBusinessRules;
    private CustomerService customerService;

    //private ModelService modelService;

    @Override
    public List<GetCarListResponse> getAll() {
        List<Car> cars = carRepository.findAll();
        List<GetCarListResponse> carResponse = cars.stream()
                .map(car ->this.modelMapperService.forResponse()
                        .map(car, GetCarListResponse.class)).collect(Collectors.toList());
        return carResponse;
    }

    @Override
    public GetCarResponse getById(int id) {
        Car car = carRepository.findById(id).orElseThrow();//id db'de yoksa hata atıyor
        GetCarResponse carResponse = modelMapperService.forResponse().map(car, GetCarResponse.class);
        return carResponse;
    }

    @Override
    public CreatedCarResponse add(CreateCarRequest createCarRequest) {
        carBusinessRules.carPlateCannotBeDuplicated(createCarRequest.getPlate());
        // CreateCarRequest nesnesinden gelen verileri kullanarak yeni bir Car nesnesi oluşturulur
        Car car = modelMapperService.forRequest().map(createCarRequest, Car.class);
        car.setId(0);

        // Car nesnesini veritabanına kaydedilir
        //car.setCreatedDate(LocalDateTime.now());
        Car createdCar = carRepository.save(car);

        // Oluşturulan Car nesnesi CreatedCarResponse nesnesine dönüştürülür
        CreatedCarResponse createdCarResponse = modelMapperService.forResponse().map(createdCar, CreatedCarResponse.class);

        // Oluşturulan CreatedCarResponse nesnesini döndürülür
        return createdCarResponse;
    }


    @Override
    public UpdatedCarResponse update(UpdateCarRequest updateCarRequest) {
        carBusinessRules.carPlateCannotBeDuplicated(updateCarRequest.getPlate());
        Car getCar = carRepository.findById(updateCarRequest.getId()).get();//
        Car car = modelMapperService.forRequest().map(updateCarRequest, Car.class);
        //car.setUpdatedDate(LocalDateTime.now());
        car.setCreatedDate(getCar.getCreatedDate());//
        Car updatedCar = carRepository.save(car);
        UpdatedCarResponse updatedCarResponse = modelMapperService.forResponse().map(updatedCar, UpdatedCarResponse.class);
        return updatedCarResponse;
    }

    @Override
    public void delete(int id) {
        carRepository.deleteById(id);
    }

    @Override
    public List<GetCarListResponse> getSearchPlate(String plate) {
        List<Car> cars = carRepository.searchPlate(plate);
        List<GetCarListResponse> getCarListResponses = cars.stream()
                .map(car -> modelMapperService.forResponse().map(car,GetCarListResponse.class))
                .collect(Collectors.toList());
        return getCarListResponses;
    }

    @Override
    public GetCarListResponse getByCarId(int id) {

        Car car = carBusinessRules.checkByCarId(id);
        GetCarListResponse response =
                modelMapperService.forResponse().map(car,GetCarListResponse.class);
        return response;
    }

    @Override
    public void updateCarState(int carId, int state) {
        Car car = carRepository.findById(carId).get();
        car.setCarState(state);
        carRepository.save(car);
    }



    @Override
    public void updateCarKilometer(int carId, int endKilometer) {
        Car car = carRepository.findById(carId).get();
        car.setKilometer(endKilometer);
        carRepository.save(car);
    }

}