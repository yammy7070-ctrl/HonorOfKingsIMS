package util;

import enums.EquipmentType;
import enums.HeroType;
import enums.MatchResult;
import model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Hard-codes the initial dataset (plan Section 6.1) and links the objects
 * together. Call initialize() once at program start, then use the getters.
 */
public class DataInitializer {

    private static final List<Equipment> equipmentList = new ArrayList<>();
    private static final List<Hero> heroList = new ArrayList<>();
    private static final List<Player> playerList = new ArrayList<>();
    private static final List<Team> teamList = new ArrayList<>();
    private static final List<MatchRecord> matchList = new ArrayList<>();
    private static final List<Admin> adminList = new ArrayList<>();

    private static boolean initialized = false;

    public static void initialize() {
        if (initialized) return;
        buildEquipment();
        buildHeroes();    // links equipment -> hero
        buildPlayers();   // links heroes -> player
        buildTeams();     // links players -> team
        buildAdmins();
        buildMatches();   // links teams/players/heroes -> match
        initialized = true;
    }

    // ---------- getters ----------
    public static List<Equipment> getEquipment() { return equipmentList; }
    public static List<Hero> getHeroes() { return heroList; }
    public static List<Player> getPlayers() { return playerList; }
    public static List<Team> getTeams() { return teamList; }
    public static List<MatchRecord> getMatches() { return matchList; }
    public static List<Admin> getAdmins() { return adminList; }

    // ---------- small helpers for stat maps ----------
    private static Map<String, Integer> stats(int hp, int attack, int defense) {
        Map<String, Integer> m = new LinkedHashMap<>();
        m.put("hp", hp); m.put("attack", attack); m.put("defense", defense);
        return m;
    }
    private static Map<String, Integer> bonus(String k1, int v1) {
        Map<String, Integer> m = new LinkedHashMap<>();
        m.put(k1, v1);
        return m;
    }
    private static Map<String, Integer> bonus(String k1, int v1, String k2, int v2) {
        Map<String, Integer> m = new LinkedHashMap<>();
        m.put(k1, v1); m.put(k2, v2);
        return m;
    }

    // ---------- lookup helpers used while linking ----------
    private static Equipment findEquip(String id) {
        for (Equipment e : equipmentList) if (e.getEquipmentId().equals(id)) return e;
        return null;
    }
    private static Hero findHero(String id) {
        for (Hero h : heroList) if (h.getHeroId().equals(id)) return h;
        return null;
    }
    private static Player findPlayer(String id) {
        for (Player p : playerList) if (p.getId().equals(id)) return p;
        return null;
    }
    private static Team findTeam(String id) {
        for (Team t : teamList) if (t.getTeamId().equals(id)) return t;
        return null;
    }

