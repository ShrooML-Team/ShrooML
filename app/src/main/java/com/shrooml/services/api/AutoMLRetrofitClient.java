package com.shrooml.services.api;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
public class AutoMLRetrofitClient {
    private final String url = "https://automl.shrooml.duckdns.org/";
    private static AutoMLRetrofitClient instance;
    private AutoMLApi autoMLApi;
    private String authToken;

    private AutoMLRetrofitClient(){
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(loggingInterceptor);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build();
        autoMLApi = retrofit.create(AutoMLApi.class);
    }
    public static synchronized AutoMLRetrofitClient getInstance(){
        if(instance == null){
            instance = new AutoMLRetrofitClient();
        }
        return instance;
    }

    public AutoMLApi getAutoMLApi() {
        return autoMLApi;
    }
    public void setAuthToken(String token){
        this.authToken = token;
    }
}
