package com.conleos.views;

import com.conleos.common.ColorGenerator;
import com.conleos.common.HtmlColor;
import com.conleos.core.Session;
import com.conleos.data.entity.User;
import com.conleos.views.admin.AdminView;
import com.conleos.views.home.HomeView;
import com.conleos.views.profile.PreferencesView;
import com.conleos.views.profile.ProfileView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private H2 viewTitle;
    private HorizontalLayout viewHeaderContainer;

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        viewHeaderContainer = new HorizontalLayout();
        viewHeaderContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        viewHeaderContainer.getStyle().set("margin-left", "16px");

        addToNavbar(true, toggle, viewTitle, viewHeaderContainer, createAvatar());
    }

    private void addDrawerContent() {
        H1 appName = new H1("Ausbildungsnachweise Online");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        nav.addItem(new SideNavItem("Home", HomeView.class,   LineAwesomeIcon.GLOBE_SOLID.create()));
        nav.addItem(new SideNavItem("Admin", AdminView.class, LineAwesomeIcon.HAMMER_SOLID.create()));
        nav.addItem(new SideNavItem("Profile", ProfileView.class, LineAwesomeIcon.STAR.create()));
        nav.addItem(new SideNavItem("Preferences", PreferencesView.class, LineAwesomeIcon.COGS_SOLID.create()));

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        return layout;
    }

    private Component createAvatar() {
        User user = Session.getSessionFromVaadinSession(VaadinSession.getCurrent()).getUser();

        Avatar avatar = new Avatar(user.getFullName());
        avatar.addThemeVariants(AvatarVariant.LUMO_XSMALL);
        avatar.getStyle().set("background-color", HtmlColor.from(ColorGenerator.fromRandomString(user.getUsername())).toString());
        HorizontalLayout container = new HorizontalLayout(avatar, new Span(user.getFullName()));
        container.setAlignItems(FlexComponent.Alignment.CENTER);

        MenuBar menu = new MenuBar();
        menu.setOpenOnHover(true);
        SubMenu subMenu = menu.addItem(container).getSubMenu();
        subMenu.addItem("Profile", event -> {
            UI.getCurrent().navigate(ProfileView.class);
        });
        subMenu.addItem("Account");
        subMenu.addItem("Preferences", event -> {
            UI.getCurrent().navigate(PreferencesView.class);
        });
        subMenu.add(new Hr());
        subMenu.addItem("Sign out", event -> {
            Session.logOut(VaadinSession.getCurrent());
            UI.getCurrent().getPage().reload();
        });

        return menu;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());

        viewHeaderContainer.removeAll();
        if (getContent() instanceof HasHeaderContent) {
            viewHeaderContainer.add(((HasHeaderContent)getContent()).createHeaderContent());
        }

    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