    // ---------- 20 equipment (covers all 4 types) ----------
    private static void buildEquipment() {
        // WEAPON
        equipmentList.add(new Equipment("E001", "Demon Blade", EquipmentType.WEAPON, bonus("attack", 90), 120, 4.5));
        equipmentList.add(new Equipment("E002", "Blade of Despair", EquipmentType.WEAPON, bonus("attack", 170), 300, 4.8));
        equipmentList.add(new Equipment("E003", "Hunter Strike", EquipmentType.WEAPON, bonus("attack", 80), 90, 4.2));
        equipmentList.add(new Equipment("E004", "Endless Battle", EquipmentType.WEAPON, bonus("attack", 65, "hp", 500), 210, 4.6));
        equipmentList.add(new Equipment("E005", "Berserker's Fury", EquipmentType.WEAPON, bonus("attack", 100), 160, 4.4));
        equipmentList.add(new Equipment("E006", "Spear of Longinus", EquipmentType.WEAPON, bonus("attack", 75), 70, 4.0));
        // ARMOR
        equipmentList.add(new Equipment("E007", "Immortality", EquipmentType.ARMOR, bonus("hp", 1000, "defense", 60), 250, 4.7));
        equipmentList.add(new Equipment("E008", "Antiquity Cuirass", EquipmentType.ARMOR, bonus("hp", 1200), 140, 4.3));
        equipmentList.add(new Equipment("E009", "Blade Armor", EquipmentType.ARMOR, bonus("defense", 90), 110, 4.1));
        equipmentList.add(new Equipment("E010", "Magic Armor", EquipmentType.ARMOR, bonus("defense", 100), 95, 4.0));
        equipmentList.add(new Equipment("E011", "Brawler's Cuirass", EquipmentType.ARMOR, bonus("hp", 1500, "defense", 50), 130, 4.2));
        // ACCESSORY
        equipmentList.add(new Equipment("E012", "Holy Crystal", EquipmentType.ACCESSORY, bonus("attack", 180), 220, 4.6));
        equipmentList.add(new Equipment("E013", "Book of Sages", EquipmentType.ACCESSORY, bonus("attack", 140, "hp", 300), 130, 4.3));
        equipmentList.add(new Equipment("E014", "Enchanted Kiss", EquipmentType.ACCESSORY, bonus("attack", 120), 100, 4.1));
        equipmentList.add(new Equipment("E015", "Fafnir's Talon", EquipmentType.ACCESSORY, bonus("attack", 90, "defense", 30), 80, 4.0));
        equipmentList.add(new Equipment("E016", "Cursed Helmet", EquipmentType.ACCESSORY, bonus("hp", 1300), 70, 3.9));
        // BOOTS
        equipmentList.add(new Equipment("E017", "Boots of Tranquility", EquipmentType.BOOTS, bonus("defense", 30, "hp", 200), 180, 4.4));
        equipmentList.add(new Equipment("E018", "Arcane Boots", EquipmentType.BOOTS, bonus("attack", 20), 200, 4.5));
        equipmentList.add(new Equipment("E019", "Warrior Boots", EquipmentType.BOOTS, bonus("defense", 45), 150, 4.2));
        equipmentList.add(new Equipment("E020", "Swift Boots", EquipmentType.BOOTS, bonus("attack", 15, "defense", 10), 90, 3.8));
    }

    // ---------- 15 heroes (covers all 6 types, each equips 2 items) ----------
    private static void buildHeroes() {
        addHero("H001", "Li Bai", HeroType.ASSASSIN, stats(3000, 200, 150), "E001", "E018");
        addHero("H002", "Diao Chan", HeroType.MAGE, stats(3200, 180, 120), "E012", "E018");
        addHero("H003", "Luban No.7", HeroType.MARKSMAN, stats(2800, 210, 100), "E002", "E003");
        addHero("H004", "Zhang Fei", HeroType.TANK, stats(4000, 120, 200), "E007", "E017");
        addHero("H005", "Cai Wenji", HeroType.SUPPORT, stats(3500, 130, 160), "E008", "E017");
        addHero("H006", "Lu Bu", HeroType.WARRIOR, stats(3800, 190, 170), "E004", "E019");
        addHero("H007", "Marco Polo", HeroType.MARKSMAN, stats(2900, 205, 110), "E002", "E005");
        addHero("H008", "Zhuge Liang", HeroType.MAGE, stats(3100, 185, 115), "E012", "E013");
        addHero("H009", "Cheng Yaojin", HeroType.TANK, stats(4200, 125, 195), "E011", "E017");
        addHero("H010", "Han Xin", HeroType.ASSASSIN, stats(3050, 195, 145), "E001", "E005");
        addHero("H011", "Sun Shangxiang", HeroType.MARKSMAN, stats(2850, 208, 105), "E003", "E018");
        addHero("H012", "Liu Bei", HeroType.WARRIOR, stats(3700, 188, 165), "E004", "E009");
        addHero("H013", "Wang Zhaojun", HeroType.MAGE, stats(3150, 182, 118), "E014", "E013");
        addHero("H014", "Yao", HeroType.SUPPORT, stats(3600, 128, 158), "E008", "E019");
        addHero("H015", "Guan Yu", HeroType.WARRIOR, stats(3750, 192, 168), "E006", "E019");
    }

