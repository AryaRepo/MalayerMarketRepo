package company.aryasoft.app.malayermarket.Fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import company.aryasoft.app.malayermarket.Adapters.CategoryAdapter;
import company.aryasoft.app.malayermarket.ApiModels.ProductGroupsApiModel;
import company.aryasoft.app.malayermarket.ApiModels.SpecialProductApiModel;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ApiCallBack;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ApiServiceGenerator;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ServiceRequestApi;
import company.aryasoft.app.malayermarket.R;
import retrofit2.Call;
import retrofit2.Response;

public class CategoryFragment extends Fragment
{
    private ServiceRequestApi requestApi = null;
    private Context FragmentContext = null;
    private CategoryAdapter categoryAdapter = null;
    private ViewSwitcher viewSwitcher_category_frg = null;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestApi = ApiServiceGenerator.GetApiService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        FragmentContext = view.getContext();
        initViews(view);

    }

    private void initViews(View view)
    {
        viewSwitcher_category_frg = view.findViewById(R.id.viewSwitcher_category_frg);
        ImageView img_Loading_category = view.findViewById(R.id.img_Loading_category);
        Glide.with(view.getContext()).load(R.drawable.my_load).into(img_Loading_category);
        RecyclerView rec_categories = view.findViewById(R.id.rec_categories);
        categoryAdapter = new CategoryAdapter(FragmentContext);
        rec_categories.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rec_categories.setAdapter(categoryAdapter);
        new AsyncCategoryLoad().execute();
    }


    private void GetProductGroups()
    {
        Call<List<ProductGroupsApiModel>> GetProductGroups = requestApi.GetProductGroups(-1);
        GetProductGroups.enqueue(new ApiCallBack<List<ProductGroupsApiModel>>(FragmentContext, GetProductGroups)
        {
            @Override
            public void onResponse(Call<List<ProductGroupsApiModel>> call, Response<List<ProductGroupsApiModel>> response)
            {
                if (response.isSuccessful())
                {
                    if (response.body() != null)
                    {
                        ArrayList<ProductGroupsApiModel> DataList = new ArrayList<>(response.body()!=null ? response.body():new ArrayList<ProductGroupsApiModel>());
                        categoryAdapter.AddCategoryData(DataList);
                    }
                    viewSwitcher_category_frg.setDisplayedChild(1);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ProductGroupsApiModel>> call, @NonNull Throwable t)
            {
                viewSwitcher_category_frg.setDisplayedChild(1);
                super.onFailure(call, t);
            }
        });
    }

    private class AsyncCategoryLoad extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            viewSwitcher_category_frg.setDisplayedChild(0);
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            GetProductGroups();
            return null;
        }
    }
}
