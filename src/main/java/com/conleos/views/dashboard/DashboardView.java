package com.conleos.views.dashboard;

import com.conleos.common.ColorGenerator;
import com.conleos.common.HtmlColor;
import com.conleos.common.Role;
import com.conleos.core.Session;
import com.conleos.data.entity.User;
import com.conleos.data.service.UserService;
import com.conleos.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;

import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@PageTitle("Dashboard")
@Route(value = "dashboard", layout = MainLayout.class)
public class DashboardView extends VerticalLayout implements BeforeEnterObserver {

    private Component createBanner(User trainee) {
        Avatar avatar = new Avatar(trainee.getFullName());
        avatar.addThemeVariants(AvatarVariant.LUMO_LARGE);
        avatar.getStyle().set("background-color", HtmlColor.from(ColorGenerator.fromRandomString(trainee.getUsername())).toString());

        Button btn = new Button(new HorizontalLayout(avatar, new Span(trainee.getFullName())));
        btn.setWidth("90%");
        btn.setHeight("135px");
        btn.addClickListener(event -> {
            UI.getCurrent().navigate("dashboard?user=" + trainee.getId());
        });
        return btn;
    }

    private Component createInstructorContent(User user) {
        VerticalLayout layout = new VerticalLayout();
        layout.setAlignItems(Alignment.CENTER);

        List<User> trainees = UserService.getInstance().getUsersByAssignee(user);

        for (User It : trainees) {
            layout.add(createBanner(It));
        }

        return layout;
    }

    private Component createTraineeContent(User user) {
        LocalDate beginOfCurrentWeek = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate beginOfWork = user.getStartDate().with(DayOfWeek.MONDAY);

        if (beginOfCurrentWeek.isBefore(beginOfWork)) {
            Span span = new Span("Work begins " + user.getStartDate().toString());
            span.getElement().getThemeList().add("badge error");
            return span;
        }

        VerticalLayout layout = new VerticalLayout();
        for (LocalDate It = beginOfWork; It.isBefore(beginOfCurrentWeek) || It.equals(beginOfCurrentWeek); It = It.plusWeeks(1)) {
            layout.add(new Button(It.toString()));
        }

        return layout;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

        // Optionally use a UserId to see the Dashboard of another user.
        // Useful for an Instructor to see a Trainees Dashboard
        //
        // Usage: localhost:8080/dashboard?user=12345
        // This shows the dashbaord of user 12345, if privileged

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
        setAlignItems(Alignment.CENTER);

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
                    if (other.getRole().equals(Role.Trainee) && other.getAssignee().getId() == session.getUser().getId()) {
                        add(createTraineeContent(other));
                    } else {
                        add(new Span("no privilege"));
                    }
                }

                // Only if otherUser is ourself
                case Trainee -> add(session.getUser().getId() == otherUserID ? createTraineeContent(session.getUser()) : new Span("no privilege"));
            }
        }


    }
}