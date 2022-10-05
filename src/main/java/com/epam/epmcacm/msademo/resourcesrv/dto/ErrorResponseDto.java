package com.epam.epmcacm.msademo.resourcesrv.dto;

import lombok.Generated;
import lombok.Getter;

/**
 * Custom exception response to handle service business exceptions.
 */
@Getter
public class ErrorResponseDto {

    private String timestamp;

    private String error;

    private String code;

    private String message;

    private String trace;

    private String path;

    protected ErrorResponseDto() {
    }

    /**
     * Create new builder for ErrorResponseDto object.
     *
     * @return new builder instance.
     */
    public static Builder<?, ?> builder() {
        return new ErrorResponseDto().new Builder<>();
    }

    @Generated
    public class Builder<T extends ErrorResponseDto, B extends ErrorResponseDto.Builder<T, B>> {

        protected Builder() {
        }

        @SuppressWarnings("unchecked")
        protected B getThis() {
            return (B) this;
        }

        /**
         * Timestamp when error occurred.
         *
         * @param timestamp the timestamp in string format.
         * @return builder.
         */
        public B timestamp(String timestamp) {
            ErrorResponseDto.this.timestamp = timestamp;
            return getThis();
        }

        /**
         * Error description.
         *
         * @param error the error.
         * @return builder.
         */
        public B error(String error) {
            ErrorResponseDto.this.error = error;
            return getThis();
        }

        /**
         * Error code.
         *
         * @param code the code.
         * @return builder.
         */
        public B code(String code) {
            ErrorResponseDto.this.code = code;
            return getThis();
        }

        /**
         * Full error description.
         *
         * @param message the error.
         * @return builder.
         */
        public B message(String message) {
            ErrorResponseDto.this.message = message;
            return getThis();
        }

        /**
         * Request trace id (span).
         *
         * @param trace the trace.
         * @return builder.
         */
        public B trace(String trace) {
            ErrorResponseDto.this.trace = trace;
            return getThis();
        }

        /**
         * Request path.
         *
         * @param path the path.
         * @return builder.
         */
        public B path(String path) {
            ErrorResponseDto.this.path = path;
            return getThis();
        }

        /**
         * Build the object by this builder.
         *
         * @return configured object.
         */
        public ErrorResponseDto build() {
            return ErrorResponseDto.this;
        }
    }
}
