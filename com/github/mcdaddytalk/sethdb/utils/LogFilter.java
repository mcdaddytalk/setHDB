package com.github.mcdaddytalk.sethdb.utils;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;


public class LogFilter extends AbstractFilter {

    private HashMap < String, ArrayList < String > > hiddenExecutors = new HashMap < String, ArrayList < String > > ();
    private static LogFilter filter;

    /**
     * Sets the Result of the filter,
     * attempts to prevent the hiddenExecutors from being chat logged.
     *
     * @param message - The message being checked for hiddenExecutors.
     */
    private Result handle(final String message) {
        if (message == null) { return Result.NEUTRAL; }
        if (this.hiddenExecutors != null && !this.hiddenExecutors.isEmpty() && this.hiddenExecutors.containsKey("commands-list")) {
            for (String word : this.hiddenExecutors.get("commands-list")) {
                if (message.toLowerCase().contains(word.toLowerCase())) {
                    return Result.DENY;
                }
            }
        }
        return Result.NEUTRAL;
    }

    /**
     * Attempts to hide the hiddenExecutors from the chat logger.
     *
     * @param event - The logger handling the message.
     */
    @Override
    public Result filter(final LogEvent event) {
        return this.handle(event.getMessage().getFormattedMessage());
    }

    /**
     * Attempts to hide the hiddenExecutors from the chat logger.
     *
     * @param logger - The logger handling the message.
     * @param level - The level of execution.
     * @param marker - The filter marker.
     * @param msg - The message caught by the filter.
     * @param t - The cached Throwable.
     * @return The result of the filter.
     */
    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final Message msg, final Throwable t) {
        return this.handle(msg.getFormattedMessage());
    }

    /**
     * Attempts to hide the hiddenExecutors from the chat logger.
     *
     * @param logger - The logger handling the message.
     * @param level - The level of execution.
     * @param marker - The filter marker.
     * @param msg - The message caught by the filter.
     * @param t - The cached Throwable.
     * @return The result of the filter.
     */
    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final Object msg, final Throwable t) {
        return this.handle(msg.toString());
    }

    /**
     * Attempts to hide the hiddenExecutors from the chat logger.
     *
     * @param logger - The logger handling the message.
     * @param level - The level of execution.
     * @param marker - The filter marker.
     * @param msg - The message caught by the filter.
     * @param params - The filter parameters.
     * @return The result of the filter.
     */
    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String msg, final Object... params) {
        return this.handle(msg);
    }

    /**
     * Adds an executor to be hidden from chat logging.
     *
     * @param log - The log identifier.
     * @param logList - The executor to be hidden.
     */
    public void addHidden(final String log, final ArrayList < String > logList) {
        this.hiddenExecutors.put(log, logList);
    }

    /**
     * Gets the currently hiddenExecutors HashMap.
     *
     * @return The current hiddenExecutors HashMap.
     */
    public HashMap<String, ArrayList<String>> getHidden() {
        return this.hiddenExecutors;
    }

    /**
     * Gets the instance of the CustomFilter.
     *
     * @param regen - If the CustomFilter should have a new instance created.
     * @return The CustomFilter instance.
     */
    public static LogFilter getFilter(final boolean regen) {
        if (filter == null || regen) {
            filter = new LogFilter();
            ((LoggerContext) LogManager.getContext(false)).getConfiguration().getLoggerConfig(LogManager.ROOT_LOGGER_NAME).addFilter(filter);
        }
        return filter;
    }
}
