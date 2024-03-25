package com.conleos.views.dashboard;

import com.conleos.core.Session;
import com.conleos.data.entity.User;
import com.conleos.data.service.UserService;
import com.conleos.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import java.util.List;

@PageTitle("Dashboard")
@Route(value = "dashboard", layout = MainLayout.class)
public class DashboardView extends VerticalLayout {

    public DashboardView(UserService service) {
        Session session = Session.getSessionFromVaadinSession(VaadinSession.getCurrent());

        setAlignItems(Alignment.CENTER);

        switch (session.getSessionRole()) {
            case Admin -> add(new Span("no content"));
            case Instructor -> add(createInstructorContent());
            case Trainee -> add(createTraineeContent());
        }

    }

    private Component createBanner(User trainee) {
        Button btn = new Button(new HorizontalLayout(new Avatar(trainee.getFullName()), new Span(trainee.getFullName())));
        btn.setWidth("90%");
        btn.setHeight("135px");
        return btn;
    }

    private Component createInstructorContent() {
        User user = Session.getSessionFromVaadinSession(VaadinSession.getCurrent()).getUser();

        VerticalLayout layout = new VerticalLayout();
        layout.setAlignItems(Alignment.CENTER);

        layout.add(createBanner(user));
        layout.add(createBanner(user));
        layout.add(createBanner(user));
        layout.add(createBanner(user));
        layout.add(createBanner(user));

        return layout;
    }

    private Component createTraineeContent() {
        return null;
    }

}