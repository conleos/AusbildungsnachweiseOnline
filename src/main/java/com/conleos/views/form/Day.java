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
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.time.DayOfWeek;
import java.time.LocalDate;
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

        Button addBtn = new Button("Add", VaadinIcon.PLUS.create());
        VerticalLayout day = new VerticalLayout();
        day.setClassName(LumoUtility.Border.ALL);
        TextField timeSum = new TextField();
        timeSum.setLabel("Zeit gesamt:");

        addBtn.addClickListener(event -> {
            Select<KindOfWork> select = new Select<>();
            select.setLabel("Art");
            select.setItems(KindOfWork.PracticalWork, KindOfWork.Schooling);
            select.setValue(KindOfWork.PracticalWork);
            TextArea time = new TextArea("Zeit");
            timeSumList.add(time);
            time.setWidth("60px");
            time.setHeight("60px");
            time.addValueChangeListener(timeChange -> {
                timeSumDay = 0;
                for (TextArea t : timeSumList) {
                    System.out.println(t.getValue());
                    System.out.println(timeSumDay);
                    timeSumDay += Double.parseDouble(t.getValue());


                }
                timeSum.setValue(String.valueOf(timeSumDay));

            });
            TextArea area = new TextArea("Beschreibung");
            descriptions.add(area);
            Button delBtn = new Button(VaadinIcon.CLOSE.create());
            delBtn.addClickListener(eventDel -> {
                Component parent = delBtn.getParent().get();
                if (parent instanceof HorizontalLayout) {
                    container.remove(parent);
                }
            });
            container.add(new HorizontalLayout(select, time, area,delBtn));
        });

        day.add(new Span(dayLabel), container, addBtn, timeSum);
        return day;
    }

}
