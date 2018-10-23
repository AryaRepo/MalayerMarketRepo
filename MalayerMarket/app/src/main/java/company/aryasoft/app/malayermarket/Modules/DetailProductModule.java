package company.aryasoft.app.malayermarket.Modules;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import company.aryasoft.app.malayermarket.ApiModels.BasicProductInfoApiModel;
import company.aryasoft.app.malayermarket.ApiModels.ProductCommentApiModel;
import company.aryasoft.app.malayermarket.ApiModels.ProductPropertyApiModel;
import company.aryasoft.app.malayermarket.ApiModels.SimilarProductApiModel;
import company.aryasoft.app.malayermarket.ApiModels.SimpleProductApiModel;
import company.aryasoft.app.malayermarket.Models.CommentModel;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ApiCallBack;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ApiServiceGenerator;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ServiceRequestApi;
import retrofit2.Call;
import retrofit2.Response;

public class DetailProductModule
{
    private Context context;
    private ServiceRequestApi requestApi;
    private OnFailureDataLoadListener onFailureDataLoadListener;
    private OnGetDetailsInfoListener onGetDetailsInfoListener;
    private OnGetProductCommentListener onGetProductCommentListener;
    private OnGetSimilarProductListener onGetSimilarProductListener;
    private OnGetProductPropertyListener onGetProductPropertyListener;
    private OnRegisterProductCommentListener onRegisterProductCommentListener;

    public void setOnRegisterProductCommentListener(OnRegisterProductCommentListener onRegisterProductCommentListener)
    {
        this.onRegisterProductCommentListener = onRegisterProductCommentListener;
    }

    public void setOnGetProductPropertyListener(OnGetProductPropertyListener onGetProductPropertyListener)
    {
        this.onGetProductPropertyListener = onGetProductPropertyListener;
    }

    public void setOnGetProductCommentListener(OnGetProductCommentListener onGetProductCommentListener)
    {
        this.onGetProductCommentListener = onGetProductCommentListener;
    }

    public void setOnGetSimilarProductListener(OnGetSimilarProductListener onGetSimilarProductListener)
    {
        this.onGetSimilarProductListener = onGetSimilarProductListener;
    }

    public void setOnFailureDataLoadListener(OnFailureDataLoadListener onFailureDataLoadListener)
    {
        this.onFailureDataLoadListener = onFailureDataLoadListener;
    }

    public void setOnGetDetailsInfoListener(OnGetDetailsInfoListener onGetDetailsInfoListener)
    {
        this.onGetDetailsInfoListener = onGetDetailsInfoListener;
    }

    public DetailProductModule(Context context)
    {
        this.context = context;
        this.requestApi = ApiServiceGenerator.GetApiService();
    }

    public void GetDetailsInfo(int ProductId)
    {
        Call<BasicProductInfoApiModel> DetailInfo = requestApi.GetProductInfo(ProductId);
        DetailInfo.enqueue(new ApiCallBack<BasicProductInfoApiModel>(context, DetailInfo)
        {
            @Override
            public void onResponse(Call<BasicProductInfoApiModel> call, Response<BasicProductInfoApiModel> response)
            {
                onGetDetailsInfoListener.OnGetDetailsInfo(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<BasicProductInfoApiModel> call, @NonNull Throwable t)
            {
                onFailureDataLoadListener.OnFailureLoadData(t);
                super.onFailure(call, t);
            }
        });
    }

    public void GetProductComment(int ProductId, int OffsetNumber)
    {
        Call<List<ProductCommentApiModel>> GetProductComment = requestApi.GetProductComments(ProductId, OffsetNumber);
        GetProductComment.enqueue(new ApiCallBack<List<ProductCommentApiModel>>(context, GetProductComment)
        {
            @Override
            public void onResponse(Call<List<ProductCommentApiModel>> call, Response<List<ProductCommentApiModel>> response)
            {
                ArrayList<ProductCommentApiModel> DataList = new ArrayList<>(response.body()!=null ? response.body():new ArrayList<ProductCommentApiModel>());
                onGetProductCommentListener.OnGetProductComment(DataList);
            }

            @Override
            public void onFailure(@NonNull Call<List<ProductCommentApiModel>> call, @NonNull Throwable t)
            {
                onFailureDataLoadListener.OnFailureLoadData(t);
                super.onFailure(call, t);
            }
        });
    }

    public void GetSimilarProduct(int ProductId)
    {
        Call<List<SimilarProductApiModel>> GetSimilarProducts = requestApi.GetSimilarProducts(ProductId);
        GetSimilarProducts.enqueue(new ApiCallBack<List<SimilarProductApiModel>>(context, GetSimilarProducts)
        {
            @Override
            public void onResponse(Call<List<SimilarProductApiModel>> call, Response<List<SimilarProductApiModel>> response)
            {
                ArrayList<SimilarProductApiModel> DataList = new ArrayList<>(response.body()!=null ? response.body():new ArrayList<SimilarProductApiModel>());
                onGetSimilarProductListener.GetSimilarProduct(DataList);
            }

            @Override
            public void onFailure(@NonNull Call<List<SimilarProductApiModel>> call, @NonNull Throwable t)
            {
                onFailureDataLoadListener.OnFailureLoadData(t);
                super.onFailure(call, t);
            }
        });
    }

    public void GetProductProperty(int ProductId)
    {
        Call<List<ProductPropertyApiModel>> GetProperties = requestApi.GetProductProperty(ProductId);
        GetProperties.enqueue(new ApiCallBack<List<ProductPropertyApiModel>>(context, GetProperties)
        {
            @Override
            public void onResponse(Call<List<ProductPropertyApiModel>> call, Response<List<ProductPropertyApiModel>> response)
            {
                ArrayList<ProductPropertyApiModel> DataList = new ArrayList<>(response.body()!=null ? response.body():new ArrayList<ProductPropertyApiModel>());
                onGetProductPropertyListener.GetProductProperty(DataList);
            }

            @Override
            public void onFailure(@NonNull Call<List<ProductPropertyApiModel>> call, @NonNull Throwable t)
            {
                onFailureDataLoadListener.OnFailureLoadData(t);
                super.onFailure(call, t);
            }
        });
    }

    public void RegisterProductComment(CommentModel commentModel)
    {
        Call<Integer> addComment = requestApi.RegisterCommentWithPoint(commentModel.UserId, commentModel.CommentTitle, commentModel.CommentText, commentModel.PointRate, commentModel.ProductId);
        addComment.enqueue(new ApiCallBack<Integer>(context, addComment)
        {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response)
            {
               onRegisterProductCommentListener.OnRegisterProductComment(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t)
            {
                onFailureDataLoadListener.OnFailureLoadData(t);
                super.onFailure(call, t);
            }
        });

    }

    ///------------------------------Interfaces
    public interface OnGetDetailsInfoListener
    {
        void OnGetDetailsInfo(BasicProductInfoApiModel BasicProductInfoData);
    }

    public interface OnGetProductCommentListener
    {
        void OnGetProductComment(ArrayList<ProductCommentApiModel> ProductCommentData);
    }

    public interface OnGetSimilarProductListener
    {
        void GetSimilarProduct(ArrayList<SimilarProductApiModel> SimilarProductData);
    }

    public interface OnGetProductPropertyListener
    {
        void GetProductProperty(ArrayList<ProductPropertyApiModel> ProductPropertyData);
    }

    public interface OnRegisterProductCommentListener
    {
        void OnRegisterProductComment(Integer RegisterProductCommentState);
    }
}
