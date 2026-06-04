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