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

    private static final Set<String> GUILD_SORT_CRITERIA = Set.of(
            "memberCount",
            "averageSkillPoints",
            "averageCombatRating",
            "averagePvpCombatRating"
    );

    public boolean isValidCharacterSortCriteria(String sortCriteria) {
        return CHARACTER_SORT_CRITERIA.contains(sortCriteria);
    }

    public boolean isValidGuildSortCriteria(String sortCriteria) {
        return GUILD_SORT_CRITERIA.contains(sortCriteria);
    }
}
