package company.aryasoft.app.malayermarket.Modules;

import android.content.Context;
import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

import company.aryasoft.app.malayermarket.ApiModels.DataCollectionsApiModel;
import company.aryasoft.app.malayermarket.ApiModels.SliderModelApi;
import company.aryasoft.app.malayermarket.ApiModels.SpecialProductApiModel;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ApiCallBack;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ApiServiceGenerator;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ServiceRequestApi;
import retrofit2.Call;
import retrofit2.Response;

public class BasicProductModule
{
    private Context context;
    private ServiceRequestApi requestApi;
    //---------------------------
    private OnSliderLoadedListener onSliderLoadedListener;
    private OnFailureDataLoadListener onFailureLoadDataListener;
    private OnNewlyProductLoadedListener onNewlyProductLoadedListener;
    private OnBestProductLoadedListener onBestProductLoadedListener;
    private OnCollectionLoadedListener onCollectionLoadedListener;
    private OnCollectionLoadMoreListener onCollectionLoadMoreListener;


    public void SetOnCollectionLoadMoreListener(OnCollectionLoadMoreListener onCollectionLoadMoreListener)
    {
        this.onCollectionLoadMoreListener = onCollectionLoadMoreListener;
    }

    public void SetOnCollectionLoadedListener(OnCollectionLoadedListener onCollectionLoadedListener)
    {
        this.onCollectionLoadedListener = onCollectionLoadedListener;
    }

    public void SetOnBestProductLoadedListener(OnBestProductLoadedListener onBestProductLoadedListener)
    {
        this.onBestProductLoadedListener = onBestProductLoadedListener;
    }

    public void SetOnNewlyProductLoadedListener(OnNewlyProductLoadedListener onNewlyProductLoadedListener)
    {
        this.onNewlyProductLoadedListener = onNewlyProductLoadedListener;
    }

    public void SetOnFailureLoadDataListener(OnFailureDataLoadListener onFailureLoadDataListener)
    {
        this.onFailureLoadDataListener = onFailureLoadDataListener;
    }

    public void SetOnSliderLoadedListener(OnSliderLoadedListener onSliderLoadedListener)
    {
        this.onSliderLoadedListener = onSliderLoadedListener;
    }

    public BasicProductModule(Context context)
    {
        this.context = context;
        this.requestApi = ApiServiceGenerator.GetApiService();
    }


    public void BeginLoad()
    {

        Call<List<SliderModelApi>> GetSlider = requestApi.GetSlider();
        GetSlider.enqueue(new ApiCallBack<List<SliderModelApi>>(context, GetSlider)
        {
            @Override
            public void onResponse(Call<List<SliderModelApi>> call, Response<List<SliderModelApi>> response)
            {
                ArrayList<SliderModelApi> DataList = new ArrayList<>(response.body()!=null ? response.body():new ArrayList<SliderModelApi>());
                onSliderLoadedListener.OnSliderLoaded(DataList);
                GetNewlyProduct();
            }
            @Override
            public void onFailure(@NonNull Call<List<SliderModelApi>> call, @NonNull Throwable t)
            {
                onFailureLoadDataListener.OnFailureLoadData(t);
                super.onFailure(call, t);

            }
        });
    }

    private void GetNewlyProduct()
    {
        Call<List<SpecialProductApiModel>> GetNewlyProducts = requestApi.GetNewlyProducts(0, 3);
        GetNewlyProducts.enqueue(new ApiCallBack<List<SpecialProductApiModel>>(context, GetNewlyProducts)
        {
            @Override
            public void onResponse(Call<List<SpecialProductApiModel>> call, Response<List<SpecialProductApiModel>> response)
            {
                ArrayList<SpecialProductApiModel> DataList = new ArrayList<>(response.body()!=null ? response.body():new ArrayList<SpecialProductApiModel>());
                onNewlyProductLoadedListener.OnNewlyProductLoaded(DataList);
                GetBestProduct();
            }

            @Override
            public void onFailure(@NonNull Call<List<SpecialProductApiModel>> call, @NonNull Throwable t)
            {
                onFailureLoadDataListener.OnFailureLoadData(t);
                super.onFailure(call, t);
            }
        });
    }

