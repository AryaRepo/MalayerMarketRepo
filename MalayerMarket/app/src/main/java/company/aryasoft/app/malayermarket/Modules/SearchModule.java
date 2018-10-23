package company.aryasoft.app.malayermarket.Modules;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import company.aryasoft.app.malayermarket.ApiModels.ProductGroupsApiModel;
import company.aryasoft.app.malayermarket.ApiModels.SimpleProductApiModel;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ApiCallBack;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ApiServiceGenerator;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ServiceRequestApi;
import retrofit2.Call;
import retrofit2.Response;

public class SearchModule
{
    private ServiceRequestApi requestApi;
    private Context context;
    private OnSearchingListener searchingListener;
    private OnFailureDataLoadListener onFailureLoadDataListener;
    private OnSearchingLoadMoreListener OnSearchingLoadMore;

    public void SetOnSearchingLoadMore(OnSearchingLoadMoreListener onSearchingLoadMore)
    {
        OnSearchingLoadMore = onSearchingLoadMore;
    }

    public void SetSearchingListener(OnSearchingListener searchingListener)
    {
        this.searchingListener = searchingListener;
    }

    public void SetOnFailureLoadDataListener(OnFailureDataLoadListener onFailureLoadDataListener)
    {
        this.onFailureLoadDataListener = onFailureLoadDataListener;
    }

    public SearchModule(Context context)
    {
        requestApi = ApiServiceGenerator.GetApiService();
        this.context = context;
    }

    public void Search(String SearchText)
    {
        Call<List<SimpleProductApiModel>> SearchCall = requestApi.Search("-1", SearchText, 0);
        SearchCall.enqueue(new ApiCallBack<List<SimpleProductApiModel>>(context, SearchCall)
        {
            @Override
            public void onResponse(Call<List<SimpleProductApiModel>> call, Response<List<SimpleProductApiModel>> response)
            {
                ArrayList<SimpleProductApiModel> DataList = new ArrayList<>(response.body()!=null ? response.body():new ArrayList<SimpleProductApiModel>());
                searchingListener.OnSearched(DataList);
            }

            @Override
            public void onFailure(@NonNull Call<List<SimpleProductApiModel>> call, @NonNull Throwable t)
            {
                onFailureLoadDataListener.OnFailureLoadData(t);
                super.onFailure(call, t);
            }
        });
    }

    public void SearchLoadMore(String SearchText, int SearchPager)
    {
        Call<List<SimpleProductApiModel>> Search = requestApi.Search("-1", SearchText, SearchPager);
        Search.enqueue(new ApiCallBack<List<SimpleProductApiModel>>(context, Search)
        {
            @Override
            public void onResponse(Call<List<SimpleProductApiModel>> call, Response<List<SimpleProductApiModel>> response)
            {
                ArrayList<SimpleProductApiModel> DataList = new ArrayList<>(response.body()!=null ? response.body():new ArrayList<SimpleProductApiModel>());
                OnSearchingLoadMore.OnSearchingLoadMore(DataList);
            }

            @Override
            public void onFailure(@NonNull Call<List<SimpleProductApiModel>> call, @NonNull Throwable t)
            {
                onFailureLoadDataListener.OnFailureLoadData(t);
                super.onFailure(call, t);
            }
        });

    }

    public interface OnSearchingListener
    {
        void OnSearched(ArrayList<SimpleProductApiModel> SearchedData);
    }

    public interface OnSearchingLoadMoreListener
    {
        void OnSearchingLoadMore(ArrayList<SimpleProductApiModel> SearchedData);
    }
}
