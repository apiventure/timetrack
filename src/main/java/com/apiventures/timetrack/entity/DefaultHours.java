package com.apiventures.timetrack.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class DefaultHours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String project;
    private int mon;
    private int tue;
    private int wed;
    private int thu;
    private int fri;

    // constructors, getters, setters

    public DefaultHours() {}

    public DefaultHours(String project, int mon, int tue, int wed, int thu, int fri) {
        this.project = project;
        this.mon = mon;
        this.tue = tue;
        this.wed = wed;
        this.thu = thu;
        this.fri = fri;
    }

    // getters & setters omitted for brevity
}