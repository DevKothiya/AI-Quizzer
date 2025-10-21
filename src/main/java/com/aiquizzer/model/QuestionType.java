package com.aiquizzer.model;

import lombok.Getter;

@Getter
public enum QuestionType {
    MULTIPLE_CHOICE("Multiple Choice"),
    TRUE_FALSE("True/False"),
    SHORT_ANSWER("Short Answer"),
    ESSAY("Essay"),
    FILL_IN_BLANK("Fill in the Blank");
    
    private final String displayName;
    
    QuestionType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
