package company.aryasoft.app.malayermarket.NetworkApiCenter;


import android.content.Context;
import android.support.annotation.NonNull;

import java.net.UnknownHostException;

import company.aryasoft.app.malayermarket.UtilityAndHelper.HelperModule;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public abstract class ApiCallBack<T> implements Callback<T>
{
    private static final int Retries = 3;
    private final Call<T> call;
    private int retryCount = 0;
    private Context ApiContext;

    public ApiCallBack(Context ApiContext, Call<T> call)
    {
        this.call = call;
        this.ApiContext = ApiContext;
    }

    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable t)
    {
        if (t instanceof UnknownHostException)
        {
            HelperModule.InternetDisconnectedDialog(ApiContext);
        }
        else if (t instanceof java.net.SocketTimeoutException)
        {
            HelperModule.NoDataReceivedDialog(ApiContext);
        }
        else
        {
            if (retryCount++ < Retries)
            {
                retry();
            }
        }
    }

    private void retry()
    {
        call.clone().enqueue(this);
    }
}
