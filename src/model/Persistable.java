package model;

/**
 * Implemented by entities that can serialise themselves to a single text line
 * for file storage.
 *
 * Note: reading data back is done by a static factory method (fromFileFormat)
 * on each implementing class, NOT in this interface. An interface instance
 * method needs an existing object to run on, but fromFileFormat must CREATE
 * a new object from a string before any object exists — so it has to be static.
 */
public interface Persistable {
    /** @return a single-line string representation suitable for saving to file */
    String toFileFormat();
}
