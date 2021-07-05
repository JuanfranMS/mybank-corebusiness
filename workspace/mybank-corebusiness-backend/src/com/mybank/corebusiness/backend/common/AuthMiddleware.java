package com.mybank.corebusiness.backend.common;

/**
 * Authentication and authorization tools. To be used by services on incoming requests.
 */
public class AuthMiddleware implements AuthMiddlewareApi {

    @Override
    public void checkAuthToken(String jwtToken) {
        // TODO: accept all so far. 
    }
    
}
