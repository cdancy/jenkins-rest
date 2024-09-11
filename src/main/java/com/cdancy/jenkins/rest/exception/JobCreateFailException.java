package com.cdancy.jenkins.rest.exception;

/**
 * Author: kun.tang@daocloud.io
 * Date:2024/9/11
 * Time:16:37
 */

public class JobCreateFailException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public JobCreateFailException(final String message) {
        super(message);
    }
}
