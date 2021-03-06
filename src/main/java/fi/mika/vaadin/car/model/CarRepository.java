package fi.mika.vaadin.car.model;

import java.util.List;

public interface CarRepository {
    Long create(Car car);
    List<Car> readAll();
    void update(Car car);
    void delete(Car car);
}
