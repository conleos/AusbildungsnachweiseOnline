package com.conleos.views.dashboard;

import com.conleos.common.ColorGenerator;
import com.conleos.common.HtmlColor;
import com.conleos.data.entity.FormStatus;
import com.conleos.data.entity.User;
import com.conleos.data.service.FormService;
import com.conleos.data.service.UserService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.ListStyleType;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import com.vaadin.flow.theme.lumo.LumoUtility.MaxWidth;

import java.util.ArrayList;
import java.util.List;

public class InstructorDashboard extends Main implements HasComponents, HasStyle {

    private OrderedList itemContainer;

    public InstructorDashboard(ArrayList<Component> headerComponents, User instructor) {
        constructUI();

        List<User> trainees = UserService.getInstance().getUsersByAssignee(instructor);

        for (User trainee : trainees) {
            itemContainer.add(new UserCard(trainee));
        }

    }

    private void constructUI() {
        addClassNames("dashboard-view");
        addClassNames(MaxWidth.SCREEN_LARGE, Margin.Horizontal.AUTO, Padding.Bottom.LARGE, Padding.Horizontal.LARGE);

        HorizontalLayout container = new HorizontalLayout();
        container.addClassNames(AlignItems.CENTER, JustifyContent.BETWEEN);

        VerticalLayout headerContainer = new VerticalLayout();
        H2 header = new H2("Dashboard");
        header.addClassNames(Margin.Bottom.NONE, Margin.Top.XLARGE, FontSize.XXXLARGE);
        Paragraph description = new Paragraph("Manage your assigned Trainees.");
        description.addClassNames(Margin.Bottom.XLARGE, Margin.Top.NONE, TextColor.SECONDARY);
        headerContainer.add(header, description);

        Select<String> sortBy = new Select<>();
        sortBy.setLabel("Sort by");
        sortBy.setItems("Needs your action", "Newest first", "Oldest first");
        sortBy.setValue("Needs your action");

        itemContainer = new OrderedList();
        itemContainer.addClassNames(Gap.MEDIUM, Display.GRID, ListStyleType.NONE, Margin.NONE, Padding.NONE);

        container.add(headerContainer, sortBy);
        add(container, itemContainer);
    }

}

class UserCard extends ListItem {

    public UserCard(User trainee) {
        addClassNames(LumoUtility.Background.CONTRAST_5, LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN, LumoUtility.AlignItems.START, LumoUtility.Padding.MEDIUM,
                LumoUtility.BorderRadius.LARGE);

        getStyle().set("cursor", "pointer");

        Div div = new Div();
        div.addClassNames(LumoUtility.Background.CONTRAST_20, LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER, LumoUtility.JustifyContent.CENTER,
                LumoUtility.Margin.Bottom.MEDIUM, LumoUtility.Overflow.HIDDEN, LumoUtility.BorderRadius.MEDIUM, LumoUtility.Width.FULL);
        div.setHeight("160px");

        Avatar avatar = new Avatar(trainee.getFullName());
        avatar.addThemeVariants(AvatarVariant.LUMO_XLARGE);
        avatar.getStyle().set("background-color", HtmlColor.from(ColorGenerator.fromRandomString(trainee.getUsername())).toString());
        avatar.getStyle().set("cursor", "pointer");

        div.add(avatar);
        //div.addClassNames("striped-background");
        HtmlColor backgroundColor = HtmlColor.from(ColorGenerator.fromRandomString(trainee.getUsername()).darker().darker());
        HtmlColor backgroundColor2 = HtmlColor.from(backgroundColor.toAWTColor().darker());
        div.getStyle().set("background-image", "repeating-linear-gradient(45deg, " + backgroundColor + ", " + backgroundColor + " 10px, " + backgroundColor2 + " 10px, " + backgroundColor2 + " 20px)");

        Span header = new Span();
        header.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.FontWeight.SEMIBOLD);
        header.setText(trainee.getFullName());

        Span subtitle = new Span();
        subtitle.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        subtitle.setText("Click to view all Forms.");

        Paragraph description = new Paragraph("");
        description.addClassName(LumoUtility.Margin.Vertical.MEDIUM);

        final int formsInReviewCount = FormService.getInstance().getFormsByOwner(trainee).stream().filter(form -> form.getStatus().equals(FormStatus.InReview)).toList().size();
        Span badge = new Span();
        badge.getElement().setAttribute("theme", formsInReviewCount > 0 ? "badge" : "badge success");
        badge.setText(formsInReviewCount > 0 ? (formsInReviewCount + " Reviews left") : "No Reviews left.");

        add(div, header, subtitle, description, badge);

        addClickListener(listItemClickEvent -> UI.getCurrent().navigate("dashboard?user=" + trainee.getId()));
    }
}