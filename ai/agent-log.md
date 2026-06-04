# Agent Log

This file groups my AI use by agent role. Each role has its main contribution,
my own decision, and the related Git commits. The detailed prompt-by-prompt
records are in prompts.md.

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
- 
## Testing / Reviewer Agent
(To be completed in Step 6. After I ask the Reviewer Agent to check my code and
find bugs, I will write here: what bugs it found, how I fixed them myself, and the
related commit hashes.)