package com.conleos.views;

import com.conleos.common.ColorGenerator;
import com.conleos.common.HtmlColor;
import com.conleos.common.Role;
import com.conleos.core.Session;
import com.conleos.data.entity.User;
import com.conleos.i18n.Lang;
import com.conleos.views.admin.AdminView;
import com.conleos.views.dashboard.DashboardView;
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
        H1 appName = new H1(Lang.translate("core.appName"));
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();
        Session session = Session.getSessionFromVaadinSession(VaadinSession.getCurrent());

        nav.addItem(new SideNavItem(Lang.translate("sideNav.label.home"), HomeView.class,   LineAwesomeIcon.GLOBE_SOLID.create()));
        nav.addItem(new SideNavItem("Dashboard", DashboardView.class, LineAwesomeIcon.CHART_PIE_SOLID.create()));
        if (session.getSessionRole().equals(Role.Admin)) {
            nav.addItem(new SideNavItem(Lang.translate("sideNav.label.admin"), AdminView.class, LineAwesomeIcon.HAMMER_SOLID.create()));
        }
        nav.addItem(new SideNavItem(Lang.translate("sideNav.label.profile"), ProfileView.class, LineAwesomeIcon.USER.create()));
        nav.addItem(new SideNavItem(Lang.translate("sideNav.label.preference"), PreferencesView.class, LineAwesomeIcon.COG_SOLID.create()));

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
        subMenu.addItem(Lang.translate("subMenu.label.profile"), event -> {
            UI.getCurrent().navigate(ProfileView.class);
        });
        subMenu.addItem(Lang.translate("subMenu.label.account"));
        subMenu.addItem(Lang.translate("subMenu.label.preference"), event -> {
            UI.getCurrent().navigate(PreferencesView.class);
        });
        subMenu.add(new Hr());
        subMenu.addItem(Lang.translate("subMenu.label.signOut"), event -> {
            Session.logOut(VaadinSession.getCurrent());
            UI.getCurrent().getPage().reload();
        });

        menu.addClassNames("avatar-box");

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
        if (title != null) {
            return Lang.translate(title.value());
        } else {
            return "";
        }
    }

}
