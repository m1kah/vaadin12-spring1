package fi.mika.vaadin.car.view;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.Route;
import fi.mika.vaadin.car.model.Car;
import fi.mika.vaadin.car.model.CarRepository;
import fi.mika.vaadin.car.model.ModifiableCar;
import fi.mika.vaadin.config.Spring;

@Route("")
public class CarView extends VerticalLayout {
    private CarRepository carRepository;
    private Grid<Car> grid;
    private ListDataProvider<Car> dataProvider;
    private CarEditor editor;

    public CarView() {
        carRepository = Spring.bean(CarRepository.class);
        initLayout();
        initData();
    }

    private void initData() {
        dataProvider = DataProvider.ofCollection(carRepository.readAll());
        grid.setDataProvider(dataProvider);
    }

    private void initLayout() {
        setSpacing(true);
        setMargin(true);
        initGrid();
        initEditor();
        Button newButton = new Button("New", this::onNewButtonClick);
        Button deleteButton= new Button("Delete", this::onDeleteButtonClick);
        HorizontalLayout buttonBar = new HorizontalLayout(newButton, deleteButton);
        buttonBar.setSpacing(true);
        add(new HorizontalLayout(new VerticalLayout(buttonBar, grid), editor));
    }

    private void initEditor() {
        editor = new CarEditor();
        editor.setEnabled(false);
    }

    private void initGrid() {
        grid = new Grid<>();
        grid.addColumn(Car::make).setHeader("Make");
        grid.addColumn(Car::model).setHeader("Model");
        grid.addColumn(Car::licenseNumber).setHeader("Lisence number");
        grid.addColumn(Car::price).setHeader("Price");
    }

    private void onNewButtonClick(ClickEvent<Button> buttonClickEvent) {
        editor.edit(ModifiableCar.create(), this::onSaveNew);
    }

    private void onSaveNew(ModifiableCar modifiableCar) {
        carRepository.create(modifiableCar);
        reloadData();
        grid.focus();
    }

    private void reloadData() {
        dataProvider = DataProvider.ofCollection(carRepository.readAll());
        grid.setDataProvider(dataProvider);
    }

    private void onDeleteButtonClick(ClickEvent<Button> buttonClickEvent) {
        for (Car selectedItem : grid.getSelectedItems()) {
            carRepository.delete(selectedItem);
        }
        reloadData();
    }

}
