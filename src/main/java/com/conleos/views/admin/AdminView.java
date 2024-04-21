package com.conleos.views.admin;

import com.conleos.common.ColorGenerator;
import com.conleos.common.HtmlColor;
import com.conleos.common.PasswordHasher;
import com.conleos.common.Role;
import com.conleos.core.Session;
import com.conleos.data.entity.User;
import com.conleos.data.service.UserService;
import com.conleos.views.HasHeaderContent;
import com.conleos.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.List;
import java.util.Optional;

@PageTitle("Admin")
@Route(value = "admin", layout = MainLayout.class)
@RouteAlias(value = "manage", layout = MainLayout.class)
public class AdminView extends VerticalLayout implements HasHeaderContent {

    private CreateUserDialog createUserDialog;
    private Long gridUserId;
    private String gridUserRole;
    private final CreateErrorNotification otherAdminPassword = new CreateErrorNotification("You can't change password of another admin!");
    private final CreateErrorNotification noUserChosen = new CreateErrorNotification("No user chosen!");

    public AdminView(UserService service) {
        addClassName("data-grid-view");
        setSizeFull();
        Session session = Session.getSessionFromVaadinSession(VaadinSession.getCurrent());
        if (!session.getSessionRole().equals(Role.Admin)) {
            add(new Span("Access denied!"));
            return;
        }
        createUserDialog = new CreateUserDialog();
        add(createUserDialog);

        List<User> users = service.getAllUsers();

        // https://vaadin.com/docs/latest/components/grid
        Grid<User> grid = new Grid<>(User.class, false);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_COLUMN_BORDERS);
        grid.setHeight("100%");
        grid.addColumn(createInfoRenderer()).setHeader("Info")
                .setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(User::getUsername).setHeader("Username")
                .setAutoWidth(true);
        /*grid.addColumn(User::getPasswordHash).setHeader("Password Hash")
                .setAutoWidth(true);*/
        grid.addColumn(createStatusComponentRenderer()).setHeader("Role")
                .setAutoWidth(true);
        grid.addColumn(createAssigneeComponentRenderer()).setHeader("Assigned to")
                .setAutoWidth(true);
        grid.addColumn(createStartDateComponentRenderer()).setHeader("Begin of Work")
                .setAutoWidth(true);
        grid.setItems(users);
        add(grid);
        grid.addSelectionListener(selection -> {
            noUserChosen.close();
            otherAdminPassword.close();
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
        return LitRenderer.<User> of(
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
    private static final SerializableBiConsumer<ComboBox<User>, User> assigneeComponentUpdater = (comboBox, user) -> {
        List<User> assignees = UserService.getInstance().getAllUsers();
        assignees.removeIf(it -> it.getRole() == Role.Trainee);

        comboBox.setEnabled(user.getRole().equals(Role.Trainee));
        comboBox.setItems(assignees);
        comboBox.setItemLabelGenerator(User::getUsername);
        comboBox.setValue(user.getAssignee());
        comboBox.addValueChangeListener(event -> {
            user.setAssignee(event.getValue());
            UserService.getInstance().saveUser(user);
        });
    };
    private static ComponentRenderer<ComboBox<User>, User> createAssigneeComponentRenderer() {
        return new ComponentRenderer<>(ComboBox<User>::new, assigneeComponentUpdater);
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
        Component[] headerComponents = new Component[2];
        Button newUser = new Button("Add a new User", e -> createUserDialog.open());
        Button changePassword = new Button("Change Password");
        changePassword.addClickListener(event -> {
            if (gridUserId != null && !gridUserRole.equals("Admin")) {
                CreateAdminAccessDialog access = new CreateAdminAccessDialog(gridUserId);
                access.open();
            } else if(gridUserId != null && gridUserRole.equals("Admin")) {
                otherAdminPassword.open();
            } else {
                noUserChosen.open();
            }
        });
        headerComponents[0] = newUser;
        headerComponents[1] = changePassword;
        return headerComponents;
    }
}