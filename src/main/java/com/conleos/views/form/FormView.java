package com.conleos.views.form;

import com.conleos.common.Role;
import com.conleos.core.Session;
import com.conleos.data.entity.Form;
import com.conleos.data.entity.FormStatus;
import com.conleos.data.entity.User;
import com.conleos.data.service.FormService;
import com.conleos.i18n.TranslationProvider;
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

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@PageTitle("view.form.pageTitle")
@Route(value = "form", layout = MainLayout.class)
public class FormView extends VerticalLayout implements HasUrlParameter<Long>, HasHeaderContent {

    ArrayList<Day> days = new ArrayList<>();

    private Form form;
    private Button saveButton;
    private Button signButton;
    private Button rejectButton;
    private static TranslationProvider translationProvider;
    private Locale locale = UI.getCurrent().getLocale();

    public FormView(TranslationProvider translationProvider) {
        this.translationProvider = translationProvider;

    }

    private void createContent(Form form) {
        this.form = form;

        Span weekInfo = new Span("Woche ab Montag, dem " + DateTimeFormatter.ofPattern("dd. MMMM uuuu", Locale.GERMAN).format(form.getMondayDate()));
        add(weekInfo);

        TabSheet tabSheet = new TabSheet();
        tabSheet.setWidthFull();

        if (Session.getSessionFromVaadinSession(VaadinSession.getCurrent()).getSessionRole().equals(Role.Trainee)) {
            for (int i = 0; i < 7; i++) {
                Day day = new Day(form.getMondayDate().plusDays(i));
                days.add(day);
                Tab tab = new Tab(VaadinIcon.CALENDAR.create(), new Span(day.getLocalDayName()));
                tab.setTooltipText(day.getDate().toString());
                tabSheet.add(tab, day.createFormContentForDay(form, i));
            }
        } else {
            tabSheet.add(new Tab(VaadinIcon.CALENDAR.create(), new Span("Übersicht")), FormOverview.createContent(form));
        }

        CommentView comment = new CommentView(form);
        tabSheet.add(new Tab(VaadinIcon.CHAT.create(), new Span(translationProvider.getTranslation("view.form.tab.span.label", locale))), comment.getChatLayout());
        add(tabSheet);

        saveButton = new Button(translationProvider.getTranslation("view.form.button.save", locale), VaadinIcon.DISC.create());
        saveButton.addClassNames(Margin.AUTO, Margin.Bottom.MEDIUM, Margin.Top.MEDIUM);
        saveButton.addClickListener(save -> {
            form.setMonday(days.get(0).getEntry(form));
            form.setTuesday(days.get(1).getEntry(form));
            form.setWednesday(days.get(2).getEntry(form));
            form.setThursday(days.get(3).getEntry(form));
            form.setFriday(days.get(4).getEntry(form));
            form.setSaturday(days.get(5).getEntry(form));
            form.setSunday(days.get(6).getEntry(form));
            form.setStatus(FormStatus.InProgress);
            FormService.getInstance().saveForm(form);
            Notification.show(translationProvider.getTranslation("view.form.button.save.notification", locale), 4000, Notification.Position.BOTTOM_START);
        });

        if (form.getStatus().equals(FormStatus.InProgress) || form.getStatus().equals(FormStatus.Rejected)) {
            signButton = new Button(translationProvider.getTranslation("view.form.button.sign", locale), VaadinIcon.PENCIL.create());
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
                signButton = new Button(translationProvider.getTranslation("view.form.signButton.review", locale), VaadinIcon.HOURGLASS.create());
                signButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
                signButton.addClassNames(Margin.AUTO, Margin.Bottom.MEDIUM, Margin.Top.MEDIUM);
                signButton.setEnabled(false);
            } else if (user.getRole().equals(Role.Instructor) || user.getRole().equals(Role.Admin)) {
                signButton = new Button(translationProvider.getTranslation("view.form.signButton.sign", locale), VaadinIcon.PENCIL.create());
                signButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
                signButton.addClassNames(Margin.AUTO, Margin.Bottom.MEDIUM, Margin.Top.MEDIUM);
                signButton.addClickListener(save -> {
                    form.setStatus(FormStatus.Signed);
                    form.setUserWhoSignedOrRejected(user);
                    FormService.getInstance().saveForm(form);
                    UI.getCurrent().getPage().reload();
                });
                rejectButton = new Button(translationProvider.getTranslation("view.form.signButton.reject", locale), VaadinIcon.STOP.create());
                createRejectionButton(form, user, rejectButton);
            }
        } else if (form.getStatus().equals(FormStatus.Signed)) {
            User user = Session.getSessionFromVaadinSession(VaadinSession.getCurrent()).getUser();
            if (user.getRole().equals(Role.Trainee)) {
                signButton = new Button(translationProvider.getTranslation("view.form.signButton.signed", locale), VaadinIcon.CHECK.create());
                signButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
                signButton.addClassNames(Margin.AUTO, Margin.Bottom.MEDIUM, Margin.Top.MEDIUM);
                User userWhoSignedOrRejected = form.getUserWhoSignedOrRejected();
                signButton.setTooltipText("Signed by " + (userWhoSignedOrRejected != null ? userWhoSignedOrRejected.getFullName() : "Unkown"));
                signButton.setTooltipText(translationProvider.getTranslation("view.form.signButton.signed.toolTip", locale) + form.getUserWhoSignedOrRejected().getFullName());
            } else if (user.getRole().equals(Role.Instructor) || user.getRole().equals(Role.Admin)) {
                signButton = new Button(translationProvider.getTranslation("view.form.signButton.revoke", locale), VaadinIcon.BACKWARDS.create());
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

        if (saveButton != null && Session.getSessionFromVaadinSession(VaadinSession.getCurrent()).getSessionRole().equals(Role.Trainee)) {
            content.add(saveButton);
        }
        if (signButton != null) {
            content.add(signButton);
        }
        if (rejectButton != null) {
            content.add(rejectButton);
        }

        if (!Session.getSessionFromVaadinSession(VaadinSession.getCurrent()).getSessionRole().equals(Role.Trainee)) {
            content.add(new InstructorFormNextButton(form));
        }

        return content.toArray(new Component[0]);
    }

}
