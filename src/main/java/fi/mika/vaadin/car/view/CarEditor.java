package fi.mika.vaadin.car.view;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import fi.mika.vaadin.car.model.ModifiableCar;

import java.util.function.Consumer;

public class CarEditor extends FormLayout {
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
        makeField = new TextField("Make");
        modelField = new TextField("Model");
        licenseNumberField = new TextField("License number");
        firstRegistrationField = new DatePicker("First registered");
        receivedField = new DatePicker("Received");
        priceField = new TextField("Price");

        Button saveButton = new Button("Save", this::onSaveButtonClick);
        Button cancelButton = new Button("Cancel", this::onCancelButtonClick);
        HorizontalLayout buttonBar = new HorizontalLayout(saveButton, cancelButton);
        buttonBar.setMargin(true);

        add(
                makeField,
                modelField,
                licenseNumberField,
                firstRegistrationField,
                receivedField,
                priceField,
                buttonBar);
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
        binder.forField(makeField).bind(ModifiableCar::make, ModifiableCar::setMake);
        binder.forField(modelField).bind(ModifiableCar::model, ModifiableCar::setModel);
        binder.forField(licenseNumberField).bind(ModifiableCar::licenseNumber, ModifiableCar::setLicenseNumber);
        binder.forField(firstRegistrationField).bind(ModifiableCar::firstRegistration, ModifiableCar::setFirstRegistration);
        //binder.forField(receivedField).bind(CarViewModel::getReceived, CarViewModel::setReceived);
        //binder.forField(priceField).bind(CarViewModel::getPrice, CarViewModel::setPrice);
    }

    public void edit(ModifiableCar value, Consumer<ModifiableCar> saveListener) {
        binder.setBean(value);
        setEnabled(true);
        this.saveListener = saveListener;
        makeField.focus();
    }
}