    private static void addHero(String id, String name, HeroType type,
                                Map<String, Integer> base, String eq1, String eq2) {
        Hero h = new Hero(id, name, type, base);
        heroList.add(h);
        h.equip(findEquip(eq1));   // equip() also bumps the item's usage count
        h.equip(findEquip(eq2));
    }

    // ---------- 15 players (5 per team, each owns 3 heroes) ----------
    private static void buildPlayers() {
        addPlayer("P001", "Zhang Wei", "zhangwei", "pass001", 30, 0.62, "T001", "H001", "H002", "H003");
        addPlayer("P002", "Wang Fang", "wangfang", "pass002", 28, 0.55, "T001", "H004", "H005", "H006");
        addPlayer("P003", "Li Na", "lina", "pass003", 35, 0.70, "T001", "H007", "H008", "H009");
        addPlayer("P004", "Zhao Lei", "zhaolei", "pass004", 25, 0.48, "T001", "H010", "H011", "H012");
        addPlayer("P005", "Chen Jie", "chenjie", "pass005", 32, 0.66, "T001", "H013", "H014", "H015");

        addPlayer("P006", "Liu Yang", "liuyang", "pass006", 31, 0.60, "T002", "H001", "H004", "H007");
        addPlayer("P007", "Yang Min", "yangmin", "pass007", 29, 0.58, "T002", "H002", "H005", "H008");
        addPlayer("P008", "Huang Lei", "huanglei", "pass008", 33, 0.64, "T002", "H003", "H006", "H009");
        addPlayer("P009", "Wu Hao", "wuhao", "pass009", 27, 0.52, "T002", "H010", "H013", "H015");
        addPlayer("P010", "Zhou Xin", "zhouxin", "pass010", 36, 0.72, "T002", "H011", "H012", "H014");

        addPlayer("P011", "Xu Tao", "xutao", "pass011", 30, 0.61, "T003", "H001", "H005", "H009");
        addPlayer("P012", "Sun Li", "sunli", "pass012", 26, 0.50, "T003", "H002", "H006", "H010");
        addPlayer("P013", "Ma Chao", "machao", "pass013", 34, 0.68, "T003", "H003", "H007", "H011");
        addPlayer("P014", "Zhu Qian", "zhuqian", "pass014", 28, 0.54, "T003", "H004", "H008", "H012");
        addPlayer("P015", "Gao Feng", "gaofeng", "pass015", 37, 0.74, "T003", "H013", "H014", "H015");
    }

    private static void addPlayer(String id, String name, String user, String pass,
                                  int level, double winRate, String teamId,
                                  String h1, String h2, String h3) {
        Player p = new Player(id, name, user, pass, level, winRate, teamId);
        p.addHero(findHero(h1));
        p.addHero(findHero(h2));
        p.addHero(findHero(h3));
        playerList.add(p);
    }

    // ---------- 3 teams (5 members each) ----------
    private static void buildTeams() {
        addTeam("T001", "Dragon Slayers", 20, 13, "P001", "P002", "P003", "P004", "P005");
        addTeam("T002", "Phoenix Rise", 20, 11, "P006", "P007", "P008", "P009", "P010");
        addTeam("T003", "Tiger Guard", 20, 9, "P011", "P012", "P013", "P014", "P015");
    }

    private static void addTeam(String id, String name, int totalMatches, int wins, String... memberIds) {
        Team t = new Team(id, name, totalMatches, wins);
        for (String pid : memberIds) {
            t.addPlayer(findPlayer(pid));
        }
        teamList.add(t);
    }

    // ---------- admin account for login ----------
    private static void buildAdmins() {
        adminList.add(new Admin("A001", "System Admin", "admin", "admin123", 1));
    }

