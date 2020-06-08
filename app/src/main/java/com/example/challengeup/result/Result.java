package com.example.challengeup.result;

public abstract class Result {

    private Result() {
    }

    public final static class Success<T> extends Result {
        public T data;

        public Success(T data) {
            this.data = data;
        }
    }

    public final static class Error extends Result {
        public Exception exception;

        public Error(Exception exception) {
            this.exception = exception;
        }
    }
}

