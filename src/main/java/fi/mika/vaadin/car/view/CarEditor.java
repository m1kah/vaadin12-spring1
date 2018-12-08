package fi.mika.vaadin.car.view;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import fi.mika.vaadin.car.model.ModifiableCar;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.function.Consumer;

public class CarEditor extends VerticalLayout {
    Binder<ModifiableCar> binder;
    private TextField makeField;
    private TextField modelField;
    private TextField licenseNumberField;
    private DatePicker firstRegistrationField;
    private DatePicker receivedField;
    private TextField priceField;
    private Consumer<ModifiableCar> saveListener;

    public CarEditor() {
        initLayout();
        initBinder();
    }

    private void initLayout() {
        setMargin(true);
        makeField = new TextField("Make");
        makeField.setRequired(true);
        modelField = new TextField("Model");
        modelField.setRequired(true);
        licenseNumberField = new TextField("License number");
        licenseNumberField.setRequired(true);
        firstRegistrationField = new DatePicker("First registered");
        priceField = new TextField("Price");

        Button saveButton = new Button("Save", this::onSaveButtonClick);
        Button cancelButton = new Button("Cancel", this::onCancelButtonClick);
        HorizontalLayout buttonBar = new HorizontalLayout(saveButton, cancelButton);
        buttonBar.setMargin(true);

        add(new FormLayout(
                makeField,
                modelField,
                licenseNumberField,
                firstRegistrationField,
                priceField,
                buttonBar));
    }

    private void onSaveButtonClick(ClickEvent<Button> buttonClickEvent) {
        ModifiableCar editedValue = binder.getBean();
        boolean saved = binder.writeBeanIfValid(editedValue);
        if (saved) {
            if (saveListener != null) {
                saveListener.accept(editedValue);
            }
            cleanAndDisable();
        } else {
            Notification.show("Errors must be fixed before saving.");
        }
    }

    private void onCancelButtonClick(ClickEvent<Button> buttonClickEvent) {
        cleanAndDisable();
    }

    private void cleanAndDisable() {
        binder.removeBean();
        setEnabled(false);
    }

    private void initBinder() {
        binder = new Binder<>(ModifiableCar.class);
        binder.forField(makeField)
                .withValidator(
                        make -> make != null && make.length() >= 3,
                        "Make must be given. Min length is 3.")
                .bind(ModifiableCar::make, ModifiableCar::setMake);
        binder.forField(modelField)
                .withValidator(
                        model -> model != null && model.length() >= 1,
                        "Model must be given. Min length is 1.")
                .bind(ModifiableCar::model, ModifiableCar::setModel);
        binder.forField(licenseNumberField)
                .withConverter(new CapitalizingConverter())
                .withValidator(
                        licenseNumber -> licenseNumber != null && licenseNumber.matches("[A-Za-z]{3}-[0-9]{3}"),
                        "License number must be in format: ABC-123")
                .bind(ModifiableCar::licenseNumber, ModifiableCar::setLicenseNumber);
        binder.forField(firstRegistrationField)
                .withValidator(
                        date -> date == null || !date.isAfter(LocalDate.now()),
                        "Date cannot be in future")
                .bind(ModifiableCar::firstRegistration, ModifiableCar::setFirstRegistration);
        binder.forField(priceField)
                .withConverter(new PriceConverter())
                .withValidator(
                        price -> price == null || price.compareTo(BigDecimal.ZERO) > 0,
                        "Price must be greater than 0.00"
                )
                .bind(ModifiableCar::price, ModifiableCar::setPrice);
    }

    public void edit(ModifiableCar value, Consumer<ModifiableCar> saveListener) {
        binder.setBean(value);
        setEnabled(true);
        this.saveListener = saveListener;
        makeField.focus();
    }
}
