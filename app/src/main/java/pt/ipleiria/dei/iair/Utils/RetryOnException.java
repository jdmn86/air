package pt.ipleiria.dei.iair.Utils;

import java.io.Serializable;

/**
 * Created by kxtreme on 09-11-2017.
 */

public abstract class RetryOnException<Input, Output> {

    private Integer maxTries;
    private int numberOfTry;
    private RetryHandler<Input>[] handlers;
    private RetryOnExceptionListener retryOnExceptionListener;

    public int getNumberOfTry() {
        return numberOfTry;
    }

    public RetryOnException(RetryParameters retryParameters) {
        this.maxTries = retryParameters.maxTries != null ? Math.max(1, retryParameters.maxTries) : null;
        this.handlers = retryParameters.handlers;
        this.retryOnExceptionListener = retryParameters.retryOnExceptionListener;
    }

    protected abstract Output doInTry(Input input, int tryNumber, Exception e) throws Exception;

    private Output result;

    public Output getResult() {
        return result;
    }

    public final Output execute(Input input) throws Exception {

        Exception exception = null;
        RetryHandler<Input> handler = null;

        for (int i = 1; maxTries == null || i <= maxTries; i++) {
            numberOfTry = i;
            try {
                //Prepare input
                if (retryOnExceptionListener != null)
                    retryOnExceptionListener.onBeforeDelay(numberOfTry);

                if (handler != null) {
                    if (handler.delay != null && handler.delay > 0)
                        Thread.sleep(handler.delay);
                    input = handler.prepareInput(input, numberOfTry, exception);
                    handler.numberOfTries++;
                }
                //Try execute
                if (retryOnExceptionListener != null)
                    retryOnExceptionListener.onBeforeTry(numberOfTry);
                result = doInTry(input, 0, null);
                return result;
            } catch (Exception e) {
                if (retryOnExceptionListener != null) {
                    retryOnExceptionListener.onException(numberOfTry, e);
                }

                //Check if general max tries is reached
                if (maxTries != null && numberOfTry >= maxTries) {
                    throw e;
                }

                //Get handler to Exception e
                handler = getHandlerByException(e);
                if (handler == null) {
                    throw e;
                }

                //Check if handler max tries is reached
                if (handler.maxTries != null && handler.numberOfTries >= handler.maxTries) {
                    throw e;
                }

                if (retryOnExceptionListener != null) {
                    retryOnExceptionListener.onExceptionHandled(numberOfTry, e, handler);
                }

                exception = e;

            }
        }
        return null;
    }

    public RetryHandler<Input> getHandlerByException(Exception e) {
        for (RetryHandler<Input> handler : handlers) {
            if (handler.exceptionClass.equals(e.getClass()))
                return handler;
        }
        return null;
    }

    public static class RetryParameters<Input> implements Serializable {
        private Integer maxTries;
        private RetryHandler<Input>[] handlers;
        private RetryOnExceptionListener retryOnExceptionListener;

        public void setRetryOnExceptionListener(RetryOnExceptionListener retryOnExceptionListener) {
            this.retryOnExceptionListener = retryOnExceptionListener;
        }

        public Integer getMaxTries() {
            return maxTries;
        }

        public RetryHandler<Input>[] getHandlers() {
            return handlers;
        }

        public RetryParameters(RetryHandler<Input>... handlers) {
            this.handlers = handlers;
        }

        public RetryParameters(Integer maxTries, RetryHandler<Input>... handlers) {
            this(handlers);
            this.maxTries = maxTries;
        }

        public RetryParameters(
                Integer maxTries, RetryOnExceptionListener retryOnExceptionListener,
                RetryHandler<Input>... handlers) {
            this(maxTries, handlers);
            this.retryOnExceptionListener = retryOnExceptionListener;
        }
    }

    public static class RetryOnExceptionListener {
        protected void onBeforeDelay(int numberOfTry) {
        }

        protected void onBeforeTry(int numberOfTry) {
        }

        protected void onException(int numberOfTry, Exception e) {
        }

        protected void onExceptionHandled(int numberOfTry, Exception e, RetryHandler handler) {
        }
    }

    public static class RetryHandler<Input> {

        private int numberOfTries = 1;
        private Class exceptionClass;
        private Integer maxTries = null;
        private Long delay = null;

        public int getNumberOfTries() {
            return numberOfTries;
        }

        public Class getExceptionClass() {
            return exceptionClass;
        }

        public Integer getMaxTries() {
            return maxTries;
        }

        public Long getDelay() {
            return delay;
        }

        public RetryHandler(Class exceptionClass, Integer maxTries, Long delay) {
            this.exceptionClass = exceptionClass;
            this.maxTries = maxTries;
            this.delay = delay;
        }

        protected Input prepareInput(Input input, int tryNumber, Exception e) {
            return input;
        }
    }
}
