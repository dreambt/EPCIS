package org.fosstrak.epcis.gui;

import java.util.EventListener;

/**
 * An interface implemented by GUI components that want notification
 * of changes to authentication options.
 */
public interface AuthenticationOptionsChangeListener extends EventListener {

    /**
     * Call this when the settings in the AuthenticationOptionsPanel have changed.
     *
     * @param ace an AuthenticationOptionsChangeEvent describing the changes.
     */
    public void configurationChanged(AuthenticationOptionsChangeEvent ace);
}