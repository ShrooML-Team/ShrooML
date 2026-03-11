package com.shrooml.models;

import java.util.List;

public class ClassificationEntity {
    private List<SuggestionEntity> suggestions;

    public List<SuggestionEntity> getSuggestions() {
        return this.suggestions;
    }

    public void setSuggestions(List<SuggestionEntity> suggestions) {
        this.suggestions = suggestions;
    }
}
