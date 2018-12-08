package fi.mika.vaadin.car.model;

import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Value.Modifiable
@Value.Immutable
public interface Car {
    @Nullable
    Long id();
    @Nullable
    String make();
    @Nullable
    String model();
    @Nullable
    String licenseNumber();
    @Nullable
    LocalDate firstRegistration();
    @Nullable
    LocalDateTime received();
    @Nullable
    BigDecimal price();
}
