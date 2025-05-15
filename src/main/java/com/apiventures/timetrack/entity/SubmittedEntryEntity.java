package com.apiventures.timetrack.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "submitted_entries")
public class SubmittedEntryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The Friday (or week‐close date) this row belongs to
    private LocalDate weekCloseDate;

    private String project;
    private int sat;
    private int sun;
    private int mon;
    private int tue;
    private int wed;
    private int thu;
    private int fri;

    public SubmittedEntryEntity() {
    }

    public SubmittedEntryEntity(LocalDate weekCloseDate,
                                String project,
                                int sat, int sun,
                                int mon, int tue,
                                int wed, int thu,
                                int fri) {
        this.weekCloseDate = weekCloseDate;
        this.project = project;
        this.sat = sat;
        this.sun = sun;
        this.mon = mon;
        this.tue = tue;
        this.wed = wed;
        this.thu = thu;
        this.fri = fri;
    }
}