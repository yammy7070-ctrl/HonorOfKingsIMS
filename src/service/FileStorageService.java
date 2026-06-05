package service;

import enums.MatchResult;
import exception.DuplicateIdException;
import model.Equipment;
import model.Hero;
import model.MatchRecord;
import model.Persistable;
import model.Player;
import model.Team;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves all data to plain text files in the data/ folder, and loads it back using
 * a two-pass load: first rebuild every object from its own line (simple fields only),
 * then link the references (hero equipment, team members, player heroes, match picks)
 * by looking the ids up in GameDataManager.
 */
public class FileStorageService {

    private static final String DATA_DIR = "data";
    private static final String EQUIPMENT_FILE = DATA_DIR + "/equipment.txt";
    private static final String HERO_FILE = DATA_DIR + "/heroes.txt";
    private static final String PLAYER_FILE = DATA_DIR + "/players.txt";
    private static final String TEAM_FILE = DATA_DIR + "/teams.txt";
    private static final String MATCH_FILE = DATA_DIR + "/matches.txt";

    private final GameDataManager data;

    public FileStorageService(GameDataManager data) {
        this.data = data;
    }

    /** True if a saved data set already exists (used to decide load vs. initial data). */
    public boolean dataFilesExist() {
        return Files.exists(Paths.get(PLAYER_FILE));
    }

    // ---------------- save ----------------
    public void saveAllData() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            writeEntities(EQUIPMENT_FILE, data.getAllEquipment());
            writeEntities(HERO_FILE, data.getAllHeroes());
            writeEntities(PLAYER_FILE, data.getAllPlayers());
            writeEntities(TEAM_FILE, data.getAllTeams());
            writeEntities(MATCH_FILE, data.getAllMatches());
            System.out.println("All data saved to the data/ folder.");
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    private void writeEntities(String file, List<? extends Persistable> items) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Persistable p : items) {
            lines.add(p.toFileFormat());
        }
        Files.write(Paths.get(file), lines);
    }

    // ---------------- load (two-pass) ----------------
    public void loadAllData() {
        try {
            // Pass 1: rebuild objects from their own line (simple fields only)
            for (String line : readLines(EQUIPMENT_FILE)) data.addEquipment(Equipment.fromFileFormat(line));
            for (String line : readLines(HERO_FILE))      data.addHero(Hero.fromFileFormat(line));
            for (String line : readLines(PLAYER_FILE))    data.addPlayer(Player.fromFileFormat(line));
            for (String line : readLines(TEAM_FILE))      data.addTeam(Team.fromFileFormat(line));

            // Pass 2: link the references by id
            linkHeroEquipment();
            linkPlayerHeroes();
            linkTeamMembers();
            loadMatches();

            System.out.println("All data loaded from the data/ folder.");
        } catch (IOException e) {
            System.out.println("Error loading data: " + e.getMessage());
        } catch (DuplicateIdException e) {
            System.out.println("Duplicate id while loading: " + e.getMessage());
        }
    }

    private void linkHeroEquipment() throws IOException {
        for (String line : readLines(HERO_FILE)) {
            String[] parts = line.split("\\|", -1);
            Hero hero = data.getHero(parts[0]);
            String ids = (parts.length > 4) ? parts[4] : "";
            if (hero != null && !ids.isEmpty()) {
                for (String eid : ids.split(",")) {
                    Equipment e = data.getEquipment(eid);
                    if (e != null) hero.getEquippedItems().add(e);   // add directly (don't re-count usage)
                }
            }
        }
    }

    private void linkPlayerHeroes() throws IOException {
        for (String line : readLines(PLAYER_FILE)) {
            String[] parts = line.split("\\|", -1);
            Player player = data.getPlayer(parts[0]);
            String ids = (parts.length > 7) ? parts[7] : "";
            if (player != null && !ids.isEmpty()) {
                for (String hid : ids.split(",")) {
                    Hero h = data.getHero(hid);
                    if (h != null) player.addHero(h);
                }
            }
        }
    }

    private void linkTeamMembers() throws IOException {
        for (String line : readLines(TEAM_FILE)) {
            String[] parts = line.split("\\|", -1);
            Team team = data.getTeam(parts[0]);
            String ids = (parts.length > 4) ? parts[4] : "";
            if (team != null && !ids.isEmpty()) {
                for (String pid : ids.split(",")) {
                    Player p = data.getPlayer(pid);
                    if (p != null) team.addPlayer(p);
                }
            }
        }
    }

    private void loadMatches() throws IOException, DuplicateIdException {
        for (String line : readLines(MATCH_FILE)) {
            String[] parts = line.split("\\|", -1);
            String matchId = parts[0];
            LocalDate date = LocalDate.parse(parts[1]);
            Team t1 = data.getTeam(parts[2]);
            Team t2 = data.getTeam(parts[3]);
            MatchResult result = MatchResult.valueOf(parts[4]);
            MatchRecord m = new MatchRecord(matchId, date, t1, t2, result);
            addPicks(m, t1, (parts.length > 5) ? parts[5] : "");
            addPicks(m, t2, (parts.length > 6) ? parts[6] : "");
            data.addMatch(m);
        }
    }

    private void addPicks(MatchRecord m, Team team, String picksStr) {
        if (picksStr == null || picksStr.isEmpty()) return;
        for (String pair : picksStr.split(";")) {
            String[] kv = pair.split(":");
            Player p = data.getPlayer(kv[0]);
            Hero h = data.getHero(kv[1]);
            if (p != null && h != null) m.addPick(team, p, h);
        }
    }

    // ---------------- helper ----------------
    private List<String> readLines(String file) throws IOException {
        Path path = Paths.get(file);
        if (!Files.exists(path)) return new ArrayList<>();
        return Files.readAllLines(path);
    }
}
