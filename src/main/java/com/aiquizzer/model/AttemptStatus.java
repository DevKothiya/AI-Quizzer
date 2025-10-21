package com.aiquizzer.model;

import lombok.Getter;

@Getter
public enum AttemptStatus {
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    ABANDONED("Abandoned"),
    TIMED_OUT("Timed Out");
    
    private final String displayName;
    
    AttemptStatus(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
