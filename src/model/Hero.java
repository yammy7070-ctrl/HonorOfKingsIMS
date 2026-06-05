package model;

import enums.HeroType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A playable hero with a type, base stats, and a list of equipped items.
 * Each hero can equip at least two compatible Equipment objects (plan Section 6.1).
 */
public class Hero implements Searchable, Persistable {

    private final String heroId;
    private String name;
    private HeroType heroType;
    private Map<String, Integer> baseStats;     // e.g. {"hp": 3000, "attack": 200}
    private List<Equipment> equippedItems;

    public Hero(String heroId, String name, HeroType heroType, Map<String, Integer> baseStats) {
        this.heroId = heroId;
        this.name = name;
        this.heroType = heroType;
        this.baseStats = (baseStats != null) ? baseStats : new LinkedHashMap<>();
        this.equippedItems = new ArrayList<>();
    }

    // --- getters / setters ---
    public String getHeroId() { return heroId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public HeroType getHeroType() { return heroType; }
    public void setHeroType(HeroType heroType) { this.heroType = heroType; }
    public Map<String, Integer> getBaseStats() { return baseStats; }
    public List<Equipment> getEquippedItems() { return equippedItems; }

    /** Equips an item (if not already equipped) and updates that item's usage count. */
    public void equip(Equipment item) {
        if (item != null && !equippedItems.contains(item)) {
            equippedItems.add(item);
            item.incrementUsage();
        }
    }

    public void unequip(Equipment item) {
        equippedItems.remove(item);
    }

    /** Returns base stats combined with the bonuses from all equipped items. */
    public Map<String, Integer> getStats() {
        Map<String, Integer> total = new LinkedHashMap<>(baseStats);
        for (Equipment item : equippedItems) {
            for (Map.Entry<String, Integer> bonus : item.getStatBonus().entrySet()) {
                total.merge(bonus.getKey(), bonus.getValue(), Integer::sum);
            }
        }
        return total;
    }

    // --- Searchable ---
    @Override
    public boolean matchesQuery(String query) {
        if (query == null) return false;
        String q = query.trim().toLowerCase();
        return heroId.toLowerCase().equals(q) || name.toLowerCase().contains(q);
    }

    // --- Persistable ---
    // Format: heroId|name|heroType|baseStats|equippedItemIds
    // baseStats: hp:3000;attack:200    equippedItemIds: E001,E002
    @Override
    public String toFileFormat() {
        StringBuilder stats = new StringBuilder();
        int i = 0;
        for (Map.Entry<String, Integer> e : baseStats.entrySet()) {
            if (i++ > 0) stats.append(';');
            stats.append(e.getKey()).append(':').append(e.getValue());
        }
        StringBuilder ids = new StringBuilder();
        for (int j = 0; j < equippedItems.size(); j++) {
            if (j > 0) ids.append(',');
            ids.append(equippedItems.get(j).getEquipmentId());
        }
        return String.join("|", heroId, name, heroType.name(), stats.toString(), ids.toString());
    }
    // NOTE: fromFileFormat(...) and equipment re-linking are implemented in Step 7.
    // --- read back from file (static factory) ---
    // Format: heroId|name|heroType|baseStats|equippedItemIds
    // Equipment links are restored later by FileStorageService (two-pass load).
    public static Hero fromFileFormat(String line) {
        String[] parts = line.split("\\|", -1);
        String id = parts[0];
        String name = parts[1];
        HeroType type = HeroType.valueOf(parts[2]);
        Map<String, Integer> base = parseStats(parts[3]);
        return new Hero(id, name, type, base);
    }

    /** Parses "hp:3000;attack:200" into a stat map. */
    private static Map<String, Integer> parseStats(String s) {
        Map<String, Integer> map = new LinkedHashMap<>();
        if (s == null || s.isEmpty()) return map;
        for (String pair : s.split(";")) {
            String[] kv = pair.split(":");
            map.put(kv[0], Integer.parseInt(kv[1]));
        }
        return map;
    }

    @Override
    public String toString() {
        return name + " [" + heroType.getDisplayName() + "]";
    }
}