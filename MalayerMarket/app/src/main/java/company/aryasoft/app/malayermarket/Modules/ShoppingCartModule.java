package company.aryasoft.app.malayermarket.Modules;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import company.aryasoft.app.malayermarket.ApiModels.ShoppingCartInfoApiModel;
import company.aryasoft.app.malayermarket.Models.ShoppingCartModel;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ApiCallBack;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ApiServiceGenerator;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ServiceRequestApi;
import company.aryasoft.app.malayermarket.UtilityAndHelper.ShoppingCartManger;
import retrofit2.Call;
import retrofit2.Response;

public class ShoppingCartModule
{
    private Context context;
    private ServiceRequestApi requestApi;
    private OnGetShoppingCartProductInfoListener onGetShoppingCartProductInfoListener;
    private OnFailureDataLoadListener onFailureDataLoadListener;
    public void setOnFailureDataLoadListener(OnFailureDataLoadListener onFailureDataLoadListener)
    {
        this.onFailureDataLoadListener = onFailureDataLoadListener;
    }

    public void setOnGetShoppingCartProductInfoListener(OnGetShoppingCartProductInfoListener onGetShoppingCartProductInfoListener)
    {
        this.onGetShoppingCartProductInfoListener = onGetShoppingCartProductInfoListener;
    }

    public ShoppingCartModule(Context context)
    {
        this.context = context;
        this.requestApi = ApiServiceGenerator.GetApiService();
    }

    public void GetShoppingCartProductInfo(String ShoppingCartProductIds)
    {
        final ArrayList<ShoppingCartModel> DataList = new ArrayList<>();
        Call<List<ShoppingCartInfoApiModel>> ShoppingCartInfo = requestApi.ShoppingCartInfo(ShoppingCartProductIds);
        ShoppingCartInfo.enqueue(new ApiCallBack<List<ShoppingCartInfoApiModel>>(context, ShoppingCartInfo)
        {
            @Override
            public void onResponse(Call<List<ShoppingCartInfoApiModel>> call, Response<List<ShoppingCartInfoApiModel>> response)
            {

                for (int i = 0; i < response.body().size(); ++i)
                {
                    DataList.add(new ShoppingCartModel
                            (response.body().get(i).ProductID, response.body().get(i).ProductTitle, response.body().get(i).SalesPrice,
                                    response.body().get(i).ImageName,
                                    ShoppingCartManger.FindProductCount(response.body().get(i).ProductID),
                                    response.body().get(i).CoverPrice));

                }
                onGetShoppingCartProductInfoListener.OnGetShoppingCartProductInfo(DataList);
            }
            @Override
            public void onFailure(@NonNull Call<List<ShoppingCartInfoApiModel>> call, @NonNull Throwable t)
            {
                onFailureDataLoadListener.OnFailureLoadData(t);
                super.onFailure(call, t);
            }
        });
    }

    //-----------------------------------------Interface
    public interface OnGetShoppingCartProductInfoListener
    {
        void OnGetShoppingCartProductInfo(ArrayList<ShoppingCartModel> ShoppingCartProductInfoData);
    }
}
