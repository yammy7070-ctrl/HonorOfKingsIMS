package service;

import exception.DuplicateIdException;
import exception.RecordNotFoundException;
import model.Admin;
import model.Equipment;
import model.Hero;
import model.MatchRecord;
import model.Player;
import model.Team;
import util.DataInitializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Central data store for the whole system (Singleton). Every other service reads
 * and writes data through this class, so there is one single source of truth.
 * Each entity type is kept in a HashMap keyed by its ID for fast O(1) lookup.
 *
 * The public addXxx methods reject duplicate IDs (DuplicateIdException) and the
 * deleteXxx methods reject missing records (RecordNotFoundException).
 * loadInitialData() puts the trusted initial data in directly, so it skips these
 * checks.
 */
public class GameDataManager {

    private static GameDataManager instance;

    private final Map<String, Player> players = new HashMap<>();
    private final Map<String, Hero> heroes = new HashMap<>();
    private final Map<String, Equipment> equipment = new HashMap<>();
    private final Map<String, Team> teams = new HashMap<>();
    private final Map<String, MatchRecord> matches = new HashMap<>();
    private final Map<String, Admin> admins = new HashMap<>();

    private GameDataManager() { }

    /** Returns the single shared instance, creating it the first time. */
    public static GameDataManager getInstance() {
        if (instance == null) {
            instance = new GameDataManager();
        }
        return instance;
    }

    /** Loads the hard-coded initial dataset from DataInitializer into the maps. */
    public void loadInitialData() {
        DataInitializer.initialize();
        for (Equipment e : DataInitializer.getEquipment()) equipment.put(e.getEquipmentId(), e);
        for (Hero h : DataInitializer.getHeroes()) heroes.put(h.getHeroId(), h);
        for (Player p : DataInitializer.getPlayers()) players.put(p.getId(), p);
        for (Team t : DataInitializer.getTeams()) teams.put(t.getTeamId(), t);
        for (MatchRecord m : DataInitializer.getMatches()) matches.put(m.getMatchId(), m);
        for (Admin a : DataInitializer.getAdmins()) admins.put(a.getId(), a);
    }

    // ---------- Player ----------
    public void addPlayer(Player p) throws DuplicateIdException {
        if (players.containsKey(p.getId())) {
            throw new DuplicateIdException("Player id already exists: " + p.getId());
        }
        players.put(p.getId(), p);
    }
    public Player getPlayer(String id) { return players.get(id); }
    public List<Player> getAllPlayers() { return new ArrayList<>(players.values()); }
    public void deletePlayer(String id) throws RecordNotFoundException {
        if (!players.containsKey(id)) {
            throw new RecordNotFoundException("Player not found: " + id);
        }
        players.remove(id);
    }

    // ---------- Hero ----------
    public void addHero(Hero h) throws DuplicateIdException {
        if (heroes.containsKey(h.getHeroId())) {
            throw new DuplicateIdException("Hero id already exists: " + h.getHeroId());
        }
        heroes.put(h.getHeroId(), h);
    }
    public Hero getHero(String id) { return heroes.get(id); }
    public List<Hero> getAllHeroes() { return new ArrayList<>(heroes.values()); }
    public void deleteHero(String id) throws RecordNotFoundException {
        if (!heroes.containsKey(id)) {
            throw new RecordNotFoundException("Hero not found: " + id);
        }
        heroes.remove(id);
    }

    // ---------- Equipment ----------
    public void addEquipment(Equipment e) throws DuplicateIdException {
        if (equipment.containsKey(e.getEquipmentId())) {
            throw new DuplicateIdException("Equipment id already exists: " + e.getEquipmentId());
        }
        equipment.put(e.getEquipmentId(), e);
    }
    public Equipment getEquipment(String id) { return equipment.get(id); }
    public List<Equipment> getAllEquipment() { return new ArrayList<>(equipment.values()); }
    public void deleteEquipment(String id) throws RecordNotFoundException {
        if (!equipment.containsKey(id)) {
            throw new RecordNotFoundException("Equipment not found: " + id);
        }
        equipment.remove(id);
    }

    // ---------- Team ----------
    public void addTeam(Team t) throws DuplicateIdException {
        if (teams.containsKey(t.getTeamId())) {
            throw new DuplicateIdException("Team id already exists: " + t.getTeamId());
        }
        teams.put(t.getTeamId(), t);
    }
    public Team getTeam(String id) { return teams.get(id); }
    public List<Team> getAllTeams() { return new ArrayList<>(teams.values()); }
    public void deleteTeam(String id) throws RecordNotFoundException {
        if (!teams.containsKey(id)) {
            throw new RecordNotFoundException("Team not found: " + id);
        }
        teams.remove(id);
    }

    // ---------- MatchRecord ----------
    public void addMatch(MatchRecord m) throws DuplicateIdException {
        if (matches.containsKey(m.getMatchId())) {
            throw new DuplicateIdException("Match id already exists: " + m.getMatchId());
        }
        matches.put(m.getMatchId(), m);
    }
    public MatchRecord getMatch(String id) { return matches.get(id); }
    public List<MatchRecord> getAllMatches() { return new ArrayList<>(matches.values()); }
    public void deleteMatch(String id) throws RecordNotFoundException {
        if (!matches.containsKey(id)) {
            throw new RecordNotFoundException("Match not found: " + id);
        }
        matches.remove(id);
    }

    // ---------- Admin ----------
    public void addAdmin(Admin a) throws DuplicateIdException {
        if (admins.containsKey(a.getId())) {
            throw new DuplicateIdException("Admin id already exists: " + a.getId());
        }
        admins.put(a.getId(), a);
    }
    public Admin getAdmin(String id) { return admins.get(id); }
    public List<Admin> getAllAdmins() { return new ArrayList<>(admins.values()); }
}
