package org.kontinuity.catapult.service.github.api;

/**
 * Indicates a specified repository does not exist.
 *
 * @author <a href="mailto:alr@redhat.com">Andrew Lee Rubinger</a>
 */
public class NoSuchRepositoryException extends RuntimeException {

    /** version number of this serializable class. */
    private static final long serialVersionUID = -78123136358123472L;
    
    /**
     * Constructor
     * @param message the exception message
     */
    public NoSuchRepositoryException(final String message) {
        super(message);
    }
}
