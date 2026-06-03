package model;

/**
 * Implemented by any entity that can be matched against a text search query
 * (such as an id or name). Lets SearchService search different entity types
 * through one common reference type (polymorphism).
 */
public interface Searchable {
    /**
     * @param query a search term (id or name fragment)
     * @return true if this entity matches the query
     */
    boolean matchesQuery(String query);
}
