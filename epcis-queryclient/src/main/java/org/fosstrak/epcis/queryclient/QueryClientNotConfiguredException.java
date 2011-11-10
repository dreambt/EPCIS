package org.fosstrak.epcis.queryclient;

/**
 * Signals that the query client is not configured.
 */
public class QueryClientNotConfiguredException extends RuntimeException {

    private static final long serialVersionUID = 1214211483696372668L;

    public QueryClientNotConfiguredException() {
        super();
    }

    public QueryClientNotConfiguredException(String msg) {
        super(msg);
    }

    public QueryClientNotConfiguredException(Throwable e) {
        super(e);
    }

    public QueryClientNotConfiguredException(String msg, Throwable e) {
        super(msg, e);
    }
}