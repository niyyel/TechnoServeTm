package com.depixel.web.WhatsCofee.ApiWeb;

import com.depixel.web.WhatsCofee.EntityClass.HistorialOdk;
import com.depixel.web.WhatsCofee.EntityClass.ContactsClass;
import com.depixel.web.WhatsCofee.EntityClass.SmsApi;
import com.depixel.web.WhatsCofee.EntityClass.UsuarioApi;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
   // public static final String URL="http://192.168.1.61:81/";
    public static final String URL="http://190.40.155.72/";

    @GET("api/historialOdkx")
    Call<List<HistorialOdk>>listaHistorial(@Query("zona") String zona,@Query("id_responsable") String id_responsable,
                                           @Query("identrenador") String identrenador,@Query("fechaFin") String fechaFin,
            @Query("fechaInicio") String fechaInicio,@Query("idusuario") String idusuario);

   @GET("api/getNumberPbgGenero")
   Call<List<ContactsClass>>getNumberPbgGenero(@Query("dni") String dni);


    @GET("api/smsx")
    Call<List<SmsApi>>consultarSms();

    @GET("api/loginx")
    Call<List<UsuarioApi>>validarLogin(@Query("usuario") String usuario, @Query("password") String password);

    @POST("api/updatesms")
    Call<String> updateSms(@Query("id_sms") Integer id_sms);

    @POST("api/saveSms")
    Call<String> saveSms(@Query("mensaje") String mensaje,@Query("numero") String numero,@Query("dni_asesor") String dni_asesor);

}
