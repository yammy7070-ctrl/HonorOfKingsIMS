# Design Document — Honor of Kings IMS

This document explains the architecture and the main design decisions of the
system. It complements plan.md (which lists the features and class fields) by
focusing on *how* the parts fit together and *why* each choice was made.

## 1. Architecture Overview

The system is built in layers, and dependencies only point downward. This keeps
the design clean and avoids circular dependencies.
Main  (console menu / user interface)
|
AuthenticationService  SearchService  RankingService  FileStorageService
|
GameDataManager   (single store of all data)
|
Model:  Person -> Player / Admin,  Hero,  Equipment,  Team,  MatchRecord
Supporting packages: `enums` (HeroType, EquipmentType, MatchResult, Role),
the interfaces (Searchable, Reportable, Persistable), and `util` (InputHelper,
ConsoleUtil, DataInitializer).

Rule: services depend on GameDataManager and the model classes; the model classes
do not depend on the services. The model is the lowest, most independent layer.

## 2. Package Structure
src/
Main.java
enums/        HeroType, EquipmentType, MatchResult, Role
model/        Person, Player, Admin, Hero, Equipment, Team, MatchRecord
Searchable, Reportable, Persistable
service/      GameDataManager, AuthenticationService, SearchService,
RankingService, FileStorageService
util/         InputHelper, ConsoleUtil, DataInitializer
exception/    RecordNotFoundException, DuplicateIdException, InvalidInputException
## 3. Key Design Decisions

- **Abstract Person + Player/Admin.** Person is abstract and has an abstract
  getInfo(). Player and Admin override it, so the program can hold both as a
  single Person reference (polymorphism).
- **Three interfaces.** Searchable (matchesQuery), Reportable (generateReport),
  Persistable (toFileFormat) let the services treat different entities the same way.
  Reading data back (fromFileFormat) is a static factory method on each class, not
  an interface method, because it must create a new object before any object exists.
- **Enums for fixed categories.** Types that never change (hero type, equipment
  type, match result, role) are enums, which is safer than using plain strings.
- **CRUD lives in GameDataManager, not Admin.** Admin only has canEditAll().
  Putting all add/delete/edit logic in one place (GameDataManager) keeps the data
  rules together and follows separation of concerns.
- **winRate is stored, not computed.** Player.winRate comes from the initial data
  and is not recalculated from matches, so the hard-coded data stays consistent.
- **Match history is derived, not stored.** A player does not keep a list of its
  matches. Instead SearchService scans all MatchRecord objects. This avoids
  two-way references between Player and MatchRecord.
- **Persistence by ID.** When saving, references (a hero's equipment, a team's
  players) are stored as IDs in a pipe-delimited text line. When loading, all
  entities are read first, then linked by ID (a two-pass load).

## 4. Service Layer Responsibilities

- **GameDataManager** — single source of truth. Holds all entities. Uses a
  HashMap<String, …> per entity type for fast lookup by ID, plus the lists for
  iteration. Provides add / find / update / delete for every entity.
- **AuthenticationService** — login and logout, remembers the current user, and
  checks whether the current user is an Admin or a Player.
- **SearchService** — finds players, teams, heroes and equipment by ID or name
  (using Searchable.matchesQuery), and returns a player's or team's recent matches.
- **RankingService** — builds leaderboards (by win rate, level, match count, or a
  custom score) and ranks equipment. Ties are broken alphabetically by username.
- **FileStorageService** (built in Step 7) — saves and loads all data using each
  class's toFileFormat() and fromFileFormat().

## 5. Collections Used

- `HashMap<String, Entity>` in GameDataManager — fast O(1) lookup by ID.
- `ArrayList` — ordered lists such as a team's members or a player's heroes.
- Sorting with a Comparator (and TreeMap where useful) — for leaderboards and
  equipment rankings.

## 6. Error Handling

Custom exceptions (RecordNotFoundException, DuplicateIdException,
InvalidInputException) report data problems clearly, and InputHelper protects the
console from invalid input so the program does not crash.
