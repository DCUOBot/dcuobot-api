package com.dcuobot.api.common.sort;

import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class SortCriteriaHelpers {
    private static final Set<String> CHARACTER_SORT_CRITERIA = Set.of(
            "skill_points",
            "combat_rating",
            "pvp_combat_rating",
            "max_health",
            "max_power",
            "toughness",
            "might",
            "precision",
            "defense",
            "dominance",
            "restoration",
            "vitalization"
    );

    public boolean isValidCharacterSortCriteria(String sortCriteria) {
        return CHARACTER_SORT_CRITERIA.contains(sortCriteria);
    }
}
