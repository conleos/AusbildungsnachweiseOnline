package com.conleos.views.admin;

import com.conleos.common.Role;
import com.conleos.core.Session;
import com.conleos.data.entity.User;
import com.conleos.data.service.UserService;
import com.conleos.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinSession;
import java.util.List;

@PageTitle("Admin")
@Route(value = "admin", layout = MainLayout.class)
@RouteAlias(value = "manage", layout = MainLayout.class)
public class AdminView extends HorizontalLayout {

    public AdminView(UserService service) {
        Session session = Session.getSessionFromVaadinSession(VaadinSession.getCurrent());

        if (session.getSessionRole().equals(Role.Trainee)) {
            add(new Span("Access denied!"));
            return;
        }

        CreateUserDialog createUserDialog = new CreateUserDialog();
        Button showDialog = new Button("Add a new User", e -> createUserDialog.open());
        add(createUserDialog, showDialog);

        List<User> users = service.getAllUsers();

        // https://vaadin.com/docs/latest/components/grid
        Grid<User> grid = new Grid<>(User.class, false);
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.addColumn(createInfoRenderer()).setHeader("Info")
                .setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(User::getUsername).setHeader("Username")
                .setAutoWidth(true);
        grid.addColumn(User::getPasswordHash).setHeader("Password Hash")
                .setAutoWidth(true);
        grid.addColumn(createStatusComponentRenderer()).setHeader("Role")
                .setAutoWidth(true);
        grid.addColumn(createAssigneeComponentRenderer()).setHeader("Assigned to")
                .setAutoWidth(true);
        grid.setItems(users);
        add(grid);
    }
    private static Renderer<User> createInfoRenderer() {
        return LitRenderer.<User> of(
                        "<vaadin-horizontal-layout style=\"align-items: center;\" theme=\"spacing\">"
                                + "<vaadin-avatar img=\"${item.pictureUrl}\" name=\"${item.fullName}\" alt=\"User avatar\"></vaadin-avatar>"
                                + "  <vaadin-vertical-layout style=\"line-height: var(--lumo-line-height-m);\">"
                                + "    <span> ${item.fullName} </span>"
                                + "    <span style=\"font-size: var(--lumo-font-size-s); color: var(--lumo-secondary-text-color);\">"
                                + "      ${item.email}" + "    </span>"
                                + "  </vaadin-vertical-layout>"
                                + "</vaadin-horizontal-layout>")
                .withProperty("pictureUrl", AdminView::getPictureURL)
                .withProperty("fullName", User::getFullName)
                .withProperty("email", User::getEmail);
    }

    private static Object getPictureURL(User user) {
        return "";
    }
    private static final SerializableBiConsumer<Span, User> statusComponentUpdater = (span, user) -> {
        switch (user.getRole()) {
            case Role.Admin -> span.getElement().setAttribute("theme", "badge error");
            case Role.Instructor -> span.getElement().setAttribute("theme", "badge success");
            case Role.Trainee -> span.getElement().setAttribute("theme", "badge contrast");
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

}