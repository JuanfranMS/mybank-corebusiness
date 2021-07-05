package com.mybank.corebusiness.backend.common;

import com.mybank.corebusiness.api.rest.CallException;

/**
 * Authentication and authorization tools. To be used by services on incoming requests.
 */
public interface AuthMiddlewareApi {

    /**
     * Identifier of the HTTP cookie to contain authorization tokens.
     */
    public static final String JWTTOKEN = "jwttoken";
    
    /**
     * Indicates whether a JWT token is valid so any request with 
     * this token is allowed to proceed.
     * @throws Exception On invalid token.
     */
    void checkAuthToken(String jwtToken) throws CallException;
}