    private void GetBestProduct()
    {
        Call<List<SpecialProductApiModel>> GetBestProducts = requestApi.GetBestSellingProducts(0, 3);
        GetBestProducts.enqueue(new ApiCallBack<List<SpecialProductApiModel>>(context, GetBestProducts)
        {
            @Override
            public void onResponse(Call<List<SpecialProductApiModel>> call, Response<List<SpecialProductApiModel>> response)
            {
                ArrayList<SpecialProductApiModel> DataList = new ArrayList<>(response.body()!=null ? response.body():new ArrayList<SpecialProductApiModel>());
                onBestProductLoadedListener.OnBestProductLoaded(DataList);
                GetCollection();
            }

            @Override
            public void onFailure(@NonNull Call<List<SpecialProductApiModel>> call, @NonNull Throwable t)
            {
                onFailureLoadDataListener.OnFailureLoadData(t);
                super.onFailure(call, t);
            }
        });
    }

    private void GetCollection()
    {
        Call<List<DataCollectionsApiModel>> GetCollections = requestApi.GetCollectionData(0, 0);
        GetCollections.enqueue(new ApiCallBack<List<DataCollectionsApiModel>>(context, GetCollections)
        {
            @Override
            public void onResponse(Call<List<DataCollectionsApiModel>> call, Response<List<DataCollectionsApiModel>> response)
            {
                ArrayList<DataCollectionsApiModel> DataList = new ArrayList<>(response.body()!=null ? response.body():new ArrayList<DataCollectionsApiModel>());
                onCollectionLoadedListener.OnCollectionLoaded(DataList);
            }

            @Override
            public void onFailure(@NonNull Call<List<DataCollectionsApiModel>> call, @NonNull Throwable t)
            {
                onFailureLoadDataListener.OnFailureLoadData(t);
                super.onFailure(call, t);
            }
        });
    }

    public void GetCollectionLoadMore(int ListOffsetNumber)
    {
        Call<List<DataCollectionsApiModel>> GetCollections = requestApi.GetCollectionData(ListOffsetNumber, 0);
        GetCollections.enqueue(new ApiCallBack<List<DataCollectionsApiModel>>(context, GetCollections)
        {
            @Override
            public void onResponse(Call<List<DataCollectionsApiModel>> call, Response<List<DataCollectionsApiModel>> response)
            {
                ArrayList<DataCollectionsApiModel> DataList = new ArrayList<>(response.body()!=null ? response.body():new ArrayList<DataCollectionsApiModel>());
                onCollectionLoadMoreListener.OnCollectionLoadMore(DataList);
            }

            @Override
            public void onFailure(@NonNull Call<List<DataCollectionsApiModel>> call, @NonNull Throwable t)
            {
                onFailureLoadDataListener.OnFailureLoadData(t);
                super.onFailure(call, t);
            }
        });
    }

    //--------------------------Interfaces
    public interface OnSliderLoadedListener
    {
        void OnSliderLoaded(ArrayList<SliderModelApi> SliderData);
    }

    public interface OnCollectionLoadedListener
    {
        void OnCollectionLoaded(ArrayList<DataCollectionsApiModel> CollectionData);
    }

    public interface OnNewlyProductLoadedListener
    {
        void OnNewlyProductLoaded(ArrayList<SpecialProductApiModel> NewlyProductData);
    }

    public interface OnCollectionLoadMoreListener
    {
        void OnCollectionLoadMore(ArrayList<DataCollectionsApiModel> CollectionData);
    }

    public interface OnBestProductLoadedListener
    {
        void OnBestProductLoaded(ArrayList<SpecialProductApiModel> BestProductData);
    }
}
