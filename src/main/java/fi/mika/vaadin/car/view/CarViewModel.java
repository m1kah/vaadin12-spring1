package fi.mika.vaadin.car.view;

import org.immutables.value.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Value.Modifiable
public interface CarViewModel {
    Long id();
    String make();
    String model();
    String licenseNumber();
    LocalDate firstRegistration();
    LocalDateTime received();
    BigDecimal price();
}
