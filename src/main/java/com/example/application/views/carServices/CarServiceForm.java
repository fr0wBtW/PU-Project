package com.example.application.views.carServices;

import com.example.application.data.entity.CarService;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

public class CarServiceForm extends FormLayout {
  private CarService carService;

  TextField name = new TextField("Име");
  TextField address = new TextField("Адрес");
  Binder<CarService> binder = new BeanValidationBinder<>(CarService.class);

  Button save = new Button("Запази");
  Button delete = new Button("Изтрий");
  Button close = new Button("Откажи");

  public CarServiceForm() {
    addClassName("carService-form");
    binder.bindInstanceFields(this);

    add(name,
        address,
        createButtonsLayout());
  }

  private HorizontalLayout createButtonsLayout() {
    save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
    close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

    save.addClickShortcut(Key.ENTER);
    close.addClickShortcut(Key.ESCAPE);

    save.addClickListener(event -> validateAndSave());
    delete.addClickListener(event -> fireEvent(new DeleteEvent(this, carService)));
    close.addClickListener(event -> fireEvent(new CloseEvent(this)));

    binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

    return new HorizontalLayout(save, delete, close); 
  }

  public void setCarService(CarService carService) {
    this.carService = carService;
    binder.readBean(carService);
  }

  private void validateAndSave() {
    try {
      binder.writeBean(carService);
      fireEvent(new SaveEvent(this, carService));
    } catch (ValidationException e) {
      e.printStackTrace();
    }
  }

  // Events
  public static abstract class CarServiceFormEvent extends ComponentEvent<CarServiceForm> {
    private CarService carService;

    protected CarServiceFormEvent(CarServiceForm source, CarService carService) {
      super(source, false);
      this.carService = carService;
    }

    public CarService getCarService() {
      return carService;
    }
  }

  public static class SaveEvent extends CarServiceFormEvent {
    SaveEvent(CarServiceForm source, CarService carService) {
      super(source, carService);
    }
  }

  public static class DeleteEvent extends CarServiceFormEvent {
    DeleteEvent(CarServiceForm source, CarService carService) {
      super(source, carService);
    }
  }

  public static class CloseEvent extends CarServiceFormEvent {
    CloseEvent(CarServiceForm source) {
      super(source, null);
    }
  }

  public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                ComponentEventListener<T> listener) {
    return getEventBus().addListener(eventType, listener);
  }
}