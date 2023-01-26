package com.example.application.views.carServices;

import com.example.application.data.entity.CarService;
import com.example.application.data.service.CrmService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.security.PermitAll;

@Component
@Scope("prototype")
@Route(value="services", layout = MainLayout.class)
@PageTitle("Сервизи")
@PermitAll
public class CarServicesView extends VerticalLayout {
    Grid<CarService> grid = new Grid<>(CarService.class);
    TextField filterText = new TextField();
    CarServiceForm form;
    CrmService service;

    public CarServicesView(CrmService service) {
        this.service = service;
        addClassName("carServices-view");
        setSizeFull();
        configureGrid();

        form = new CarServiceForm();
        form.setWidth("25em");
        form.addListener(CarServiceForm.SaveEvent.class, this::saveCarService);
        form.addListener(CarServiceForm.DeleteEvent.class, this::deleteCarService);
        form.addListener(CarServiceForm.CloseEvent.class, e -> closeEditor());

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
            editCarService(event.getValue()));
    }

    private void configureGrid() {
        grid.addClassNames("carService-grid");
        grid.setSizeFull();
        grid.setColumns("name", "address");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private HorizontalLayout getToolbar() {
        Button addCarButton = new Button("Добави Сервиз");
        addCarButton.addClickListener(click -> addCar());

        HorizontalLayout toolbar = new HorizontalLayout(addCarButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void saveCarService(CarServiceForm.SaveEvent event) {
        service.saveCarService(event.getCarService());
        updateList();
        closeEditor();
    }

    public void deleteCarService(CarServiceForm.DeleteEvent event) {
            service.deleteCarService(event.getCarService());
            updateList();
            closeEditor();
    }

    public void editCarService(CarService carService) {
        if (carService == null) {
            closeEditor();
        } else {
            form.setCarService(carService);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setCarService(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void updateList() {
        grid.setItems(service.findAllCarServices());
    }

    void addCar() {
        grid.asSingleSelect().clear();
        editCarService(new CarService());
    }
}
