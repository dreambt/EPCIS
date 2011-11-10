package org.fosstrak.epcis.utils;

/**
 * Authentication methods supported by the query and capture clients.
 */
public enum AuthenticationType {

	/**
	 * No authentication.
	 */
	NONE,

	/**
	 * Basic authentication as described in RFC 2617.
	 */
	BASIC,
	
	/**
	 * HTTPS using an X.509 certificate to authenticate the client.
	 */
	HTTPS_WITH_CLIENT_CERT
	
}
