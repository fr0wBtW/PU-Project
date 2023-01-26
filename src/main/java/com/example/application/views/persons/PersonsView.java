package com.example.application.views.persons;

import com.example.application.data.entity.Person;
import com.example.application.data.service.CrmService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.vaadin.flow.component.button.Button;
import javax.annotation.security.PermitAll;

@Component
@Scope("prototype")
@Route(value="", layout = MainLayout.class)
@PageTitle("Обща информация")
@PermitAll
public class PersonsView extends VerticalLayout {
    Grid<Person> grid = new Grid<>(Person.class);
    TextField filterText = new TextField();
    TextField filterTextByPhone = new TextField();
    TextField filterTextByCar = new TextField();
    PersonForm form;
    CrmService service;

    public PersonsView(CrmService service) {
        this.service = service;
        addClassName("persons-view");
        setSizeFull();
        configureGrid();

        form = new PersonForm(service.findAllCars());
        form.setWidth("25em");
        form.addListener(PersonForm.SaveEvent.class, this::savePerson);
        form.addListener(PersonForm.DeleteEvent.class, this::deletePerson);
        form.addListener(PersonForm.CloseEvent.class, e -> closeEditor());

        FlexLayout content = new FlexLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.setFlexShrink(0, form);
        content.addClassNames("content", "gap-m");
        content.setSizeFull();

        add(getToolbar(), content);
        updateList();
        updateListByPhoneNumber();
        updateListByCar();
        closeEditor();
        grid.asSingleSelect().addValueChangeListener(event ->
            editPerson(event.getValue()));
    }

    private void configureGrid() {
        grid.addClassNames("person-grid");
        grid.setSizeFull();
        grid.setColumns("firstName", "lastName", "phoneNumber");
        grid.addColumn(p -> p.getCar().getMake()).setHeader("Марка");
        grid.addColumn(p -> p.getCar().getRegistrationNumber()).setHeader("Регистрационен номер");
        grid.addColumn(p -> p.getCar().getCarService().getName()).setHeader("Сервиз");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Филтрирай по име на собственик...");
        filterText.setClearButtonVisible(true);
        filterText.setWidth("400px");
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        filterTextByPhone.setPlaceholder("Филтрирай по име телефон...");
        filterTextByPhone.setClearButtonVisible(true);
        filterTextByPhone.setWidth("400px");
        filterTextByPhone.setValueChangeMode(ValueChangeMode.LAZY);
        filterTextByPhone.addValueChangeListener(e -> updateListByPhoneNumber());

        filterTextByCar.setPlaceholder("Филтрирай по марка на автомобил...");
        filterTextByCar.setClearButtonVisible(true);
        filterTextByCar.setWidth("400px");
        filterTextByCar.setValueChangeMode(ValueChangeMode.LAZY);
        filterTextByCar.addValueChangeListener(e -> updateListByCar());

        Button addPersonButton = new Button("Добави собственик");
        addPersonButton.addClickListener(click -> addPerson());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, filterTextByPhone, addPersonButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void savePerson(PersonForm.SaveEvent event) {
        service.savePerson(event.getPerson());
        updateList();
        closeEditor();
    }

    private void deletePerson(PersonForm.DeleteEvent event) {
        var temp = event.getPerson().toString();

        service.deletePerson(event.getPerson());
        updateList();
        closeEditor();

        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

        Div text = new Div(new Text(String.format(temp, "Успешно изтрихте %temp")));

        Button closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getElement().setAttribute("aria-label", "Close");
        closeButton.addClickListener(e -> {
            notification.close();
        });

        HorizontalLayout layout = new HorizontalLayout(text, closeButton);
        layout.setAlignItems(Alignment.CENTER);

        notification.add(layout);
        notification.open();
    }

    public void editPerson(Person person) {
        if (person == null) {
            closeEditor();
        } else {
            form.setPerson(person);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setPerson(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void updateList() {
        grid.setItems(service.findAllPersons(filterText.getValue()));
    }

    private void updateListByPhoneNumber() {
        grid.setItems(service.findAllPersonsByPhoneNumber(filterTextByPhone.getValue()));
    }

    private void updateListByCar() {
        grid.setItems(service.findAllPersonsByCar(filterTextByCar.getValue()));
    }

    void addPerson() {
        grid.asSingleSelect().clear();
        editPerson(new Person());
    }
}
