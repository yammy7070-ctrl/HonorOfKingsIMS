# Code Review Notes

This file records issues found while reviewing the code with a Reviewer Agent,
and how I decided to fix them.

## Review 1 — Deletion consistency

Finding:
When an admin deletes a player, hero or equipment, GameDataManager only removed it
from its own HashMap. The object was still referenced elsewhere:
- a deleted player was still listed in its Team's member list;
- a deleted hero was still in players' owned-hero lists;
- a deleted equipment was still in heroes' equipped lists.

So after deleting, Team Overview or Player Lookup could still show the deleted item
(a "dangling reference").

Decision:
I fixed it in GameDataManager. Each delete method now also removes the object from
the related lists (team members / owned heroes / equipped items) before removing it
from the map. Past match records are left unchanged on purpose, because they are a
historical record.
## Review 2 — Input edge cases

Findings:
1. (Bug) If the user enters a negative number for "top how many" in the leaderboard,
   or a negative N in match history, the code called subList(0, n) with a negative
   size and crashed with IndexOutOfBoundsException.
2. (Minor, kept) A search returns only the first matching record, so a very broad
   query just returns one result. Acceptable for this project.
3. (Minor, known limitation) Passwords are stored as plain text — fine for a
   coursework demo, but not safe in a real system.

Decision:
I fixed finding 1 by clamping a negative count to 0 in RankingService and
SearchService, so it returns an empty list instead of crashing. Findings 2 and 3 are
left as known limitations and will be noted in the README.