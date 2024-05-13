package com.conleos.views.admin;

import com.conleos.common.ColorGenerator;
import com.conleos.common.HtmlColor;
import com.conleos.common.Role;
import com.conleos.core.Session;
import com.conleos.data.entity.User;
import com.conleos.data.service.UserService;
import com.conleos.views.HasHeaderContent;
import com.conleos.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@PageTitle("Admin")
@Route(value = "admin", layout = MainLayout.class)
@RouteAlias(value = "manage", layout = MainLayout.class)
public class AdminView extends VerticalLayout implements HasHeaderContent {

    private CreateUserDialog createUserDialog;
    private Long gridUserId;
    private String gridUserRole;
    private Locale locale = UI.getCurrent().getLocale();
    private final CreateErrorNotification otherAdminPasswordError = new CreateErrorNotification(getTranslation("view.admin.error.password", locale));
    private final CreateErrorNotification noUserChosenNotification = new CreateErrorNotification(getTranslation("view.admin.error.user", locale));


    public AdminView(UserService service) {
        addClassName("data-grid-view");
        setSizeFull();
        Session session = Session.getSessionFromVaadinSession(VaadinSession.getCurrent());
        if (!session.getSessionRole().equals(Role.Admin)) {
            add(new Span(getTranslation("view.admin.span.open", locale)));
            return;
        }
        createUserDialog = new CreateUserDialog();
        add(createUserDialog);

        List<User> users = service.getAllUsers();

        // https://vaadin.com/docs/latest/components/grid
        Grid<User> grid = new Grid<>(User.class, false);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_COLUMN_BORDERS);
        grid.setHeight("100%");
        grid.addColumn(createInfoRenderer()).setHeader(getTranslation("view.admin.grid.info", locale))
                .setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(User::getUsername).setHeader(getTranslation("view.admin.grid.username", locale))
                .setAutoWidth(true);
        /*grid.addColumn(User::getPasswordHash).setHeader("Password Hash")
                .setAutoWidth(true);*/
        grid.addColumn(createStatusComponentRenderer()).setHeader(getTranslation("view.admin.grid.role", locale))
                .setAutoWidth(true);
        grid.addColumn(createAssigneeComponentRenderer()).setHeader(getTranslation("view.admin.grid.assignedTo", locale))
                .setAutoWidth(true);
        grid.addColumn(createStartDateComponentRenderer()).setHeader(getTranslation("view.admin.grid.beginOfWork", locale))
                .setAutoWidth(true);
        grid.setItems(users);
        add(grid);
        grid.addSelectionListener(selection -> {
            noUserChosenNotification.close();
            otherAdminPasswordError.close();
            Optional<User> optionalUser = selection.getFirstSelectedItem();
            if (optionalUser.isPresent()) {
                gridUserId = optionalUser.get().getId();
                gridUserRole = optionalUser.get().getRole().toString();
            } else {
                gridUserId = null;
                gridUserRole = null;
            }
        });


    }

    private static Renderer<User> createInfoRenderer() {
        return LitRenderer.<User>of(
                        "<vaadin-horizontal-layout style=\"align-items: center;\" theme=\"spacing\">"
                                + "<vaadin-avatar img=\"${item.pictureUrl}\" name=\"${item.fullName}\" alt=\"User avatar\" style=\"background-color: ${item.color};\"></vaadin-avatar>"
                                + "  <vaadin-vertical-layout style=\"line-height: var(--lumo-line-height-m);\">"
                                + "    <span> ${item.fullName} </span>"
                                + "    <span style=\"font-size: var(--lumo-font-size-s); color: var(--lumo-secondary-text-color);\">"
                                + "      ${item.email}" + "    </span>"
                                + "  </vaadin-vertical-layout>"
                                + "</vaadin-horizontal-layout>")
                .withProperty("pictureUrl", AdminView::getPictureURL)
                .withProperty("fullName", User::getFullName)
                .withProperty("email", User::getEmail)
                .withProperty("color", AdminView::getUserColor);
    }

    private static Object getPictureURL(User user) {
        return "";
    }

    private static Object getUserColor(User user) {
        return HtmlColor.from(ColorGenerator.fromRandomString(user.getUsername())).toString();
    }

    private static final SerializableBiConsumer<Span, User> statusComponentUpdater = (span, user) -> {
        switch (user.getRole()) {
            case Admin -> span.getElement().setAttribute("theme", "badge error");
            case Instructor -> span.getElement().setAttribute("theme", "badge success");
            case Trainee -> span.getElement().setAttribute("theme", "badge contrast");
        }
        span.setText(user.getRole().toString());
    };

    private static ComponentRenderer<Span, User> createStatusComponentRenderer() {
        return new ComponentRenderer<>(Span::new, statusComponentUpdater);
    }

    private static final SerializableBiConsumer<MultiSelectComboBox<Long>, User> assigneeComponentUpdater = (comboBox, user) -> {
        List<Long> assignees = UserService.getInstance().getAllUsers().stream().filter(it -> !it.getRole().equals(Role.Trainee)).map(User::getId).toList();

        comboBox.setEnabled(user.getRole().equals(Role.Trainee));
        comboBox.setItems(assignees);
        comboBox.setItemLabelGenerator(item -> UserService.getInstance().getUserByID(item).getUsername());
        comboBox.setValue(user.getAssigneeIds());
        comboBox.addValueChangeListener(event -> {
            user.setAssigneeIds(new ArrayList<>(event.getValue()));
            UserService.getInstance().saveUser(user);
        });
    };

    private static ComponentRenderer<MultiSelectComboBox<Long>, User> createAssigneeComponentRenderer() {
        return new ComponentRenderer<>(MultiSelectComboBox::new, assigneeComponentUpdater);
    }

    private static final SerializableBiConsumer<DatePicker, User> startDateComponentUpdater = (datePicker, user) -> {
        datePicker.setEnabled(user.getRole().equals(Role.Trainee));
        datePicker.setValue(user.getStartDate());
        datePicker.addValueChangeListener(event -> {
            user.setStartDate(event.getValue());
            UserService.getInstance().saveUser(user);
        });
    };

    private static ComponentRenderer<DatePicker, User> createStartDateComponentRenderer() {
        return new ComponentRenderer<>(DatePicker::new, startDateComponentUpdater);
    }

    @Override
    public Component[] createHeaderContent() {
        Component[] headerComponents = new Component[3];

        Button newUser = new Button("Add a new User", e -> createUserDialog.open());
        Button changePassword = new Button("Change Password", VaadinIcon.PENCIL.create());
        changePassword.addClickListener(event -> {
            if (gridUserId != null && !gridUserRole.equals("Admin")) {
                AdminAccessDialog access = new AdminAccessDialog(gridUserId, new AdminChangePasswordDialog(gridUserId));
                access.open();
            } else if (gridUserId != null && gridUserRole.equals("Admin")) {
                otherAdminPasswordError.open();
            } else {
                noUserChosenNotification.open();
            }
        });

        Button deleteAccount = new Button("Delete Account", VaadinIcon.TRASH.create());
        deleteAccount.addClickListener(event -> {
            if (gridUserId != null) {
                if (!Session.getSessionFromVaadinSession(VaadinSession.getCurrent()).getUser().getId().equals(gridUserId)) {
                    AdminAccessDialog access = new AdminAccessDialog(gridUserId, new DeleteAccountDialog(gridUserId));
                    access.open();
                } else {
                    Notification.show("Cannot delete your Account!", 4000, Notification.Position.BOTTOM_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            } else {
                noUserChosenNotification.open();
            }
        });

        headerComponents[0] = newUser;
        headerComponents[1] = changePassword;
        headerComponents[2] = deleteAccount;
        return headerComponents;
    }
}