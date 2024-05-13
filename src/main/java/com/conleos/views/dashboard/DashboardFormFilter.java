package com.conleos.views.dashboard;

import com.conleos.data.entity.FormStatus;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.List;
import java.util.Locale;

public class DashboardFormFilter extends VerticalLayout {

    private final Button button;
    private Dialog dialog;
    private final Button applyFiltersButton;
    private boolean hideSignedForms = true;
    private Locale locale = UI.getCurrent().getLocale();

    public DashboardFormFilter() {
        button = new Button(getTranslation("view.dashboardFilter.button.filter", locale), VaadinIcon.FILTER.create());
        button.addClickListener(event -> dialog.open());
        dialog = new Dialog(getTranslation("view.dashboardFilter.button.filter.label", locale));
        dialog.setWidth("45%");

        Checkbox hideSignedFormsCheckBox = new Checkbox(getTranslation("view.dashboardFilter.checkBox.label", locale));
        hideSignedFormsCheckBox.setValue(hideSignedForms);
        dialog.add(hideSignedFormsCheckBox);

        Button cancelButton = new Button(getTranslation("view.dashboardFilter.button.cancel", locale));
        cancelButton.addClickListener(event -> {
            dialog.close();
            hideSignedFormsCheckBox.setValue(hideSignedForms);
        });
        applyFiltersButton = new Button(getTranslation("view.dashboardFilter.button.apply", locale));
        applyFiltersButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        applyFiltersButton.addClickListener(event -> {
            dialog.close();
            hideSignedForms = hideSignedFormsCheckBox.getValue();
        });
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(applyFiltersButton);
    }

    public void filterAndSortFormCards(List<FormCard> cards) {
        if (hideSignedForms) {
            cards.removeIf(formCard -> formCard.getForm() != null && formCard.getForm().getStatus().equals(FormStatus.Signed));
        }
    }

    public Button getButton() {
        return button;
    }

    public Dialog getDialog() {
        return dialog;
    }

    public Button getApplyFiltersButton() {
        return applyFiltersButton;
    }


}
