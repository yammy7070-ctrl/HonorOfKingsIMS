# Prompt Records
## Prompt 01
Time: 2026-6-3 15:00\
Model: Kimi/Claude\
Agent Role: Architect Agent\
Related Commit: 8a43719
### My Prompt
Please give me PlantUML code to draw a UML class diagram. Title: "Honor of Kings
IMS - Class Diagram". White background, no fill colour. Put it in layers from top
to bottom: top is the Main class; next the Service Layer with four classes in a row
(AuthenticationService, SearchService, RankingService, FileStorageService); then
GameDataManager in the middle; then the Model Layer with MatchRecord, Team, Person
(with subclass Admin), Player, Hero, Equipment. Also show the enums (HeroType,
EquipmentType, MatchResult) and the three <<interface>> interfaces (Reportable,
Searchable, Persistable) in a row, and the InputHelper utility class on its own.
Try to keep the lines straight and not crossing. Please also tell me a website
where I can paste the code to get the picture. Use my plan and the PDF.
### AI Response Summary
It gave me PlantUML code. I pasted it into the PlantUML web tool and got a picture
(docs/uml1.png).
![UML Class Diagram](docs/uml1.png)
### My Decision
Modified. The picture had some mistakes: MatchResult has no DRAW, HeroType has no
WARRIOR, and Person has no role field. I asked the AI to fix these.
### AI Response Summary
It updated the code and I got a new picture
![UML Class Diagram](docs/uml2.webp)
### My Decision
Modified again. I saw that Person had login() and logout() methods. I think login
should not be inside Person, because that is the job of AuthenticationService, and
a Person should not control its own login state. So I told the AI to delete them.
CombatSimulator and RecommendationEngine were also missing, so I asked it to add
them from the PDF.
### AI Response Summary
It fixed the code again and I got the final diagram
![UML Class Diagram](docs/uml.png)
## Prompt 02
Time: 2026-6-3 15:30\
Model: Kimi/Claude\
Agent Role: Architect Agent\
Related Commit: 8a43719
### My Prompt
Please help me write my plan.md. Use chapter 1 "Project Goal" and chapter 2
"Requirement Analysis" (core features + extra features) from the PDF. I already
have chapters 3 (Java Concepts), 4 (Class Design) and 5 (UML), so skip those. Then
chapter 6 "Data Design" (data amount, formulas, storage format), chapter 7 "AI
Usage Plan", chapter 8 "Prompt Strategy", chapter 9 "Development Timeline", chapter
10 "Testing Plan" (please give 15 test cases, more than the 10 required), chapter 11
"Risk Analysis", chapter 12 "Extra Credit Plan" (plan all 5 extra features), and
chapter 13 "Final Reflection Placeholder".
### AI Response Summary
It gave me a full draft of the plan
[plan1.docx](./docs/requirement.docx)
### My Decision
Modified. The draft was mostly fine. I added real team names so the dataset table
is more complete, and added a little more detail to the planning. For UML I just
reference my own pictures. I also decided not to use CSV or an external JSON
library. Instead I will save data in a simple plain-text file with "|" between the
fields, because it needs no extra library and is easier for me to control.
## Prompt 03
Time: 2026-6-3 18:30\
Model: Claude\
Agent Role: Architect Agent\
Related Commit: 8d0c90c
### My Prompt
I want to make the enums and interfaces first. I will put the enums (EquipmentType,
HeroType, MatchResult, Role) in a package called "enums", and the interfaces
(Persistable, Reportable, Searchable) in a package called "model". For saving data
to files, should I use a build tool like Maven/Gradle for JSON, or just write the
file code by myself?
### AI Response Summary
It said both work: use Maven/Gradle if I am comfortable with build tools, or use
pure IDEA and write my own simple serialization if I want to avoid setting up
dependencies.
### My Decision
Accepted, and I chose pure IDEA + handwritten serialization, because I am not
familiar with Maven yet and I want to avoid environment problems. After that I
created the four enums and the three interfaces.
## Prompt 04
Time: 2026-6-3 19:18\
Model: Claude\
Agent Role:Implementation Agent\
Related Commit: ad0ee45
### My Prompt
Using my plan.md design and the existing enums and Searchable/Persistable
interfaces, implement only the Equipment and Hero model classes in plain Java:
private fields with getters/setters, matchesQuery for Searchable, toFileFormat
for Persistable (pipe-delimited, storing equipment IDs for references).
### AI Response Summary
Provided Equipment and Hero. Equipment is self-contained; Hero stores equippedItems
as a List<Equipment> and combines base + bonus stats via Map.merge in getStats().
toFileFormat stores referenced equipment by ID; fromFileFormat and re-linking are
deferred to FileStorageService (Step 7) because resolving references needs the full
loaded dataset.
### My Decision
Accepted. I did not fully understand the file-reading part yet, but it makes sense
that the equipment must exist before a hero can point to it. So I will leave
fromFileFormat for Step 7, like the AI suggested.
## Prompt 05
Time: 2026-6-3 19:35\
Model: Claude\
Agent Role: Implementation Agent\
Related Commit: cce3fa7
### My Prompt
Implement the remaining model classes (Person abstract, Player, Admin, Team,
MatchRecord) per my plan.md, in plain Java. Private fields, getters/setters,
matchesQuery/generateReport/toFileFormat where the interfaces require, store
references by ID.
### AI Response Summary
Provided all five classes. Person is abstract with a polymorphic getInfo().
Suggested moving Admin's create/delete/edit operations to GameDataManager and
keeping only canEditAll() on Admin, for better separation of concerns. Added
involvesPlayer() on MatchRecord to support player match-history search.
### My Decision
Accepted, including moving CRUD out of Admin into GameDataManager (I agreed this
is cleaner). 