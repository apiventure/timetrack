package com.apiventures.timetrack.repository;

import com.apiventures.timetrack.entity.SubmittedEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface SubmittedEntryRepository extends JpaRepository<SubmittedEntryEntity, Long> {
    /**
     * Fetch all entries that share the same weekCloseDate (Friday).
     */
    List<SubmittedEntryEntity> findByWeekCloseDate(LocalDate weekCloseDate);
    List<SubmittedEntryEntity> findAllByOrderByWeekCloseDateDesc();
}