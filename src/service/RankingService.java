package service;

import model.Equipment;
import model.Hero;
import model.MatchRecord;
import model.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds player leaderboards and equipment rankings. The scoring formulas come
 * from plan Section 6.2. When two players have the same score, the tie is broken
 * alphabetically by username (A before Z), so the order is always predictable.
 */
public class RankingService {

    private final GameDataManager data;

    public RankingService(GameDataManager data) {
        this.data = data;
    }

    /** Counts how many matches a player took part in (scanned from all matches). */
    public int countMatches(Player player) {
        int count = 0;
        for (MatchRecord m : data.getAllMatches()) {
            if (m.involvesPlayer(player)) count++;
        }
        return count;
    }

    // ---------- player leaderboards ----------
    public List<Player> leaderboardByWinRate(int topX) {
        List<Player> list = data.getAllPlayers();
        list.sort(Comparator.comparingDouble(Player::getWinRate).reversed()
                .thenComparing(Player::getUsername));
        return topN(list, topX);
    }

    public List<Player> leaderboardByLevel(int topX) {
        List<Player> list = data.getAllPlayers();
        list.sort(Comparator.comparingInt(Player::getLevel).reversed()
                .thenComparing(Player::getUsername));
        return topN(list, topX);
    }

    public List<Player> leaderboardByMatchCount(int topX) {
        List<Player> list = data.getAllPlayers();
        list.sort(Comparator.comparingInt((Player p) -> countMatches(p)).reversed()
                .thenComparing(Player::getUsername));
        return topN(list, topX);
    }

    /** Custom score = winRate*0.6 + (level/maxLevel)*0.3 + (matchCount/maxMatches)*0.1 */
    public List<Player> leaderboardByCustomScore(int topX) {
        List<Player> list = data.getAllPlayers();
        final int maxLevel = maxLevel(list);
        final int maxMatches = maxMatchCount(list);
        list.sort(Comparator.comparingDouble((Player p) -> customScore(p, maxLevel, maxMatches)).reversed()
                .thenComparing(Player::getUsername));
        return topN(list, topX);
    }

    public double customScore(Player p, int maxLevel, int maxMatches) {
        double levelPart = (maxLevel == 0) ? 0 : (double) p.getLevel() / maxLevel;
        double matchPart = (maxMatches == 0) ? 0 : (double) countMatches(p) / maxMatches;
        return p.getWinRate() * 0.6 + levelPart * 0.3 + matchPart * 0.1;
    }

    // ---------- equipment ranking ----------
    /** Equipment score = (usageCount/maxUsage)*0.4 + (averageRating/5.0)*0.6 */
    public List<Equipment> rankEquipment() {
        List<Equipment> list = data.getAllEquipment();
        final int maxUsage = maxUsage(list);
        list.sort(Comparator.comparingDouble((Equipment e) -> equipmentScore(e, maxUsage)).reversed()
                .thenComparing(Equipment::getName));
        return list;
    }

    public double equipmentScore(Equipment e, int maxUsage) {
        double usagePart = (maxUsage == 0) ? 0 : (double) e.getUsageCount() / maxUsage;
        double ratingPart = e.getAverageRating() / 5.0;
        return usagePart * 0.4 + ratingPart * 0.6;
    }

    // ---------- hero pick rate ----------
    /** Returns how many times each hero was picked across all matches. */
    public Map<Hero, Integer> heroPickCounts() {
        Map<Hero, Integer> counts = new LinkedHashMap<>();
        for (Hero h : data.getAllHeroes()) counts.put(h, 0);
        for (MatchRecord m : data.getAllMatches()) {
            for (Hero h : m.getTeam1Picks().values()) counts.merge(h, 1, Integer::sum);
            for (Hero h : m.getTeam2Picks().values()) counts.merge(h, 1, Integer::sum);
        }
        return counts;
    }

    // ---------- private helpers ----------
    private List<Player> topN(List<Player> list, int n) {
        if (n < 0) n = 0;                          // guard against a negative count
        if (list.size() > n) return new ArrayList<>(list.subList(0, n));
        return list;
    }
    private int maxLevel(List<Player> list) {
        int max = 0;
        for (Player p : list) if (p.getLevel() > max) max = p.getLevel();
        return max;
    }
    private int maxMatchCount(List<Player> list) {
        int max = 0;
        for (Player p : list) { int c = countMatches(p); if (c > max) max = c; }
        return max;
    }
    private int maxUsage(List<Equipment> list) {
        int max = 0;
        for (Equipment e : list) if (e.getUsageCount() > max) max = e.getUsageCount();
        return max;
    }
}