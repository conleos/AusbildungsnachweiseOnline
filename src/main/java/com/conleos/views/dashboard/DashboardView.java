package com.conleos.views.dashboard;

import com.conleos.common.Role;
import com.conleos.core.Session;
import com.conleos.data.entity.Form;
import com.conleos.data.entity.User;
import com.conleos.data.service.FormService;
import com.conleos.data.service.UserService;
import com.conleos.views.HasHeaderContent;
import com.conleos.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.theme.lumo.LumoUtility.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@PageTitle("Dashboard")
@Route(value = "dashboard", layout = MainLayout.class)
public class DashboardView extends Div implements BeforeEnterObserver, HasHeaderContent {

    private ArrayList<Component> headerComponents = new ArrayList<>();

    private Component createInstructorContent(User user) {
        return new InstructorDashboard(headerComponents, user);
    }

    private Component createTraineeContent(User user) {
        return new TraineeDashboard(headerComponents, user);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

        // Optionally use a UserId to see the Dashboard of another user.
        // Useful for an Instructor to see a Trainees Dashboard
        //
        // Usage: localhost:8080/dashboard?user=12345
        // This shows the dashbaord of user 12345, if privileged

        this.addClassNames("outer-box");

        QueryParameters params = event.getLocation().getQueryParameters();
        List<String> userParams = params.getParameters().getOrDefault("user", null);
        Long otherUserID = (long) -1;
        if (userParams != null && userParams.size() == 1) {
            try {
                otherUserID = Long.parseLong(userParams.get(0));
            } catch (Exception ignored) { }
        }

        // Create page content
        removeAll();
        setSizeFull();

        Session session = Session.getSessionFromVaadinSession(VaadinSession.getCurrent());

        if (otherUserID == -1) {
            switch (session.getSessionRole()) {
                case Admin -> add(new Span("no content"));
                case Instructor -> add(createInstructorContent(session.getUser()));
                case Trainee -> add(createTraineeContent(session.getUser()));
            }
        } else {
            switch (session.getSessionRole()) {
                // As an Admin you can see everything
                case Admin -> {
                    User other = UserService.getInstance().getUserByID(otherUserID);
                    switch (other.getRole()) {
                        case Admin -> add(new Span("no content"));
                        case Instructor -> add(createInstructorContent(other));
                        case Trainee -> add(createTraineeContent(other));
                    }
                }

                // Only Assigned Trainees can be viewed
                case Instructor -> {
                    User other = UserService.getInstance().getUserByID(otherUserID);
                    if (other.getRole().equals(Role.Trainee) && other.getAssignee().getId().equals(session.getUser().getId())) {
                        add(createTraineeContent(other));
                    } else {
                        add(new Span("no privilege"));
                    }
                }

                // Only if otherUser is ourself
                case Trainee -> add(session.getUser().getId().equals(otherUserID) ? createTraineeContent(session.getUser()) : new Span("no privilege"));
            }
        }

    }

    @Override
    public Component[] createHeaderContent() {
        return headerComponents.toArray(new Component[0]);
    }
}