package model;

import enums.EquipmentType;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An item that heroes can equip. Holds a type, a set of stat bonuses,
 * a usage count, and an average rating used by ranking/recommendation logic.
 */
public class Equipment implements Searchable, Persistable {

    private final String equipmentId;          // identity is fixed, so final
    private String name;
    private EquipmentType equipmentType;
    private Map<String, Integer> statBonus;     // e.g. {"attack": 50, "defense": 20}
    private int usageCount;
    private double averageRating;               // 0.0 - 5.0

    public Equipment(String equipmentId, String name, EquipmentType equipmentType,
                     Map<String, Integer> statBonus, int usageCount, double averageRating) {
        this.equipmentId = equipmentId;
        this.name = name;
        this.equipmentType = equipmentType;
        this.statBonus = (statBonus != null) ? statBonus : new LinkedHashMap<>();
        this.usageCount = usageCount;
        this.averageRating = averageRating;
    }

    // --- getters / setters (encapsulation) ---
    public String getEquipmentId() { return equipmentId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public EquipmentType getEquipmentType() { return equipmentType; }
    public void setEquipmentType(EquipmentType equipmentType) { this.equipmentType = equipmentType; }
    public Map<String, Integer> getStatBonus() { return statBonus; }
    public int getUsageCount() { return usageCount; }
    public void setUsageCount(int usageCount) { this.usageCount = usageCount; }
    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }

    /** Increases usage count by one; called when a hero equips this item. */
    public void incrementUsage() {
        this.usageCount++;
    }

    // --- Searchable ---
    @Override
    public boolean matchesQuery(String query) {
        if (query == null) return false;
        String q = query.trim().toLowerCase();
        return equipmentId.toLowerCase().equals(q) || name.toLowerCase().contains(q);
    }

    // --- Persistable ---
    // Format: equipmentId|name|equipmentType|statBonus|usageCount|averageRating
    // statBonus encoded as key:value pairs joined by ';'  e.g. attack:50;defense:20
    @Override
    public String toFileFormat() {
        StringBuilder stats = new StringBuilder();
        int i = 0;
        for (Map.Entry<String, Integer> e : statBonus.entrySet()) {
            if (i++ > 0) stats.append(';');
            stats.append(e.getKey()).append(':').append(e.getValue());
        }
        return String.join("|",
                equipmentId, name, equipmentType.name(),
                stats.toString(), String.valueOf(usageCount), String.valueOf(averageRating));
    }
    // NOTE: the matching static fromFileFormat(...) factory is added in Step 7
    // (FileStorageService), where the full data set is available for re-linking.

    @Override
    public String toString() {
        return name + " (" + equipmentType.name() + ")";
    }
}