package com.telenord.cobrosapp.Rest

import com.telenord.cobrosapp.models.*
import com.telenord.cobrosapp.Response.ClientesResponse
import retrofit2.Call
import retrofit2.http.*
import java.util.*


interface Central {

    @FormUrlEncoded
    @POST("/users/login")
    fun Login(
            @Field("client_id") client_id: String,
            @Field("client_secret") client_secret: String,
            @Field("username") user: String,
            @Field("password") password: String,
            @Field("grant_type") grant_type: String,
            @Field("db") db: String): Call<Token>

    @FormUrlEncoded
    @POST("/users/login")
    fun getTokenByRefresh(
            @Field("client_id") client_id: String,
            @Field("client_secret") client_secret: String,
            @Field("refresh_token") refresh_token: String,
            @Field("grant_type") grant_type: String,
            @Field("db") db: String): Call<Token>

    @GET("/cobrador/info")
    fun getInfo(
            @Query("imei") imei : String) : Call<InfoResponse>


    @GET("/clientes")
    fun getClientes(@Query("q") ref: String,
                    @Query("page") page: Int,
                    @Query("fields") field: Int): Call<ClientesResponse>

    @GET("/pagos/{contrato}/balance")
    fun getBalance(@Header("imei") imei: String, @Path("contrato") Contrato: String): Call<PagoDetalle>

    @GET("/pagos/{contrato}/mensualidad")
    fun getMensualidad(@Header("imei") imei: String, @Path("contrato")Contrato: String): Call<List<Precio>>

    @POST("/pagos/{contrato}/pago")
    fun postPagos(@Header("imei") imei: String, @Path("contrato") Contrato: String,
                  @Body pago: Pago):Call<ResponseFactura>

    @GET("tareas/conceptos")
    fun getConceptos(@Query("type") type: Int): Call<ArrayList<Concepto>>

    @GET("tareasv2/averias/horarios")
    fun getHorarios(): Call<ArrayList<Horario>>

    @FormUrlEncoded
    @POST("fcm/supervisor/token")
    fun postFcmTOken(@Field("imei") imei: String,
                     @Field("token") token: String): Call<Void>
    @FormUrlEncoded
    @POST("tareas/{contrato}/averias")
    fun postAveria(@Path("contrato") Contrato: String,
                   @Field("concepto")Concepto: Int,
                   @Field("horario")Horario: String,
                   @Field("observacion")Observacion: String): Call<Void>

    @GET("/pagos/bancos")
    fun getBancos(@Header("imei") imei: String): Call<List<Banco>>

    @GET("/pagos/{contrato}/reimprimir")
    fun getReimprimir(@Header("imei") imeip: String,@Path("contrato") Contrato: String,@Query("imei")imei:String): Call<ResponseFactura>

    @GET("/pagos/{contrato}/facturacion")
    fun getDetalleFacturacion(@Header("imei") imei: String, @Path("contrato") Contrato:String):Call<ArrayList<DetalleFacturacion>>

    @FormUrlEncoded
    @POST("dispositivos/agregar")
    fun postDispositivo(@Field("imei") imei: String,@Field("usuario") user: String): Call<Void>

    @FormUrlEncoded
    @POST("/dispositivos/{imei}/impresora")
    fun postImpresora(@Path("imei")imei: String,@Field("mac") mac : String,@Field("modelo")modelo : Int): Call<Void>

    @GET("dispositivos/modelos")
    fun getModelos(): Call<ArrayList<ImpresoraModelo>>

    @GET("/dispositivos/{imei}/impresora")
    fun getImpresoras(@Path("imei")imei: String,@Query("tipo")tipo: Int) : Call<ArrayList<Impresora>>

    @DELETE("/dispositivos/{imei}/impresora/{mac}")
    fun deleteImpresora(@Path("imei")imei: String,@Path("mac")mac: String) : Call<Void>

    @PUT("/dispositivos/{imei}/impresora/{mac}")
    fun updateDefault(@Path("imei")imei: String,@Path("mac")mac: String) : Call<Void>

    @GET("pagos/{contrato}/adelantado")
    fun getValorDescuento(@Header("imei") imei: String, @Path("contrato")contrato: String, @Query("cant_meses") meses : Int): Call<PagoAdelantado>

    @GET("pagos/{contrato}/precioservicios")
    fun getPrecioServicios(@Header("imei") imei: String, @Path("contrato") contrato: String, @Query("opc_cargos")cargo: Pago.Tipo_OP) : Call<Precio>

    @POST("clientes/{contrato}/activar")
    fun postM15(@Path("contrato") contrato: String): Call<Void>

