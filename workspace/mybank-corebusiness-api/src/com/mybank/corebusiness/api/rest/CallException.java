package com.mybank.corebusiness.api.rest;

/**
 * Exception containing an error message when a call to an API's method occurs. 
 */
public class CallException extends Exception {
    private static final long serialVersionUID = 1L;
    public CallException(String message) { super(message); }
    public CallException() { super(""); }
    
    /**
     * Provides the useful message, from a full exception message that supposedly 
     * contains a CallException message. 
     */
    public static String extractMsg(String rawMessage) {
        int msgIndex = rawMessage.indexOf("api.rest.CallException:");
        if (msgIndex == -1) {
            return rawMessage;
        } else {
            int msgIndexHeadless = msgIndex + "api.rest.CallException".length() + 2;
            String fromCallException = rawMessage.substring(msgIndexHeadless, rawMessage.length() - 1);
            String userMessage = fromCallException.split("</p>", 2)[0];
            return userMessage;
        }
    }
}
