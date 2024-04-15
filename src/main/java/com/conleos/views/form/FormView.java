package com.conleos.views.form;

import com.conleos.core.Session;
import com.conleos.data.entity.Form;
import com.conleos.data.entity.User;
import com.conleos.data.repository.CommentRepository;
import com.conleos.data.service.FormService;
import com.conleos.views.HasHeaderContent;
import com.conleos.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.theme.lumo.LumoUtility.*;

import java.time.LocalDate;
import java.util.ArrayList;

@PageTitle("Edit your Form")
@Route(value = "form", layout = MainLayout.class)
public class FormView extends VerticalLayout implements HasUrlParameter<Long>, HasHeaderContent {

    ArrayList<Day> days = new ArrayList<>();
    Select<Integer> nr = new Select<>();

    private LocalDate dateOfForm;

    public FormView() {

    }

    private void createContent(Form form) {
        dateOfForm = form.getMondayDate();

        TabSheet tabSheet = new TabSheet();
        tabSheet.setWidthFull();
        for (int i = 0; i < 5; i++) {
            Day day = new Day(i);
            days.add(day);
            tabSheet.add(new Tab(VaadinIcon.CALENDAR.create(), new Span(day.getLocalDayName())), day.createFormContentForDay(form, i));
        }
        CommentView comment = new CommentView(form);
        tabSheet.add(new Tab(VaadinIcon.CHAT.create(), new Span("Chat")), comment.getChatLayout());
        add(tabSheet);

        Button saveBtn = new Button("Save");
        saveBtn.addClassNames(Margin.AUTO, Margin.Bottom.MEDIUM, Margin.Top.MEDIUM);
        saveBtn.addClickListener(save -> {
            form.removeAllEntries();
            for (Day day : days) {
                form.addEntries(day.getEntries(form));
            }
            FormService.getInstance().saveForm(form);

            // Notify User
            Notification.show("Your current changes have been saved.", 4000, Notification.Position.BOTTOM_START);
        });


        add(saveBtn);

    }


    @Override
    public void setParameter(BeforeEvent event, Long parameter) {
        Form form = FormService.getInstance().getFormByID(parameter);

        if (form == null) {
            add(new Span("Form not found!"));
            return;
        }

        User user = Session.getSessionFromVaadinSession(VaadinSession.getCurrent()).getUser();

        switch (user.getRole()) {
            case Admin -> {
                createContent(form);
            }
            case Trainee -> {
                if (form.getOwner().getId().equals(user.getId())) {
                    createContent(form);
                } else {
                    add(new Span("access denied!"));
                }
            }
            case Instructor -> {
                if (form.getOwner().getAssignee().getId().equals(user.getId())) {
                    createContent(form);
                } else {
                    add(new Span("access denied!"));
                }
            }
        }


    }

    @Override
    public Component[] createHeaderContent() {
        return new Component[]{
                new DateBasedNavigator(dateOfForm)
        };
    }
}
