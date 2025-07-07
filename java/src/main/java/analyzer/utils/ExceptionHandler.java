package analyzer.utils;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ExceptionHandler {
    
    private static final Logger LOGGER = Logger.getLogger(ExceptionHandler.class.getName());
    
    private ExceptionHandler() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Handles IO exceptions with proper logging
     */
    public static void handleIOException(IOException e, String operation) {
        LOGGER.log(Level.SEVERE, "IO Error during operation: " + operation, e);
    }
    
    /**
     * Handles general exceptions with proper logging
     */
    public static void handleException(Exception e, String operation) {
        LOGGER.log(Level.SEVERE, "Error during operation: " + operation, e);
    }
    
    /**
     * Handles exceptions during file processing
     */
    public static void handleFileProcessingException(Exception e, String filePath) {
        LOGGER.log(Level.WARNING, "Failed to process file: " + filePath, e);
    }
    
    /**
     * Logs warnings for non-critical issues
     */
    public static void logWarning(String message) {
        LOGGER.log(Level.WARNING, message);
    }
    
    /**
     * Logs info messages
     */
    public static void logInfo(String message) {
        LOGGER.log(Level.INFO, message);
    }
} 