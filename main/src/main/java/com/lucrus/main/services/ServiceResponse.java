package com.lucrus.main.services;

import com.google.gson.annotations.Expose;

/**
 * Created by lucrus on 20/10/15.
 */
public class ServiceResponse<T> {
    @Expose
    public String status;
    @Expose
    public String error;
    @Expose
    public T result;
}
