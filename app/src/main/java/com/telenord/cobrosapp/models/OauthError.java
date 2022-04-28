package com.telenord.cobrosapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by aneudy on 11/7/2017.
 */

public class OauthError {
    @SerializedName("estado")
    @Expose
    private Integer estado;
    @SerializedName("error")
    @Expose
    private String error;
    @SerializedName("mensaje")
    @Expose
    private String mensaje;

    public OauthError(){}

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
