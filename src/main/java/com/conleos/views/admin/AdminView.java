package com.conleos.views.admin;

import com.conleos.common.Role;
import com.conleos.core.Session;
import com.conleos.views.MainLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinSession;

@PageTitle("Hello World")
@Route(value = "admin", layout = MainLayout.class)
@RouteAlias(value = "manage", layout = MainLayout.class)
public class AdminView extends HorizontalLayout {

    public AdminView() {
        Session session = Session.getSessionFromVaadinSession(VaadinSession.getCurrent());

        if (session.getSessionRole().equals(Role.Trainee)) {
            add(new Span("Access denied!"));
            return;
        }

    }

}