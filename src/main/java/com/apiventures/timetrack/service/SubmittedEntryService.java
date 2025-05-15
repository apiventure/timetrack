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

    /**
     * Retrieve all submitted entries for a given week‐close date.
     */
    public List<SubmittedEntryEntity> findByWeek(LocalDate weekCloseDate) {
        return repo.findByWeekCloseDate(weekCloseDate);
    }
    public List<SubmittedEntryEntity> fetchAll() {
        return repo.findAllByOrderByWeekCloseDateDesc();
    }
    /**
     * Archive the current list of defaults as submitted entries for that Friday.
     * Deletes any previous archive for the same weekCloseDate first.
     */
    @Transactional
    public void archiveDefaults(LocalDate weekCloseDate, List<DefaultHours> defaults) {
        // Remove any existing entries for this Friday
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
