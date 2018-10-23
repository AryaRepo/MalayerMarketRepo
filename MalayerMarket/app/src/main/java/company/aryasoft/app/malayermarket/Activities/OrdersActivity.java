package company.aryasoft.app.malayermarket.Activities;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import com.airbnb.lottie.LottieAnimationView;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import company.aryasoft.app.malayermarket.Adapters.OrdersAdapters;
import company.aryasoft.app.malayermarket.ApiModels.UserOrdersListApiModel;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ApiCallBack;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ApiServiceGenerator;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ServiceRequestApi;
import company.aryasoft.app.malayermarket.R;
import company.aryasoft.app.malayermarket.UtilityAndHelper.HelperModule;
import company.aryasoft.app.malayermarket.UtilityAndHelper.SharedPreferencesHelper;
import retrofit2.Call;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OrdersActivity extends AppCompatActivity
{
    private ArrayList<UserOrdersListApiModel> OrdersDataList = null;
    private RecyclerView rec_orders = null;
    private OrdersAdapters ordersAdapters = null;
    private ServiceRequestApi requestApi = null;
    private int UserId = 0;
    private RelativeLayout rel_NoData = null;
    private LottieAnimationView orders_animation_view = null;
    //-------------------------
    private LinearLayoutManager layoutManager = null;
    private boolean IsLoading = false;
    private int OrderOffsetNumber = 0;
    private boolean OrderEnded = false;
    private SweetAlertDialog LoadingDialog = null;
    private Handler AlertHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        requestApi = ApiServiceGenerator.GetApiService();
        UserId = SharedPreferencesHelper.ReadInt("UserId");
        LoadingDialog =new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        LoadingDialog.setTitleText(getString(R.string.loading_title));
        LoadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        LoadingDialog.setCancelable(false);
        new AsyncOrderLoad().execute();
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

    private void initViews()
    {
        Toolbar toolbar = findViewById(R.id.include_orders_toolbar);
        toolbar.setTitle("سفارشات من");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        OrdersDataList = new ArrayList<>();
        rec_orders = findViewById(R.id.rec_orders);
        rel_NoData = findViewById(R.id.rel_NoData);
        orders_animation_view = findViewById(R.id.orders_animation_view);
        ordersAdapters = new OrdersAdapters(this);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rec_orders.setLayoutManager(layoutManager);
        rec_orders.setAdapter(ordersAdapters);
        //--------
        OrdersDataList = new ArrayList<>();
        if (UserId != 0)
        {
            AlertHandler = new Handler(getMainLooper());
            AlertHandler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    LoadingDialog.show();
                }
            });
            GetUserOrdersListData();

            rec_orders.addOnScrollListener(new RecyclerView.OnScrollListener()
            {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy)
                {
                    if (ordersAdapters.getItemCount() >= 15)
                    {
                        if (IsLoading)
                        {
                            return;
                        }
                        int VisibleItemCount = layoutManager.getChildCount();
                        int TotalItemCount = layoutManager.getItemCount();
                        int PastVisibleItem = layoutManager.findFirstVisibleItemPosition();
                        if ((VisibleItemCount + PastVisibleItem) >= TotalItemCount)
                        {
                            if (!HelperModule.IsShowDialogLoading)
                            {
                                LoadingDialog.show();
                            }
                            IsLoading = true;
                            OrderOffsetNumber += 15;
                            GetUserOrdersListData();
                        }
                    }
                    super.onScrolled(recyclerView, dx, dy);

                }
            });
        }
    }

    private void GetUserOrdersListData()
    {
        if (!OrderEnded)
        {
            Call<List<UserOrdersListApiModel>> GetUserOrdersList = requestApi.GetUserOrdersList(UserId, OrderOffsetNumber);
            GetUserOrdersList.enqueue(new ApiCallBack<List<UserOrdersListApiModel>>(OrdersActivity.this, GetUserOrdersList)
            {
                @Override
                public void onResponse(Call<List<UserOrdersListApiModel>> call, Response<List<UserOrdersListApiModel>> response)
                {
                    if (response.isSuccessful())
                    {
                        if (response.body().size() > 0)
                        {
                            OrdersDataList.addAll(response.body());
                            rel_NoData.setVisibility(View.GONE);
                            rec_orders.setVisibility(View.VISIBLE);
                            ordersAdapters.AddOrderListData(OrdersDataList);
                            IsLoading = false;
                            OrderEnded = false;
                            AlertHandler = new Handler(getMainLooper());
                            AlertHandler.post(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    orders_animation_view.setAnimation("motorcycle.json");
                                    orders_animation_view.playAnimation();
                                }
                            });

                        }
                        else if (response.body().size() <= 0)
                        {
                            IsLoading = false;
                            OrderEnded = true;
                            if (ordersAdapters.getItemCount() <= 0)
                            {
                                orders_animation_view.setVisibility(View.GONE);
                                rel_NoData.setVisibility(View.VISIBLE);
                            }

                        }
                    }
                    AlertHandler = new Handler(getMainLooper());
                    AlertHandler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            LoadingDialog.dismiss();
                        }
                    });

                }

                @Override
                public void onFailure(@NonNull Call<List<UserOrdersListApiModel>> call, @NonNull Throwable t)
                {
                    AlertHandler = new Handler(getMainLooper());
                    AlertHandler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            LoadingDialog.dismiss();
                        }
                    });
                    super.onFailure(call, t);
                }
            });
        }
        else
        {
            AlertHandler = new Handler(getMainLooper());
            AlertHandler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    LoadingDialog.dismiss();
                }
            });
        }

    }

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private class AsyncOrderLoad extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... params)
        {
            initViews();
            return null;
        }
    }
}
