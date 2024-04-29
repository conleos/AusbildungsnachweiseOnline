package com.conleos.data.service;

import com.conleos.common.PasswordHasher;
import com.conleos.common.Role;
import com.conleos.data.entity.Form;
import com.conleos.data.entity.User;
import com.conleos.data.repository.FormRepository;
import com.conleos.data.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class FormService {
    private static FormService instance;

    private final FormRepository formRepository;

    public FormService(FormRepository formRepository) {
        instance = this;
        this.formRepository = formRepository;
    }

    public static FormService getInstance() {
        return instance;
    }

    public List<Form> getAllForms() {
        return formRepository.getAllForms();
    }
    public List<Integer> getAllFormIDs() {
        return formRepository.getAllFormChannels();
    }

    public void saveForm(Form form) {
        List<Form.FormEntry> temp = form.getEntries();
        form.setEntries(new ArrayList<>());
        formRepository.saveAndFlush(form);

        for (Form.FormEntry entry : temp) {
            form.getEntries().add(new Form.FormEntry(entry));
        }
        formRepository.saveAndFlush(form);
    }
    public Form getFormByID(Long id) {
        List<Form> temp = formRepository.getFormByChannel(id);
        return temp.isEmpty() ? null : temp.getFirst();
    }
    public List<Form> getFormsByOwner(User owner) {
        return formRepository.getFormsByOwner(owner);
    }
    public Form getFormByDateAndUser(LocalDate date, User user) {
        List<Form> temp = formRepository.getFormsByDateAndUser(date, user);
        return temp.isEmpty() ? null : temp.getFirst();
    }
}
