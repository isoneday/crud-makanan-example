package com.iswandi.crudmakanan.network;

import com.iswandi.crudmakanan.helper.MyConstant;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by iswandisaputra on 3/13/18.
 */

public class RetrofitClient {
    private static Retrofit getRetrofit(){
        //insialisasi retrofit 2
        Retrofit r = new Retrofit.Builder()
                .baseUrl(MyConstant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return r;
    }
    public static RestAPI getInstaceRetrofit(){
        return getRetrofit().create(RestAPI.class);
    }
}
