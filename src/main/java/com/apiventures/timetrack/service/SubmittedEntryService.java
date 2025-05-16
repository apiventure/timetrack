package com.apiventures.timetrack.service;



import com.apiventures.timetrack.entity.DefaultHours;
import com.apiventures.timetrack.entity.SubmittedEntryEntity;
import com.apiventures.timetrack.entity.SubmittedEntryEntity;
import com.apiventures.timetrack.repository.SubmittedEntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class SubmittedEntryService {

    private final SubmittedEntryRepository repo;

    public SubmittedEntryService(SubmittedEntryRepository repo) {
        this.repo = repo;
    }


    public List<SubmittedEntryEntity> findByWeek(LocalDate weekCloseDate) {
        return repo.findByWeekCloseDate(weekCloseDate);
    }
    public List<SubmittedEntryEntity> fetchAll() {
        return repo.findAllByOrderByWeekCloseDateDesc();
    }

    @Transactional
    public void archiveDefaults(LocalDate weekCloseDate, List<DefaultHours> defaults) {

        repo.findByWeekCloseDate(weekCloseDate)
                .forEach(existing -> repo.delete(existing));

        // Save each project-hours row as a SubmittedEntry
        defaults.forEach(d -> {
            SubmittedEntryEntity entry = new SubmittedEntryEntity(
                    weekCloseDate,
                    d.getProject(),
                    /* sat */ 0,
                    /* sun */ 0,
                    d.getMon(),
                    d.getTue(),
                    d.getWed(),
                    d.getThu(),
                    d.getFri()
            );
            repo.save(entry);
        });
    }
}
