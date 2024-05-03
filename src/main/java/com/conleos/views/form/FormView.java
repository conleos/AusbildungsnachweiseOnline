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
            Day day = new Day(form.getMondayDate().plusDays(i));
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
            List<Form.FormEntry> newForm = daysToList(days);
            if (!sameTimeSlot(newForm) && beginAndEndTimeRight(newForm)) {
                List<Form.FormEntry> oldForm = formToList(form);

                form.removeAllEntries();
                for (Day day : days) {
                    form.addEntries(day.getEntries(form));
                }

                if (entriesChanged(oldForm, newForm)) {
                    if (!form.getStatus().equals(FormStatus.InProgress)) {
                        form.setStatus(FormStatus.InReview);
                    }
                    FormService.getInstance().saveForm(form);
                    Notification.show("Your current changes have been saved.", 4000, Notification.Position.BOTTOM_START);
                } else {
                    FormService.getInstance().saveForm(form);
                    Notification.show("No changes occured", 4000, Notification.Position.BOTTOM_START);
                }
            } else if (!beginAndEndTimeRight(newForm)) {
                Notification.show("The end-time cannot be before the begin-time", 4000, Notification.Position.BOTTOM_START);
            } else {
                Notification.show("Some Timeslots are double used", 4000, Notification.Position.BOTTOM_START);
            }
        });

        if (form.getStatus().equals(FormStatus.InProgress) || form.getStatus().equals(FormStatus.Rejected)) {
            signButton = new Button("Request Review", VaadinIcon.PENCIL.create());
            signButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            signButton.addClassNames(Margin.AUTO, Margin.Bottom.MEDIUM, Margin.Top.MEDIUM);
            signButton.addClickListener(save -> {
                saveButton.click();
                form.setStatus(FormStatus.InReview);
                form.setUserWhoSignedOrRejected(null);
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
                    form.setUserWhoSignedOrRejected(user);
                    FormService.getInstance().saveForm(form);
                    UI.getCurrent().getPage().reload();
                });
                rejectButton = new Button("Reject", VaadinIcon.STOP.create());
                createRejectionButton(form, user, rejectButton);
            }
        } else if (form.getStatus().equals(FormStatus.Signed)) {
            User user = Session.getSessionFromVaadinSession(VaadinSession.getCurrent()).getUser();
            if (user.getRole().equals(Role.Trainee)) {
                signButton = new Button("Signed", VaadinIcon.CHECK.create());
                signButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
                signButton.addClassNames(Margin.AUTO, Margin.Bottom.MEDIUM, Margin.Top.MEDIUM);
                signButton.setTooltipText("Signed by " + form.getUserWhoSignedOrRejected().getFullName());
            } else if (user.getRole().equals(Role.Instructor) || user.getRole().equals(Role.Admin)) {
                signButton = new Button("Revoke", VaadinIcon.BACKWARDS.create());
                createRejectionButton(form, user, signButton);
            }
        }

    }

    private static void createRejectionButton(Form form, User user, Button rejectButton) {
        rejectButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        rejectButton.addClassNames(Margin.AUTO, Margin.Bottom.MEDIUM, Margin.Top.MEDIUM);
        rejectButton.addClickListener(save -> {
            form.setStatus(FormStatus.Rejected);
            form.setUserWhoSignedOrRejected(user);
            FormService.getInstance().saveForm(form);
            UI.getCurrent().getPage().reload();
        });
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
            case Admin -> createContent(form);
            case Trainee -> {
                if (form.getOwner().equals(user)) {
                    createContent(form);
                } else {
                    add(new Span("access denied!"));
                }
            }
            case Instructor -> {
                if (form.getOwner().getAssignees().contains(user)) {
                    createContent(form);
                } else {
                    add(new Span("access denied!"));
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + user.getRole());
        }


    }

    @Override
    public Component[] createHeaderContent() {
        ArrayList<Component> content = new ArrayList<>();

        content.add(new DateBasedNavigator(form));

        if (saveButton != null) {
            content.add(saveButton);
        }
        if (signButton != null) {
            content.add(signButton);
        }
        if (rejectButton != null) {
            content.add(rejectButton);
        }

        return content.toArray(new Component[0]);
    }


    //Methods for Save function

    /**
     * @param days
     * @return input list of Day and returns it as a list of FormEntry
     */
    private List<Form.FormEntry> daysToList(List<Day> days) {
        List<Form.FormEntry> ret = new ArrayList<>();
        for (Day day : days) {
            ret.addAll(day.getEntries(form));
        }
        return ret;
    }

    /**
     * @param of first list of Form.FormEntry
     * @param nf second list of Form.FormEntry
     * @return false if lists are identical; true if they differ
     */
    private boolean entriesChanged(List<Form.FormEntry> of, List<Form.FormEntry> nf) {
        boolean changed = false;
        if (of.size() == nf.size()) {
            for (int fo = 0; fo < of.size(); fo++) {
                if (compareForm(of, nf, fo)) {
                    changed = true;
                }
            }
        } else {
            changed = true;
        }
        return changed;
    }

    /**
     * @param form
     * @return input a form and returns it as a list
     */
    private List<Form.FormEntry> formToList(Form form) {
        return new ArrayList<>(form.getEntries());
    }

    /**
     * @param forms
     * @return false if the beginning is after the end or vice versa; true if all the times are set correct
     */
    private boolean beginAndEndTimeRight(List<Form.FormEntry> forms) {
        for (Form.FormEntry formEntry : forms) {
            if (formEntry.getEnd().isBefore(formEntry.getBegin())) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param forms
     * @return true if there are double entries at a certain timeslot; false if not
     */
    private boolean sameTimeSlot(List<Form.FormEntry> forms) {
        for (int i = 0; i < forms.size(); i++) {
            for (int j = i + 1; j < forms.size(); j++) {
                if (forms.get(i).getDate().isEqual(forms.get(j).getDate())) {
                    if (
                            (forms.get(i).getBegin().equals(forms.get(j).getBegin()) || forms.get(i).getEnd().equals(forms.get(j).getEnd())) ||
                                    (forms.get(i).getEnd().isBefore(forms.get(j).getEnd()) && forms.get(i).getEnd().isAfter(forms.get(j).getBegin())) ||
                                    (forms.get(i).getBegin().isAfter(forms.get(j).getBegin()) && forms.get(i).getBegin().isBefore(forms.get(j).getEnd()))
                    ) return true;
                }
            }
        }
        return false;
    }

    /**
     * @param of      first list of Form.FormEntry
     * @param nf      second list of Form.FormEntry
     * @param current current step in both lists
     * @return HELP METHOD; false if lists are identical; true if they differ
     */
    // TODO: Maybe it should return true if they are identical ?
    private boolean compareForm(List<Form.FormEntry> of, List<Form.FormEntry> nf, int current) {
        return !of.get(current).getDate().isEqual(nf.get(current).getDate()) ||
                (!of.get(current).getBegin().equals(nf.get(current).getBegin())) ||
                (!of.get(current).getEnd().equals(nf.get(current).getEnd())) ||
                (!of.get(current).getDescription().contentEquals(nf.get(current).getDescription())) ||
                (of.get(current).getKindOfWork().compareTo(nf.get(current).getKindOfWork()) != 0);
    }
}
