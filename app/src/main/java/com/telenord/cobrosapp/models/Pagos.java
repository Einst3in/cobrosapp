package com.telenord.cobrosapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Pagos implements Parcelable
{

    @SerializedName("opcion")
    @Expose
    private String opcion;
    @SerializedName("contrato")
    @Expose
    private String contrato;
    @SerializedName("monto")
    @Expose
    private Double monto;
    @SerializedName("tipopago")
    @Expose
    private String tipopago;
    @SerializedName("insert")
    @Expose
    private String insert;
    @SerializedName("intcajas")
    @Expose
    private Integer intcajas;
    @SerializedName("intcantmeses")
    @Expose
    private Integer intcantmeses;
    @SerializedName("descuento")
    @Expose
    private String descuento;
    @SerializedName("mensualidad")
    @Expose
    private Double mensualidad;
    @SerializedName("descuentocalculado")
    @Expose
    private Double descuentocalculado;

    public static final String TAG_Frame_Pregunta_pago = "Pregunta_pago";
    public static final String TAG_Frame_Pregunta_pago_caja = "Pregunta_pago_caja";
    public static final String TAG_Frame_pregunta_reconexion = "Pregunta Reconexion";
    public static final String TAG_FRAME_CAJAS2 = "cajas2";
    public static final String TAG_FRAME_EXTENSION = "extensiones";
    public static final String TAG_FRAME_PAGOS_ADELANTADOS = "frame_pagos_adelantados";




    public final static Creator<Pagos> CREATOR = new Creator<Pagos>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Pagos createFromParcel(Parcel in) {
            Pagos instance = new Pagos();
            instance.opcion = ((String) in.readValue((String.class.getClassLoader())));
            instance.contrato = ((String) in.readValue((String.class.getClassLoader())));
            instance.monto = ((Double) in.readValue((Double.class.getClassLoader())));
            instance.tipopago = ((String) in.readValue((String.class.getClassLoader())));
            instance.insert = ((String) in.readValue((String.class.getClassLoader())));
            instance.intcajas = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.intcantmeses = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.descuento = ((String) in.readValue((String.class.getClassLoader())));
            instance.mensualidad = ((Double) in.readValue((Double.class.getClassLoader())));
            instance.descuentocalculado = ((Double) in.readValue((Double.class.getClassLoader())));
            return instance;
        }

        public Pagos[] newArray(int size) {
            return (new Pagos[size]);
        }

    }
            ;

    /**
     * No args constructor for use in serialization
     *
     */
    public Pagos() {
    }

    /**
     *
     * @param intcajas
     * @param descuento
     * @param tipopago
     * @param opcion
     * @param descuentocalculado
     * @param mensualidad
     * @param insert
     * @param intcantmeses
     * @param monto
     * @param contrato
     */
    public Pagos(String opcion, String contrato, Double monto, String tipopago, String insert, Integer intcajas, Integer intcantmeses, String descuento, Double mensualidad, Double descuentocalculado) {
        super();
        this.opcion = opcion;
        this.contrato = contrato;
        this.monto = monto;
        this.tipopago = tipopago;
        this.insert = insert;
        this.intcajas = intcajas;
        this.intcantmeses = intcantmeses;
        this.descuento = descuento;
        this.mensualidad = mensualidad;
        this.descuentocalculado = descuentocalculado;
    }

    public void asignarOpcion(String TAG){
        switch (TAG){
            case TAG_Frame_Pregunta_pago:{
                this.setOpcion("mensualidad");
                break;
            }

            case TAG_Frame_Pregunta_pago_caja:{
                this.setOpcion("caja");
                break;
            }

            case TAG_Frame_pregunta_reconexion:{
                this.setOpcion("reconexion");
                break;
            }

            case TAG_FRAME_CAJAS2:{
                this.setOpcion("caja2");
                break;
            }

            case TAG_FRAME_EXTENSION:{
                this.setOpcion("extension");
                break;
            }

            case TAG_FRAME_PAGOS_ADELANTADOS:{
                this.setOpcion("adelantados");
                break;
            }

            default:
                break;

        }
    }

    public String getOpcion() {
        return opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }

    public String getContrato() {
        return contrato;
    }

    public void setContrato(String contrato) {
        this.contrato = contrato;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public String getTipopago() {
        return tipopago;
    }

    public void setTipopago(String tipopago) {
        this.tipopago = tipopago;
    }

    public String getInsert() {
        return insert;
    }

    public void setInsert(String insert) {
        this.insert = insert;
    }

    public Integer getIntcajas() {
        return intcajas;
    }

    public void setIntcajas(Integer intcajas) {
        this.intcajas = intcajas;
    }

    public Integer getIntcantmeses() {
        return intcantmeses;
    }

    public void setIntcantmeses(Integer intcantmeses) {
        this.intcantmeses = intcantmeses;
    }

    public String getDescuento() {
        return descuento;
    }

    public void setDescuento(String descuento) {
        this.descuento = descuento;
    }

    public Double getMensualidad() {
        return mensualidad;
    }

    public void setMensualidad(Double mensualidad) {
        this.mensualidad = mensualidad;
    }

    public Double getDescuentocalculado() {
        return descuentocalculado;
    }

    public void setDescuentocalculado(Double descuentocalculado) {
        this.descuentocalculado = descuentocalculado;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(opcion);
        dest.writeValue(contrato);
        dest.writeValue(monto);
        dest.writeValue(tipopago);
        dest.writeValue(insert);
        dest.writeValue(intcajas);
        dest.writeValue(intcantmeses);
        dest.writeValue(descuento);
        dest.writeValue(mensualidad);
        dest.writeValue(descuentocalculado);
    }

    public int describeContents() {
        return 0;
    }

}