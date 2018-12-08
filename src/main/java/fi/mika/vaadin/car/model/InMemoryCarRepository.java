package fi.mika.vaadin.car.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryCarRepository implements CarRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryCarRepository.class);
    private Map<Long, Car> cars = Collections.synchronizedMap(new HashMap<>());
    private AtomicLong idGenerator = new AtomicLong();

    @Override
    public void create(Car car) {
        cars.put(idGenerator.incrementAndGet(), car);
        log.debug("Car created: {}", car);
    }

    @Override
    public List<Car> readAll() {
        return new ArrayList<>(cars.values());
    }

    @Override
    public void update(Car car) {
        Objects.requireNonNull(car.id(), "Car.id cannot be null when updating");
        cars.put(car.id(), car);
        log.debug("Car updated: {}", car);
    }

    @Override
    public void delete(Car car) {
        Objects.requireNonNull(car.id(), "Car.id cannot be null when deleting");
        cars.remove(car.id());
        log.debug("Car deleted: {}", car);
    }
}