    @GET("/clientes/{contrato}/direccion")
    fun getDireccion(@Path("contrato") contrato: String):Call<Direccion>

    @GET("/clientes/contratos/ciudades")
    fun getCiudades():Call<ArrayList<Ciudad>>

    @GET("/clientes/contratos/sectores/ciudad")
    fun getSectorByCiudad(@Query("ciudad")ciudad: Int): Call<ArrayList<Sector>>

    @GET("/clientes/contratos/calles/sector")
    fun getCallesBySector(@Query("sector") sector: Int) : Call<ArrayList<Calles>>

    @GET("/clientes/contratos/edificios")
    fun getEdificios(): Call<ArrayList<Edificio>>

    @GET("/clientes/contratos/calles/ciudad")
    fun getCallesByCiudad(@Query("ciudad") ciudad: Int) : Call<ArrayList<Calles>>

    @GET("/pagos/cajas/tipos/{contrato}")
    fun getTiposCajas(@Header("imei") imei: String, @Path("contrato")contrato: String): Call<ArrayList<TipoEquipo>>

    @GET("/pagos/cajas/disponible")
    fun getCantidadStb(@Header("imei") imei: String, @Query("tipo")tipo: String):Call<Cantidad>

    @GET("/pagos/cajas/precios/{contrato}/{tipo}")
    fun getPreciosStb(@Path("contrato") contrato: String, @Path("tipo") tipo : String, @Query("cant") cantidad: Int): Call<Precio>

    @GET("pagos/internet/{contrato}/tipos")
    fun getTiposInternet(@Header("imei") imei: String, @Path("contrato") contrato: String): Call<ArrayList<TipoEquipo>>

    @GET("pagos/internet/{contrato}/planes")
    fun getPlanesInternet(@Header ("imei") imei: String, @Path("contrato") contrato: String, @Query("tipo")tipoEquipo: String): Call<ArrayList<PlanInternet>>

    @GET("pagos/cuadre")
    fun getCuadres(@Header("imei") pimei: String, @Query("fecha")fecha: String, @Query("imei")imei: String): Call<Cuadre>

    @GET("pagos/{contrato}/movimientos")
    fun getMovimientos(@Header("imei") imei: String, @Path("contrato") contrato: String) : Call<MovimientosResponse>

    @GET("ruta/{tipo}")
    fun getRuta(@Path("tipo") tipo: Int,@Query("page")pagina: Int): Call<RutaResponse>

    @GET("ruta/filtros/conceptos")
    fun getRutaConceptos() : Call<ArrayList<Concepto>>

    @GET("ruta/visitas/conceptos")
    fun getVisitasConceptos() : Call<ArrayList<Concepto>>

    @FormUrlEncoded
    @POST("ruta/visitas/{contrato}")
    fun postVisitaRuta(@Path("contrato") contrato: String, @Field("concepto") concepto: Int,@Field("lat")lat: Double , @Field("lng")lng: Double ): Call<Void>

    @GET("pagos/{contrato}/acuerdos")
    fun getAcuerdo(@Header("imei") imei: String, @Path("contrato") contrato: String):Call<Acuerdo>

    @GET("clientes/{contrato}/cajas")
    fun getSTBByContrato(@Path("contrato") contrato:String):Call<ArrayList<STB>>


    @GET("pagos/cajas/cambio/{contrato}/precio")
    fun getPreciosSTBCambio(@Header("imei") imei: String,@Path("contrato")contrato:String):Call<ArrayList<PrecioSTB>>

    @GET("clientes/{contrato}/telefonos")
    fun getTelefonos(@Path("contrato")contrato: String) : Call<Telefonos>

    @FormUrlEncoded
    @POST("clientes/{contrato}/telefonos")
    fun postTelefonos(@Path("contrato")contrato: String,@Field("telefono")telefono:String,@Field("celular")celular:String,
                      @Field("otroTelefono")otroTelefono: String,@Field("email")email: String) : Call<Void>

    @GET("tareasv2/averias/contacto")
    fun getContacto():Call<Contacto>

    //cardnet endpoints

    @POST("/pagos/{contrato}/pago")
    fun postPagosCardnet(@Header("imei") imei: String, @Path("contrato") Contrato: String,
                         @Body pago: Pago):Call<ResponseFactura>

    @GET("/pagos/pagospendientes")
    fun getpagosPendientes(@Header("imei") imei: String, @Query("cobradorid") cobradorid: Int,@Query("contrato") contrato: String): Call<List<PagosPendientes>>

    //delete el pago pendiente
    @POST("/pagos/pagospendientes/{contrato}")
    fun deletePagopendiente(@Header("imei") imei: String, @Path("contrato") Contrato: String):Call<Void>





}