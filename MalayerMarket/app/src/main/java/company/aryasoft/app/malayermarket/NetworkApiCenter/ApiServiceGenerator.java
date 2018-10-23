package company.aryasoft.app.malayermarket.NetworkApiCenter;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import company.aryasoft.app.malayermarket.BuildConfig;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiServiceGenerator
{
    private static Retrofit retrofit;
    private static Gson gson;
    private static OkHttpClient Client;
    private static OkHttpClient.Builder httpClient;

    public static ServiceRequestApi GetApiService()
    {
        if (httpClient == null)
        {
            httpClient = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS);
            httpClient.addInterceptor(new Interceptor()
            {
                @Override
                public Response intercept(@NonNull Interceptor.Chain chain) throws IOException
                {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .addHeader("Connection", "close")
                            .header("Accept", "application/json")
                            .header("Content-Type", "application/json; charset=utf-8")
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(request);
                }
            });
        }
        if (Client == null)
        {
            Client = httpClient.build();
        }
        if (gson == null)
        {
            gson = new GsonBuilder()
                    .setLenient()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    .create();
        }
        if (retrofit == null)
        {
            Retrofit.Builder builder = new Retrofit.Builder()
                    .client(Client)
                    .baseUrl(BuildConfig.BaseServerKey)
                    .addConverterFactory(GsonConverterFactory.create(gson));
            retrofit = builder.build();
        }
        return retrofit.create(ServiceRequestApi.class);
    }

}
