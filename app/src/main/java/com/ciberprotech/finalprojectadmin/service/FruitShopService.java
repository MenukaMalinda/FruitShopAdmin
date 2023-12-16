package com.ciberprotech.finalprojectadmin.service;

import com.ciberprotech.finalprojectadmin.model.AuthResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface FruitShopService {
    @FormUrlEncoded
    @POST("loginController.php")
    Call<AuthResponse> sendVcode(@Field("email") String email);

    @FormUrlEncoded
    @POST("verifyController.php")
    Call<AuthResponse> login(@Field("vcode") String vcode);
}
