package com.example.application.views.cars;

import com.example.application.data.entity.Car;
import com.example.application.data.entity.CarService;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

import java.util.List;

public class CarForm extends FormLayout {
  private Car car;

  TextField make = new TextField("Марка");
  TextField model = new TextField("Модел");
  TextField registrationNumber = new TextField("Регистрационен номер");
  ComboBox<CarService> carService = new ComboBox<>("Сервиз");
  Binder<Car> binder = new BeanValidationBinder<>(Car.class);

  Button save = new Button("Запази");
  Button delete = new Button("Изтрий");
  Button close = new Button("Откажи");

  public CarForm(List<CarService> carServices) {
    addClassName("car-form");
    binder.bindInstanceFields(this);

    carService.setItems(carServices);
    carService.setItemLabelGenerator(CarService::getName);
    add(make,
        model,
        registrationNumber,
        carService,
        createButtonsLayout());
  }

  private HorizontalLayout createButtonsLayout() {
    save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
    close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

    save.addClickShortcut(Key.ENTER);
    close.addClickShortcut(Key.ESCAPE);

    save.addClickListener(event -> validateAndSave());
    delete.addClickListener(event -> fireEvent(new DeleteEvent(this, car)));
    close.addClickListener(event -> fireEvent(new CloseEvent(this)));

    binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

    return new HorizontalLayout(save, delete, close); 
  }

  public void setCar(Car car) {
    this.car = car;
    binder.readBean(car);
  }

  private void validateAndSave() {
    try {
      binder.writeBean(car);
      fireEvent(new SaveEvent(this, car));
    } catch (ValidationException e) {
      e.printStackTrace();
    }
  }

  // Events
  public static abstract class CarFormEvent extends ComponentEvent<CarForm> {
    private Car car;

    protected CarFormEvent(CarForm source, Car car) {
      super(source, false);
      this.car = car;
    }

    public Car getCar() {
      return car;
    }
  }

  public static class SaveEvent extends CarFormEvent {
    SaveEvent(CarForm source, Car car) {
      super(source, car);
    }
  }

  public static class DeleteEvent extends CarFormEvent {
    DeleteEvent(CarForm source, Car car) {
      super(source, car);
    }

  }

  public static class CloseEvent extends CarFormEvent {
    CloseEvent(CarForm source) {
      super(source, null);
    }
  }

  public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                ComponentEventListener<T> listener) {
    return getEventBus().addListener(eventType, listener);
  }
}