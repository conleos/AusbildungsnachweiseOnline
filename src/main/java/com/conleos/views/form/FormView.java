package com.conleos.views.form;

import com.conleos.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.cglib.core.Local;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;

@PageTitle("Edit your Form")
@Route(value = "form", layout = MainLayout.class)
public class FormView extends VerticalLayout {

    public FormView() {
        createContent();
    }

    private void createContent() {
        LocalDate beginOfWeek = LocalDate.now().with(DayOfWeek.MONDAY);
        for (int i = 0; i < 5; i++) {
            createFormContentForDay(beginOfWeek.plusDays(i));
        }
    }

    private void createFormContentForDay(LocalDate date) {
        String dayLabel = date.getDayOfWeek().getDisplayName(TextStyle.FULL, UI.getCurrent().getLocale());

        VerticalLayout container = new VerticalLayout();

        Button addBtn = new Button("Add", VaadinIcon.PLUS.create());

        addBtn.addClickListener(event -> {
            Select<KindOfWork> select = new Select<>();
            select.setLabel("Art");
            select.setItems(KindOfWork.PracticalWork, KindOfWork.Schooling);
            select.setValue(KindOfWork.PracticalWork);
            TextArea time = new TextArea("Zeit");
            time.setWidth("60px");
            time.setHeight("60px");
            TextArea area = new TextArea("Beschreibung");
            Button delBtn = new Button(VaadinIcon.CLOSE.create());
            delBtn.addClickListener(eventDel -> {
                Component parent = delBtn.getParent().get();
                if (parent instanceof HorizontalLayout) {
                    container.remove(parent);
                }
            });
            container.add(new HorizontalLayout(select, time, area,delBtn));
        });

        add(new Span(dayLabel), container, addBtn);
    }

}