    // ---------- 10 match records (full picks: all 5 members per team) ----------
    private static void buildMatches() {
        addMatch("M001", LocalDate.of(2026, 5, 1), "T001", "T002", MatchResult.WIN,
                new String[]{"P001:H001", "P002:H004", "P003:H007", "P004:H010", "P005:H013"},
                new String[]{"P006:H001", "P007:H002", "P008:H003", "P009:H010", "P010:H011"});

        addMatch("M002", LocalDate.of(2026, 5, 3), "T001", "T003", MatchResult.LOSS,
                new String[]{"P001:H002", "P002:H005", "P003:H008", "P004:H011", "P005:H014"},
                new String[]{"P011:H001", "P012:H002", "P013:H003", "P014:H004", "P015:H013"});

        addMatch("M003", LocalDate.of(2026, 5, 5), "T002", "T003", MatchResult.WIN,
                new String[]{"P006:H004", "P007:H005", "P008:H006", "P009:H013", "P010:H012"},
                new String[]{"P011:H005", "P012:H006", "P013:H007", "P014:H008", "P015:H014"});

        addMatch("M004", LocalDate.of(2026, 5, 7), "T001", "T002", MatchResult.WIN,
                new String[]{"P001:H003", "P002:H006", "P003:H009", "P004:H012", "P005:H015"},
                new String[]{"P006:H007", "P007:H008", "P008:H009", "P009:H015", "P010:H014"});

        addMatch("M005", LocalDate.of(2026, 5, 9), "T003", "T001", MatchResult.LOSS,
                new String[]{"P011:H009", "P012:H010", "P013:H011", "P014:H012", "P015:H015"},
                new String[]{"P001:H001", "P002:H004", "P003:H007", "P004:H010", "P005:H013"});

        addMatch("M006", LocalDate.of(2026, 5, 11), "T002", "T001", MatchResult.DRAW,
                new String[]{"P006:H001", "P007:H002", "P008:H003", "P009:H010", "P010:H011"},
                new String[]{"P001:H002", "P002:H005", "P003:H008", "P004:H011", "P005:H014"});

        addMatch("M007", LocalDate.of(2026, 5, 13), "T003", "T002", MatchResult.WIN,
                new String[]{"P011:H001", "P012:H002", "P013:H003", "P014:H004", "P015:H013"},
                new String[]{"P006:H004", "P007:H005", "P008:H006", "P009:H013", "P010:H012"});

        addMatch("M008", LocalDate.of(2026, 5, 15), "T001", "T003", MatchResult.WIN,
                new String[]{"P001:H003", "P002:H006", "P003:H009", "P004:H012", "P005:H015"},
                new String[]{"P011:H005", "P012:H006", "P013:H007", "P014:H008", "P015:H014"});

        addMatch("M009", LocalDate.of(2026, 5, 17), "T002", "T003", MatchResult.LOSS,
                new String[]{"P006:H007", "P007:H008", "P008:H009", "P009:H015", "P010:H014"},
                new String[]{"P011:H009", "P012:H010", "P013:H011", "P014:H012", "P015:H015"});

        addMatch("M010", LocalDate.of(2026, 5, 19), "T001", "T002", MatchResult.WIN,
                new String[]{"P001:H001", "P002:H004", "P003:H007", "P004:H010", "P005:H013"},
                new String[]{"P006:H001", "P007:H002", "P008:H003", "P009:H010", "P010:H011"});
    }

    private static void addMatch(String id, LocalDate date, String t1Id, String t2Id,
                                 MatchResult result, String[] t1Picks, String[] t2Picks) {
        Team t1 = findTeam(t1Id);
        Team t2 = findTeam(t2Id);
        MatchRecord m = new MatchRecord(id, date, t1, t2, result);
        for (String pick : t1Picks) {
            String[] pair = pick.split(":");          // "P001:H001" -> ["P001","H001"]
            m.addPick(t1, findPlayer(pair[0]), findHero(pair[1]));
        }
        for (String pick : t2Picks) {
            String[] pair = pick.split(":");
            m.addPick(t2, findPlayer(pair[0]), findHero(pair[1]));
        }
        matchList.add(m);
    }
}
