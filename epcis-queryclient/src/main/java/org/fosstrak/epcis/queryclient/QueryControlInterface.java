package org.fosstrak.epcis.queryclient;

import java.util.List;

import org.fosstrak.epcis.model.Poll;
import org.fosstrak.epcis.model.QueryResults;
import org.fosstrak.epcis.model.Subscribe;
import org.fosstrak.epcis.soap.DuplicateSubscriptionExceptionResponse;
import org.fosstrak.epcis.soap.ImplementationExceptionResponse;
import org.fosstrak.epcis.soap.InvalidURIExceptionResponse;
import org.fosstrak.epcis.soap.NoSuchNameExceptionResponse;
import org.fosstrak.epcis.soap.NoSuchSubscriptionExceptionResponse;
import org.fosstrak.epcis.soap.QueryParameterExceptionResponse;
import org.fosstrak.epcis.soap.QueryTooComplexExceptionResponse;
import org.fosstrak.epcis.soap.QueryTooLargeExceptionResponse;
import org.fosstrak.epcis.soap.SecurityExceptionResponse;
import org.fosstrak.epcis.soap.SubscribeNotPermittedExceptionResponse;
import org.fosstrak.epcis.soap.SubscriptionControlsExceptionResponse;
import org.fosstrak.epcis.soap.ValidationExceptionResponse;

public interface QueryControlInterface {

    /**
     * Performs a poll operation at the repository's Query Controls Module.
     * 
     * @param poll
     *            The Poll object including the query name and parameters to be
     *            executed.
     * @return The QueryResults.
     * @throws QueryParameterExceptionResponse
     * @throws NoSuchNameExceptionResponse
     * @throws ValidationExceptionResponse
     * @throws SecurityExceptionResponse
     * @throws QueryTooLargeExceptionResponse
     * @throws QueryTooComplexExceptionResponse
     * @throws ImplementationExceptionResponse
     */
    QueryResults poll(final Poll poll) throws ImplementationExceptionResponse, QueryTooComplexExceptionResponse,
            QueryTooLargeExceptionResponse, SecurityExceptionResponse, ValidationExceptionResponse,
            NoSuchNameExceptionResponse, QueryParameterExceptionResponse;

    /**
     * Performs a subscribe operation at the repository's Query Controls Module,
     * i.e. subscribes a query for later execution.
     * 
     * @param subscribe
     *            The Subscribe object including the query name, the parameters,
     *            and subscription id used for subscribing the query.
     * @throws QueryParameterExceptionResponse
     * @throws SubscriptionControlsExceptionResponse
     * @throws NoSuchNameExceptionResponse
     * @throws SubscribeNotPermittedExceptionResponse
     * @throws ValidationExceptionResponse
     * @throws InvalidURIExceptionResponse
     * @throws SecurityExceptionResponse
     * @throws QueryTooComplexExceptionResponse
     * @throws ImplementationExceptionResponse
     * @throws DuplicateSubscriptionExceptionResponse
     */
    void subscribe(final Subscribe subscribe) throws DuplicateSubscriptionExceptionResponse,
            ImplementationExceptionResponse, QueryTooComplexExceptionResponse, SecurityExceptionResponse,
            InvalidURIExceptionResponse, ValidationExceptionResponse, SubscribeNotPermittedExceptionResponse,
            NoSuchNameExceptionResponse, SubscriptionControlsExceptionResponse, QueryParameterExceptionResponse;

    /**
     * Perform an unsubscribe operation at the repository's Query Controls
     * Module, i.e. unsubscribes a previously subscribed query.
     * 
     * @param subscriptionId
     *            The ID of the query to be unsubscribed.
     * @throws NoSuchSubscriptionExceptionResponse
     * @throws ValidationExceptionResponse
     * @throws SecurityExceptionResponse
     * @throws ImplementationExceptionResponse
     */
    void unsubscribe(final String subscriptionId) throws ImplementationExceptionResponse, SecurityExceptionResponse,
            ValidationExceptionResponse, NoSuchSubscriptionExceptionResponse;

    /**
     * Retrieves the names of queries that can be coped with.
     * 
     * @return A List of query names.
     * @throws ValidationExceptionResponse
     * @throws SecurityExceptionResponse
     */
    List<String> getQueryNames() throws ImplementationExceptionResponse, SecurityExceptionResponse,
            ValidationExceptionResponse;

    /**
     * Retrieves the ID of a subscribed query.
     * 
     * @param queryName
     *            The name of the query.
     * @return A List of IDs.
     * @throws NoSuchNameExceptionResponse
     * @throws ValidationExceptionResponse
     * @throws SecurityExceptionResponse
     * @throws ImplementationExceptionResponse
     */
    List<String> getSubscriptionIds(final String queryName) throws ImplementationExceptionResponse,
            SecurityExceptionResponse, ValidationExceptionResponse, NoSuchNameExceptionResponse;

    /**
     * Retrieves the standard version implemented by this implementation.
     * 
     * @return The implemented standard version.
     * @throws ValidationExceptionResponse
     * @throws SecurityExceptionResponse
     * @throws ImplementationExceptionResponse
     */
    String getStandardVersion() throws ImplementationExceptionResponse, SecurityExceptionResponse,
            ValidationExceptionResponse;

    /**
     * Retrieves the vendor version.
     * 
     * @return The vendor version.
     * @throws ValidationExceptionResponse
     * @throws SecurityExceptionResponse
     * @throws ImplementationExceptionResponse
     */
    String getVendorVersion() throws ImplementationExceptionResponse, SecurityExceptionResponse,
            ValidationExceptionResponse;
}
