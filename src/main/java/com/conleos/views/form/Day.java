package com.conleos.views.form;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;

public class Day {

    List<TextArea> descriptions = new ArrayList<>();
    List<TextArea> timeSumList = new ArrayList<>();
    LocalDate date;
    double timeSumDay = 0;
    public Day (int i) {
        LocalDate beginOfWeek = LocalDate.now().with(DayOfWeek.MONDAY);
        this.date = beginOfWeek.plusDays(i);
    }

    public VerticalLayout createFormContentForDay() {
        String dayLabel = date.getDayOfWeek().getDisplayName(TextStyle.FULL, UI.getCurrent().getLocale());

        VerticalLayout container = new VerticalLayout();
        container.setWidthFull();

        Button addBtn = new Button("Add", VaadinIcon.PLUS.create());
        VerticalLayout day = new VerticalLayout();
        day.setClassName(LumoUtility.Border.ALL);
        TextField timeSum = new TextField();
        timeSum.setLabel("Zeit gesamt:");

        addBtn.addClickListener(event -> {
            Select<KindOfWork> select = new Select<>();
            select.setLabel("Art");
            select.setItems(KindOfWork.values());
            select.setValue(KindOfWork.PracticalWork);

            TimePicker timeBegin = new TimePicker("Von -");
            timeBegin.setValue(LocalTime.of(8, 0));
            timeBegin.addValueChangeListener(timeChange -> {

            });
            TimePicker timeEnd = new TimePicker("- Bis");
            timeEnd.setValue(LocalTime.of(16, 0));
            timeEnd.addValueChangeListener(timeChange -> {

            });
            TextArea area = new TextArea("Beschreibung");
            area.setWidthFull();
            descriptions.add(area);
            Button delBtn = new Button(VaadinIcon.CLOSE.create());
            delBtn.addClickListener(eventDel -> {
                Component parent = delBtn.getParent().get();
                if (parent instanceof HorizontalLayout) {
                    container.remove(parent);
                }
            });
            VerticalLayout block = new VerticalLayout(select, timeBegin, timeEnd);
            block.setWidth("225px");
            HorizontalLayout layout = new HorizontalLayout(block, area, delBtn);
            layout.setWidthFull();
            container.add(layout);
        });

        day.add(new Span(dayLabel), container, addBtn, timeSum);
        return day;
    }

}
