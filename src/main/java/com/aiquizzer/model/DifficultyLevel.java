package com.aiquizzer.model;

import lombok.Getter;

@Getter
public enum DifficultyLevel {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard"),
    EXPERT("Expert");
    
    private final String displayName;
    
    DifficultyLevel(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
