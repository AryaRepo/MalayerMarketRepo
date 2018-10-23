package company.aryasoft.app.malayermarket.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import company.aryasoft.app.malayermarket.Adapters.SubCategoriesAdapter;
import company.aryasoft.app.malayermarket.ApiModels.ProductGroupsApiModel;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ApiCallBack;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ApiServiceGenerator;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ServiceRequestApi;
import company.aryasoft.app.malayermarket.R;
import retrofit2.Call;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SubCategoryActivity extends AppCompatActivity
{
    private ServiceRequestApi requestApi = null;
    private int ListViewItemPosition = -1;
    private SubCategoriesAdapter SubCategoriesAdapter = null;
    private ArrayList<ProductGroupsApiModel> DataList = null;
    private ViewSwitcher viewSwitcher_sub_category = null;
    private boolean IsFirstTime = true;
    private SweetAlertDialog LoadingDialog = null;
    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

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
    protected void onCreate(Bundle savedInstanceState)
    {
        requestApi = ApiServiceGenerator.GetApiService();
        super.onCreate(savedInstanceState);
        int productGroupID = getIntent().getIntExtra("ProductGroupID", -1);
        setContentView(R.layout.activity_sub_category);
        initViews();
        GetSubCategories(productGroupID);
        SubCategoriesAdapter.setItemClick(new SubCategoriesAdapter.SubCategoryItemClick()
        {
            @Override
            public void OnClick(int Position)
            {
                LoadingDialog.show();
                ListViewItemPosition = Position;
                GetSubCategories(DataList.get(Position).ProductGroupID);
            }
        });
    }

    private void initViews()
    {
        Toolbar toolbar = findViewById(R.id.include_sub_category_toolbar);
        toolbar.setTitle("انتخاب زیر دسته بندی");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewSwitcher_sub_category = findViewById(R.id.viewSwitcher_sub_category);
        ImageView img_Loading_sub_category = findViewById(R.id.img_Loading_sub_category);
        Glide.with(this).load(R.drawable.my_load).into(img_Loading_sub_category);
        RecyclerView listSubCategories = findViewById(R.id.lst_sub_categories);
        SubCategoriesAdapter = new SubCategoriesAdapter(this);
        GridLayoutManager subCategoriesLayoutManager = new GridLayoutManager(this, 3);
        listSubCategories.setLayoutManager(subCategoriesLayoutManager);
        listSubCategories.setAdapter(SubCategoriesAdapter);
        DataList = new ArrayList<>();
        viewSwitcher_sub_category.setDisplayedChild(0);
        //----
        LoadingDialog =new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        LoadingDialog.setTitleText(getString(R.string.loading_title));
        LoadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        LoadingDialog.setCancelable(false);
    }

    private void GetSubCategories(int CategoryId)
    {
        final Call<List<ProductGroupsApiModel>> GetSubGroups = requestApi.GetProductGroups(CategoryId);
        GetSubGroups.enqueue(new ApiCallBack<List<ProductGroupsApiModel>>(this, GetSubGroups)
        {
            @Override
            public void onResponse(Call<List<ProductGroupsApiModel>> call, Response<List<ProductGroupsApiModel>> response)
            {
                if (response.isSuccessful())
                {

                    if (response.body().size() > 0)
                    {
                        ArrayList<ProductGroupsApiModel> responseDataList = new ArrayList<>(response.body());
                        SubCategoriesAdapter.RefreshData(responseDataList);
                        DataList.addAll(responseDataList);
                        if (IsFirstTime)
                        {
                            viewSwitcher_sub_category.setDisplayedChild(1);
                        }
                        else
                        {
                            LoadingDialog.dismiss();
                        }
                        IsFirstTime = false;
                    }
                    else
                    {
                        if (IsFirstTime)
                        {
                            viewSwitcher_sub_category.setDisplayedChild(1);
                        }
                        else
                        {
                            LoadingDialog.dismiss();
                        }
                        if(DataList.size()==0)
                        {
                            Toast.makeText(SubCategoryActivity.this, "گروه محصول مربوطه زیر گروهی یا کالایی ندارد.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        startActivity(new Intent(SubCategoryActivity.this, ProductActivity.class).putExtra("ProductGroupID", DataList.get(ListViewItemPosition).ProductGroupID).putExtra("ActionType", 1).putExtra("ToolbarTitle", "محصولات دسته " + DataList.get(ListViewItemPosition).ProductGroupTitle));
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<ProductGroupsApiModel>> call, @NonNull Throwable t)
            {
                viewSwitcher_sub_category.setDisplayedChild(1);
                super.onFailure(call, t);
            }
        });
    }

}
