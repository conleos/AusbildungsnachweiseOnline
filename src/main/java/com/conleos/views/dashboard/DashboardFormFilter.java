package com.conleos.views.dashboard;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.List;

public class DashboardFormFilter {

    private Button button;
    private Dialog dialog;

    private Button cancelButton;
    private Button applyFiltersButton;

    public DashboardFormFilter() {
        button = new Button("Filter", VaadinIcon.FILTER.create());
        button.addClickListener(event -> {
           dialog.open();
        });
        dialog = new Dialog("Set Filters");
        dialog.setWidth("45%");

        cancelButton = new Button("Cancel");
        cancelButton.addClickListener(event -> {
            dialog.close();
        });
        applyFiltersButton = new Button("Apply");
        applyFiltersButton.addClickListener(event -> {
            dialog.close();
        });
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(applyFiltersButton);
    }

    public void filterAndSortFormCards(List<FormCard> cards) {
        
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
