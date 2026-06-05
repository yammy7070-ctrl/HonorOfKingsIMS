package service;

import exception.RecordNotFoundException;
import model.Equipment;
import model.Hero;
import model.MatchRecord;
import model.Player;
import model.Team;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Searches the data by id or name (using each entity's matchesQuery), and answers
 * match-history questions by scanning all MatchRecord objects.
 */
public class SearchService {

    private final GameDataManager data;

    public SearchService(GameDataManager data) {
        this.data = data;
    }

    /** Finds a player whose id or name matches the query; throws if none match. */
    public Player searchPlayer(String query) throws RecordNotFoundException {
        for (Player p : data.getAllPlayers()) {
            if (p.matchesQuery(query)) return p;
        }
        throw new RecordNotFoundException("No player matches: " + query);
    }

    public Team searchTeam(String query) throws RecordNotFoundException {
        for (Team t : data.getAllTeams()) {
            if (t.matchesQuery(query)) return t;
        }
        throw new RecordNotFoundException("No team matches: " + query);
    }

    public Hero searchHero(String query) throws RecordNotFoundException {
        for (Hero h : data.getAllHeroes()) {
            if (h.matchesQuery(query)) return h;
        }
        throw new RecordNotFoundException("No hero matches: " + query);
    }

    public Equipment searchEquipment(String query) throws RecordNotFoundException {
        for (Equipment e : data.getAllEquipment()) {
            if (e.matchesQuery(query)) return e;
        }
        throw new RecordNotFoundException("No equipment matches: " + query);
    }

    /** Returns the last n matches a player took part in, newest first. */
    public List<MatchRecord> getRecentMatchesForPlayer(Player player, int n) {
        if (n < 0) n = 0;
        List<MatchRecord> found = new ArrayList<>();
        for (MatchRecord m : data.getAllMatches()) {
            if (m.involvesPlayer(player)) {
                found.add(m);
            }
        }
        found.sort(Comparator.comparing(MatchRecord::getDate).reversed());
        if (found.size() > n) {
            return new ArrayList<>(found.subList(0, n));
        }
        return found;
    }

    /** Returns the last n matches a team took part in, newest first. */
    public List<MatchRecord> getRecentMatchesForTeam(Team team, int n) {
        if (n < 0) n = 0;
        List<MatchRecord> found = new ArrayList<>();
        for (MatchRecord m : data.getAllMatches()) {
            if (m.getTeam1() == team || m.getTeam2() == team) {
                found.add(m);
            }
        }
        found.sort(Comparator.comparing(MatchRecord::getDate).reversed());
        if (found.size() > n) {
            return new ArrayList<>(found.subList(0, n));
        }
        return found;
    }

    /** Returns all players who own the given hero (used by Hero Details). */
    public List<Player> getOwnersOfHero(Hero hero) {
        List<Player> owners = new ArrayList<>();
        for (Player p : data.getAllPlayers()) {
            if (p.getOwnedHeroes().contains(hero)) {
                owners.add(p);
            }
        }
        return owners;
    }
}