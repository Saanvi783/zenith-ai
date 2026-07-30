package com.zenith.backend.service;

import com.zenith.backend.model.PlacementNote;
import com.zenith.backend.repository.PlacementNoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RagService {

    private final PlacementNoteRepository noteRepository;
    private final OpenRouterService openRouterService;

    @Autowired
    public RagService(PlacementNoteRepository noteRepository, OpenRouterService openRouterService) {
        this.noteRepository = noteRepository;
        this.openRouterService = openRouterService;
    }

    public String retrieveRelevantContext(String query) {
        List<String> keywords = new ArrayList<>();
        try {
            String systemPrompt = "You are a search query optimizer. Extract search terms.";
            String userPrompt = String.format("Extract 2-3 key search terms (nouns or concepts) from this query: \"%s\". Reply with ONLY a comma-separated list of terms.", query);
            String response = openRouterService.ask(systemPrompt, userPrompt);
            for (String term : response.split(",")) {
                String clean = term.trim().replaceAll("[\"']", "");
                if (!clean.isEmpty()) {
                    keywords.add(clean);
                }
            }
        } catch (Exception e) {
            // Fallback: extract terms from query
            keywords = Arrays.stream(query.split("\\s+"))
                    .map(w -> w.replaceAll("[^a-zA-Z]", ""))
                    .filter(w -> w.length() > 3)
                    .limit(3)
                    .collect(Collectors.toList());
        }

        Set<PlacementNote> matchedNotes = new LinkedHashSet<>();
        for (String keyword : keywords) {
            if (keyword.length() >= 3) {
                List<PlacementNote> notes = noteRepository.searchByKeyword(keyword);
                matchedNotes.addAll(notes);
            }
        }

        if (matchedNotes.isEmpty()) {
            return "";
        }

        StringBuilder context = new StringBuilder("\n--- GROUNDED PLACEMENT CONTEXT ---\n");
        int count = 1;
        for (PlacementNote note : matchedNotes) {
            context.append(String.format("[%d] Title: %s (Category: %s)\nContent: %s\n\n",
                    count++, note.getTitle(), note.getCategory(), note.getContent()));
            if (count > 3) break; // limit to top 3 notes
        }
        context.append("------------------------------------\n");
        return context.toString();
    }
}
