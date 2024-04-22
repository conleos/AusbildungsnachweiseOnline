package com.conleos.views.form;

import com.conleos.common.Role;
import com.conleos.core.Session;
import com.conleos.data.entity.Form;
import com.conleos.data.entity.FormStatus;
import com.conleos.data.entity.User;
import com.conleos.data.service.FormService;
import com.conleos.views.HasHeaderContent;
import com.conleos.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.theme.lumo.LumoUtility.*;

import java.util.ArrayList;
import java.util.List;

@PageTitle("Edit your Form")
@Route(value = "form", layout = MainLayout.class)
public class FormView extends VerticalLayout implements HasUrlParameter<Long>, HasHeaderContent {

    ArrayList<Day> days = new ArrayList<>();

    private Form form;
    private Button saveButton;
    private Button signButton;
    private Button rejectButton;

    public FormView() {

    }

    private void createContent(Form form) {
        this.form = form;

        TabSheet tabSheet = new TabSheet();
        tabSheet.setWidthFull();
        for (int i = 0; i < 5; i++) {
            Day day = new Day(i);
            days.add(day);
            Tab tab = new Tab(VaadinIcon.CALENDAR.create(), new Span(day.getLocalDayName()));
            tab.setTooltipText(day.getDate().toString());
            tabSheet.add(tab, day.createFormContentForDay(form, i));
        }
        CommentView comment = new CommentView(form);
        tabSheet.add(new Tab(VaadinIcon.CHAT.create(), new Span("Chat")), comment.getChatLayout());
        add(tabSheet);

        saveButton = new Button("Save", VaadinIcon.DISC.create());
        saveButton.addClassNames(Margin.AUTO, Margin.Bottom.MEDIUM, Margin.Top.MEDIUM);
        saveButton.addClickListener(save -> {
            List<Form.FormEntry> oldForm = new ArrayList<Form.FormEntry>();
            for (int lo=0; lo<form.getEntries().size() ; lo++){
                oldForm.add(form.getEntries().get(lo));
            }

            form.removeAllEntries();
            for (Day day : days) {
                form.addEntries(day.getEntries(form));
            }
            FormService.getInstance().saveForm(form);

            List<Form.FormEntry> newForm = new ArrayList<Form.FormEntry>();
            for (int lo=0; lo<form.getEntries().size(); lo++){
                newForm.add(form.getEntries().get(lo));
            }

            boolean changed = false;
            if(oldForm.size()==newForm.size()){
                for(int fo=0; fo<oldForm.size(); fo++){
                    if(compareForm(oldForm, newForm, fo)){
                        changed = true;
                    }
                }
            }
            else {
                changed = true;
            }
            if(changed){
                form.setStatus(FormStatus.Rejected);
            }

            FormService.getInstance().saveForm(form);

            UI.getCurrent().getPage().reload();

            // Notify User
            Notification.show("Your current changes have been saved.", 4000, Notification.Position.BOTTOM_START);
        });

        if (form.getStatus().equals(FormStatus.InProgress) || form.getStatus().equals(FormStatus.Rejected)) {
            signButton = new Button("Request Review", VaadinIcon.PENCIL.create());
            signButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            signButton.addClassNames(Margin.AUTO, Margin.Bottom.MEDIUM, Margin.Top.MEDIUM);
            signButton.addClickListener(save -> {
                form.setStatus(FormStatus.InReview);
                FormService.getInstance().saveForm(form);
                UI.getCurrent().getPage().reload();
            });
            signButton.setEnabled(Session.getSessionFromVaadinSession(VaadinSession.getCurrent()).getSessionRole().equals(Role.Trainee));
        } else if (form.getStatus().equals(FormStatus.InReview)) {
            User user = Session.getSessionFromVaadinSession(VaadinSession.getCurrent()).getUser();
            if (user.getRole().equals(Role.Trainee)) {
                signButton = new Button("In Review", VaadinIcon.HOURGLASS.create());
                signButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
                signButton.addClassNames(Margin.AUTO, Margin.Bottom.MEDIUM, Margin.Top.MEDIUM);
                signButton.setEnabled(false);
            } else if (user.getRole().equals(Role.Instructor) || user.getRole().equals(Role.Admin)) {
                signButton = new Button("Sign", VaadinIcon.PENCIL.create());
                signButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
                signButton.addClassNames(Margin.AUTO, Margin.Bottom.MEDIUM, Margin.Top.MEDIUM);
                signButton.addClickListener(save -> {
                    form.setStatus(FormStatus.Signed);
                    FormService.getInstance().saveForm(form);
                    UI.getCurrent().getPage().reload();
                });
                rejectButton = new Button("Reject", VaadinIcon.STOP.create());
                rejectButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
                rejectButton.addClassNames(Margin.AUTO, Margin.Bottom.MEDIUM, Margin.Top.MEDIUM);
                rejectButton.addClickListener(save -> {
                    form.setStatus(FormStatus.Rejected);
                    FormService.getInstance().saveForm(form);
                    UI.getCurrent().getPage().reload();
                });
            }
        } else if (form.getStatus().equals(FormStatus.Signed)) {
            User user = Session.getSessionFromVaadinSession(VaadinSession.getCurrent()).getUser();
            if (user.getRole().equals(Role.Trainee)) {
                signButton = new Button("Signed", VaadinIcon.CHECK.create());
                signButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
                signButton.addClassNames(Margin.AUTO, Margin.Bottom.MEDIUM, Margin.Top.MEDIUM);
            } else if (user.getRole().equals(Role.Instructor) || user.getRole().equals(Role.Admin)) {
                signButton = new Button("Revoke", VaadinIcon.BACKWARDS.create());
                signButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
                signButton.addClassNames(Margin.AUTO, Margin.Bottom.MEDIUM, Margin.Top.MEDIUM);
                signButton.addClickListener(save -> {
                    form.setStatus(FormStatus.Rejected);
                    FormService.getInstance().saveForm(form);
                    UI.getCurrent().getPage().reload();
                });
            }
        }



    }

    /**
     *
     * @param of first list of Form.FormEntry
     * @param nf second list of Form.FormEntry
     * @param current current step in both lists
     * @return false if lists are identical; true if they differ
     */
    private boolean compareForm(List<Form.FormEntry> of, List<Form.FormEntry> nf, int current){
        if(
                of.get(current).getDate().isEqual(nf.get(current).getDate()) &&
                (of.get(current).getBegin().compareTo(nf.get(current).getBegin()) == 0) &&
                (of.get(current).getEnd().compareTo(nf.get(current).getEnd()) == 0) &&
                (of.get(current).getDescription().contentEquals(nf.get(current).getDescription())) &&
                (of.get(current).getKindOfWork().compareTo(nf.get(current).getKindOfWork()) == 0)
        ){
            return false;

        }
        return true;
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
                new DateBasedNavigator(form.getOwner(), form.getMondayDate()),
                saveButton,
                signButton,
                rejectButton != null ? rejectButton : new Span("") // Cannot be NULL
        };
    }
}
