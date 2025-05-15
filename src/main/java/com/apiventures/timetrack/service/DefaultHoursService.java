package com.apiventures.timetrack.service;

import com.apiventures.timetrack.entity.DefaultHours;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class DefaultHoursService {
    private final List<DefaultHours> defaults = new ArrayList<>();

    public List<DefaultHours> findAll() {
        return Collections.unmodifiableList(defaults);
    }

    public void saveAll(List<DefaultHours> entries) {
        defaults.clear();
        defaults.addAll(entries);
    }
}