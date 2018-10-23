package company.aryasoft.app.malayermarket.Activities;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import company.aryasoft.app.malayermarket.Adapters.ProductRecyclerAdapter;
import company.aryasoft.app.malayermarket.Adapters.SpecialRecyclerAdapter;
import company.aryasoft.app.malayermarket.ApiModels.DataCollectionsApiModel;
import company.aryasoft.app.malayermarket.ApiModels.SimpleProductApiModel;
import company.aryasoft.app.malayermarket.ApiModels.SpecialProductApiModel;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ApiCallBack;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ApiServiceGenerator;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ServiceRequestApi;
import company.aryasoft.app.malayermarket.R;
import company.aryasoft.app.malayermarket.UtilityAndHelper.GridSpacingItemDecoration;
import company.aryasoft.app.malayermarket.UtilityAndHelper.ProductType;
import retrofit2.Call;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProductActivity extends AppCompatActivity
{

    private RelativeLayout rel_noproduct = null;
    private RecyclerView rec_product_activity = null;
    private GridLayoutManager GridManager = null;
    private ArrayList<SimpleProductApiModel> TempCollectionData = null;
    private ProductRecyclerAdapter ProductAdapter = null;
    private boolean DataEnded = false;
    private int GroupId = -1;
    private ServiceRequestApi requestApi = null;
    private boolean IsLoading = false;
    private SpecialRecyclerAdapter NewlySpecialAdapter = null;
    private SpecialRecyclerAdapter BestSellingSpecialAdapter = null;
    private int ItemCounter = 0;
    private int OP = 0;
    private ViewSwitcher viewSwitcher_product = null;
    private ImageView img_Loading_product = null;
    private SweetAlertDialog LoadingDialog = null;
    private ImageView img_bg_product;

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
        }
        return true;
    }


    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestApi = ApiServiceGenerator.GetApiService();
        setContentView(R.layout.activity_product);
        img_bg_product = findViewById(R.id.img_bg_product);
        Glide.with(this).load(R.drawable.bg_splash).into(img_bg_product);
        initViews();
        OP = getIntent().getIntExtra("ActionType", 0);
        switch (OP)
        {
            case 1:
                GroupId = getIntent().getIntExtra("ProductGroupID", -1);
                GetProductDataByGroupId(GroupId);
                break;
            case 2:
                GetNewlyProduct();
                break;
            case 3:
                GetBestProduct();
                break;
            case 4:
                GetCollections();
                break;
        }

    }

    private void GetCollections()
    {
        if (!IsLoading)
        {
            viewSwitcher_product.setDisplayedChild(0);
        }
        else
        {
            LoadingDialog.show();
        }
        if (ProductAdapter == null)
        {
            TempCollectionData = new ArrayList<>();
            ProductAdapter = new ProductRecyclerAdapter(this);
            GridManager = new GridLayoutManager(this, 2);
            rec_product_activity.setLayoutManager(GridManager);
            rec_product_activity.setAdapter(ProductAdapter);
        }
        if (!DataEnded)
        {
            Call<List<DataCollectionsApiModel>> GetCollection = requestApi.GetCollectionByTypeID(ItemCounter, 10, getIntent().getIntExtra("CollectionTypeID", 0));
            GetCollection.enqueue(new ApiCallBack<List<DataCollectionsApiModel>>(this, GetCollection)
            {
                @Override
                public void onResponse(Call<List<DataCollectionsApiModel>> call, Response<List<DataCollectionsApiModel>> response)
                {
                    if (response.isSuccessful())
                    {
                        if (response.body().size() > 0)
                        {
                            for (int i = 0; i < response.body().get(0).ProductInfoList.size(); ++i)
                            {
                                SimpleProductApiModel ObjProduct = new SimpleProductApiModel();
                                ObjProduct.ProductID = response.body().get(0).ProductInfoList.get(i).ProductID;
                                ObjProduct.ImageName = response.body().get(0).ProductInfoList.get(i).ImageName;
                                ObjProduct.CoverPrice = response.body().get(0).ProductInfoList.get(i).CoverPrice;
                                ObjProduct.SalesPrice = response.body().get(0).ProductInfoList.get(i).SalesPrice;
                                ObjProduct.LabelTitle = response.body().get(0).ProductInfoList.get(i).LabelTitle;
                                ObjProduct.ProductTitle = response.body().get(0).ProductInfoList.get(i).ProductTitle;
                                ObjProduct.WareHouseID = response.body().get(0).ProductInfoList.get(i).WareHouseID;
                                ObjProduct.WareHouseTypeID = response.body().get(0).ProductInfoList.get(i).WareHouseTypeID;
                                ObjProduct.PurchasePrice = response.body().get(0).ProductInfoList.get(i).PurchasePrice;
                                TempCollectionData.add(ObjProduct);
                            }
                            ProductAdapter.AddProductItem(TempCollectionData);
                            TempCollectionData.clear();
                            if (IsLoading)
                            {
                                LoadingDialog.dismiss();
                            }
                            else
                            {
                                viewSwitcher_product.setDisplayedChild(1);
                            }
                            IsLoading = false;
                        }
                        else
                        {
                            if (IsLoading)
                            {
                                LoadingDialog.dismiss();
                            }
                            else
                            {
                                viewSwitcher_product.setDisplayedChild(1);
                            }
                            IsLoading = false;
                            DataEnded = true;
                            if (ProductAdapter.getItemCount() <= 0)
                            {
                                rec_product_activity.setVisibility(View.GONE);
                                rel_noproduct.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<DataCollectionsApiModel>> call, @NonNull Throwable t)
                {
                    super.onFailure(call, t);
                }
            });
        }

    }

    private void initViews()
    {
        Toolbar toolbar = findViewById(R.id.include_product_activity_toolbar);
        toolbar.setTitle(getIntent().getStringExtra("ToolbarTitle"));
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //-------------------
        LoadingDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        LoadingDialog.setTitleText(getString(R.string.loading_title));
        LoadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        LoadingDialog.setCancelable(false);
        viewSwitcher_product = findViewById(R.id.viewSwitcher_product);
        img_Loading_product = findViewById(R.id.img_Loading_product);
        Glide.with(this).load(R.drawable.my_load).into(img_Loading_product);
        rel_noproduct = findViewById(R.id.rel_Noproduct);
        rec_product_activity = findViewById(R.id.rec_product_activity);
        rec_product_activity.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                //-----------------------------------------------------
                if (IsLoading)
                {
                    return;
                }
                int VisibleItemCount = GridManager.getChildCount();
                int TotalItemCount = GridManager.getItemCount();
                int PastVisibleItem = GridManager.findFirstVisibleItemPosition();
                if (PastVisibleItem + VisibleItemCount == TotalItemCount && TotalItemCount != 0)
                {
                    switch (OP)
                    {
                        case 1:

                            if (ProductAdapter.getItemCount() >= 15)
                            {
                                IsLoading = true;
                                ItemCounter += 15;
                                GetProductDataByGroupId(GroupId);
                            }
                            break;
                        case 2:

                            if (NewlySpecialAdapter.getItemCount() >= 15)
                            {
                                IsLoading = true;
                                ItemCounter += 15;
                                GetNewlyProduct();
                            }
                            break;
                        case 3:
                            if (BestSellingSpecialAdapter.getItemCount() >= 15)
                            {
                                IsLoading = true;
                                ItemCounter += 15;
                                GetBestProduct();
                            }
                            break;
                    }
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

    }

    private void GetNewlyProduct()
    {
        if (!IsLoading)
        {
            viewSwitcher_product.setDisplayedChild(0);
        }
        else
        {
            LoadingDialog.show();
        }
        if (NewlySpecialAdapter == null)
        {
            NewlySpecialAdapter = new SpecialRecyclerAdapter(this, ProductType.NewlyOrBestSellingSingleMode);
            GridManager = new GridLayoutManager(this, 2);
            rec_product_activity.setLayoutManager(GridManager);
            rec_product_activity.addItemDecoration(new GridSpacingItemDecoration(5, 5, true, 0));
            rec_product_activity.setAdapter(NewlySpecialAdapter);

        }
        if (!DataEnded)
        {
            Call<List<SpecialProductApiModel>> GetNewlyProducts = requestApi.GetNewlyProducts(ItemCounter, 15);
            GetNewlyProducts.enqueue(new ApiCallBack<List<SpecialProductApiModel>>(this, GetNewlyProducts)
            {
                @Override
                public void onResponse(Call<List<SpecialProductApiModel>> call, Response<List<SpecialProductApiModel>> response)
                {
                    if (response.isSuccessful())
                    {
                        if (response.body().size() > 0)
                        {
                            ArrayList<SpecialProductApiModel> DataList = new ArrayList<>(response.body()!=null ? response.body():new ArrayList<SpecialProductApiModel>());
                            if (NewlySpecialAdapter.getItemCount() == 0)
                            {

                                NewlySpecialAdapter.AddSpecialData(DataList, true);
                            }
                            else
                            {
                                NewlySpecialAdapter.AddSpecialData(DataList, false);
                            }
                            if (IsLoading)
                            {
                                LoadingDialog.dismiss();
                            }
                            else
                            {
                                viewSwitcher_product.setDisplayedChild(1);
                            }
                            IsLoading = false;
                        }
                        else
                        {
                            if (IsLoading)
                            {
                                LoadingDialog.dismiss();
                            }
                            else
                            {
                                viewSwitcher_product.setDisplayedChild(1);
                            }
                            IsLoading = false;
                            DataEnded = true;
                            if (NewlySpecialAdapter.getItemCount() <= 0)
                            {
                                rec_product_activity.setVisibility(View.GONE);
                                rel_noproduct.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                }

                @Override
                public void onFailure(@NonNull Call<List<SpecialProductApiModel>> call, @NonNull Throwable t)
                {
                    viewSwitcher_product.setDisplayedChild(1);
                    super.onFailure(call, t);
                }
            });
        }

    }

    private void GetBestProduct()
    {
        viewSwitcher_product.setDisplayedChild(0);
        if (BestSellingSpecialAdapter == null)
        {
            BestSellingSpecialAdapter = new SpecialRecyclerAdapter(this, ProductType.NewlyOrBestSellingSingleMode);
            GridManager = new GridLayoutManager(this, 2);
            rec_product_activity.setLayoutManager(GridManager);
            rec_product_activity.setAdapter(BestSellingSpecialAdapter);
        }
        if (!DataEnded)
        {
            Call<List<SpecialProductApiModel>> GetBestProducts = requestApi.GetBestSellingProducts(ItemCounter, 10);
            GetBestProducts.enqueue(new ApiCallBack<List<SpecialProductApiModel>>(this, GetBestProducts)
            {
                @Override
                public void onResponse(Call<List<SpecialProductApiModel>> call, Response<List<SpecialProductApiModel>> response)
                {
                    if (response.isSuccessful())
                    {
                        if (response.body().size() > 0)
                        {
                            ArrayList<SpecialProductApiModel> DataList = new ArrayList<>(response.body()!=null ? response.body():new ArrayList<SpecialProductApiModel>());
                            if (BestSellingSpecialAdapter.getItemCount() == 0)
                            {
                                BestSellingSpecialAdapter.AddSpecialData(DataList, true);
                            }
                            else
                            {
                                BestSellingSpecialAdapter.AddSpecialData(DataList, false);
                            }
                            // HelperModule.HideLoading();
                            IsLoading = false;
                        }
                        else
                        {
                            //HelperModule.HideLoading();
                            IsLoading = false;
                            DataEnded = true;
                            if (BestSellingSpecialAdapter.getItemCount() <= 0)
                            {
                                rec_product_activity.setVisibility(View.GONE);
                                rel_noproduct.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    viewSwitcher_product.setDisplayedChild(1);
                }

                @Override
                public void onFailure(@NonNull Call<List<SpecialProductApiModel>> call, @NonNull Throwable t)
                {
                    viewSwitcher_product.setDisplayedChild(1);
                    super.onFailure(call, t);
                }
            });
        }

    }

    private void GetProductDataByGroupId(int GroupId)
    {
        viewSwitcher_product.setDisplayedChild(0);
        if (ProductAdapter == null)
        {
            ProductAdapter = new ProductRecyclerAdapter(this);
            GridManager = new GridLayoutManager(this, 2);
            rec_product_activity.setLayoutManager(GridManager);
            rec_product_activity.setAdapter(ProductAdapter);
        }
        if (!DataEnded)
        {
            Call<List<SimpleProductApiModel>> GetProductByGroupId = requestApi.GetProductByGroupId(GroupId, ItemCounter);
            GetProductByGroupId.enqueue(new ApiCallBack<List<SimpleProductApiModel>>(ProductActivity.this, GetProductByGroupId)
            {
                @Override
                public void onResponse(Call<List<SimpleProductApiModel>> call, Response<List<SimpleProductApiModel>> response)
                {
                    if (response.isSuccessful())
                    {
                        if (response.body().size() > 0)
                        {
                            ArrayList<SimpleProductApiModel> DataList = new ArrayList<>(response.body()!=null ? response.body():new ArrayList<SimpleProductApiModel>());
                            ProductAdapter.AddProductItem(DataList);
                            viewSwitcher_product.setDisplayedChild(1);
                            IsLoading = false;
                        }
                        else
                        {
                            IsLoading = false;
                            DataEnded = true;
                            if (ProductAdapter.getItemCount() <= 0)
                            {
                                rec_product_activity.setVisibility(View.GONE);
                                rel_noproduct.setVisibility(View.VISIBLE);
                            }
                            viewSwitcher_product.setDisplayedChild(1);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<SimpleProductApiModel>> call, @NonNull Throwable t)
                {
                    viewSwitcher_product.setDisplayedChild(1);
                    super.onFailure(call, t);
                }
            });
        }
    }
}