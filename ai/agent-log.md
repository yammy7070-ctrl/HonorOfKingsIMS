# Agent Log

This file groups my AI use by agent role. Each role has its main contribution, my own decision, and the related Git commits. The detailed prompt-by-prompt records are in prompts.md.

## Architect Agent
Main contribution:
Helped me design the overall structure. It produced the PlantUML class diagram,
drafted my plan.md (project goal, requirement analysis, data design, and so on),
and suggested how to organise the enums and the three interfaces (Searchable,
Reportable, Persistable).

Human decision:
I did not accept everything. On the UML I fixed missing enum values (DRAW for
MatchResult, WARRIOR for HeroType) and removed login() / logout() from Person,
because login should be in AuthenticationService, not inside Person. For storage
I rejected CSV and JSON and chose a simple plain-text format with "|" between
fields. I also chose pure IDEA + handwritten serialization instead of Maven,
because I am not familiar with build tools yet.

Related commits:
- 8a43719 finalize plan.md and add UML diagram
- 8d0c90c define enums and core interfaces
- 3306abc design and implement ranking and scoring strategy

## Implementation Agent
Main contribution:
Helped me write the model classes a few at a time. First Equipment and Hero
(Hero.getStats() adds the base stats and the equipment bonus together), then the
Person hierarchy (Person / Player / Admin), Team and MatchRecord.

Human decision:
I accepted the class code but made my own choices in some places. For example, it
suggested moving the create / delete / edit methods out of Admin and into
GameDataManager so all the data logic is in one place, and I agreed this is
cleaner. I also decided to leave fromFileFormat for Step 7, because reading an
object back from a file needs the other data to be loaded first.

Related commits:
- ad0ee45 implement Equipment and Hero model classes
- cce3fa7 implement Person hierarchy, Team and MatchRecord
- e587f28 hard-code initial dataset in DataInitializer
- cc857d6 add duplicate-id and missing-record handling to GameDataManager
- d7dd991 implement AuthenticationService and SearchService
- 9e14e16 build console menu with login, role routing and query features
- ec6bc82 add admin data management with exception handling
- 26634c5 add fromFileFormat factories to model classes
- 20d6e78 implement FileStorageService with save and two-pass load
- f53149a load data on startup and add save option in Main
- 1e1fd8c add combat simulator (extra credit)
- c7825d4 add player self-edit, match win-loss and pick rate, equipment recommendation
## Testing / Reviewer Agent
Main contribution:
Reviewed the delete logic and found that deleting a player, hero or equipment left
dangling references in team member lists, owned-hero lists and equipped-item lists.
Human decision:
I fixed the three delete methods in GameDataManager so they also remove the object
from the related lists. I tested it by deleting a player and checking the team no
longer showed them.

Related commits:
- 46d1b8f review deletion consistency and reference handling
- c9e4ef4 remove dangling references when deleting player, hero or equipment
- e2ebb2d review input edge cases and ranking robustness
- 66c006b clamp negative count so leaderboard and match history do not crash