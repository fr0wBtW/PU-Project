package com.example.application.views.cars;

import com.example.application.data.entity.Car;
import com.example.application.data.service.CrmService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.security.PermitAll;

@Component
@Scope("prototype")
@Route(value="cars", layout = MainLayout.class)
@PageTitle("Автомобили")
@PermitAll
public class CarsView extends VerticalLayout {
    Grid<Car> grid = new Grid<>(Car.class);
    TextField filterText = new TextField();
    CarForm form;
    CrmService service;

    public CarsView(CrmService service) {
        this.service = service;
        addClassName("cars-view");
        setSizeFull();
        configureGrid();

        form = new CarForm(service.findAllCarServices());
        form.setWidth("25em");
        form.addListener(CarForm.SaveEvent.class, this::saveCar);
        form.addListener(CarForm.DeleteEvent.class, this::deleteCar);
        form.addListener(CarForm.CloseEvent.class, e -> closeEditor());

        FlexLayout content = new FlexLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.setFlexShrink(0, form);
        content.addClassNames("content", "gap-m");
        content.setSizeFull();

        add(getToolbar(), content);
        updateList();
        closeEditor();
        grid.asSingleSelect().addValueChangeListener(event ->
            editCar(event.getValue()));
    }

    private void configureGrid() {
        grid.addClassNames("car-grid");
        grid.setSizeFull();
        grid.setColumns("make", "model", "registrationNumber");
        grid.addColumn(p -> p.getCarService().getName()).setHeader("Име");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Филтрирай по марка или модел...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addCarButton = new Button("Добави автомобил");
        addCarButton.addClickListener(click -> addCar());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addCarButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void saveCar(CarForm.SaveEvent event) {
        service.saveCar(event.getCar());
        updateList();
        closeEditor();
    }

    private void deleteCar(CarForm.DeleteEvent event) {
        service.deleteCar(event.getCar());
        updateList();
        closeEditor();
    }

    public void editCar(Car car) {
        if (car == null) {
            closeEditor();
        } else {
            form.setCar(car);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setCar(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void updateList() {
        grid.setItems(service.findAllCars(filterText.getValue()));
    }

    void addCar() {
        grid.asSingleSelect().clear();
        editCar(new Car());
    }
}
