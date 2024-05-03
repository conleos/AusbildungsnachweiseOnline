package com.conleos.views.home;

import com.conleos.core.Session;
import com.conleos.views.MainLayout;
import com.conleos.views.admin.AdminView;
import com.conleos.views.dashboard.DashboardView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;

@PageTitle("Home")
@Route(value = "home", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)

public class HomeView extends Div implements BeforeEnterObserver {

    public HomeView() {

    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        Session session = Session.getSessionFromVaadinSession(VaadinSession.getCurrent());

        switch (session.getSessionRole()) {
            case Admin -> beforeEnterEvent.rerouteTo(AdminView.class);
            case Instructor, Trainee -> beforeEnterEvent.rerouteTo(DashboardView.class);
        }
    }
}
