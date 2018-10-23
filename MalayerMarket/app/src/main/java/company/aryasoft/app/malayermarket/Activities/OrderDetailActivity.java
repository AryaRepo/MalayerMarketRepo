package company.aryasoft.app.malayermarket.Activities;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import company.aryasoft.app.malayermarket.Adapters.OrderDetailAdapter;
import company.aryasoft.app.malayermarket.ApiModels.SimilarProductApiModel;
import company.aryasoft.app.malayermarket.ApiModels.UserOrderDetailApiModel;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ApiCallBack;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ApiServiceGenerator;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ServiceRequestApi;
import company.aryasoft.app.malayermarket.R;
import retrofit2.Call;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OrderDetailActivity extends AppCompatActivity
{
    private ListView order_detail_list = null;
    private ServiceRequestApi requestApi = null;
    private int OrderId = 0;
    private OrderDetailAdapter orderDetailAdapter = null;
    private TextView order_detail_TotalPrice=null;
    private SweetAlertDialog LoadingDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        requestApi = ApiServiceGenerator.GetApiService();
        initViews();
    }

    private void initViews()
    {
        OrderId = getIntent().getIntExtra("OrderId", 0);
        order_detail_list = findViewById( R.id.order_detail_list);
        order_detail_TotalPrice=findViewById(R.id.order_detail_TotalPrice);
        orderDetailAdapter = new OrderDetailAdapter(this);
        order_detail_list.setAdapter(orderDetailAdapter);
        LoadingDialog =new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        LoadingDialog.setTitleText(getString(R.string.loading_title));
        LoadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        LoadingDialog.setCancelable(false);
        GetOrderDetailData();
    }

    private void GetOrderDetailData()
    {
        LoadingDialog.show();
        Call<List<UserOrderDetailApiModel>> GetOrderDetail = requestApi.GetUserOrdersList(OrderId);
        GetOrderDetail.enqueue(new ApiCallBack<List<UserOrderDetailApiModel>>(OrderDetailActivity.this, GetOrderDetail)
        {
            @Override
            public void onResponse(Call<List<UserOrderDetailApiModel>> call, Response<List<UserOrderDetailApiModel>> response)
            {
                LoadingDialog.dismiss();
                if (response.isSuccessful())
                {
                    if(response.body().size()>0)
                    {
                        ArrayList<UserOrderDetailApiModel> DataList = new ArrayList<>(response.body()!=null ? response.body():new ArrayList<UserOrderDetailApiModel>());
                        orderDetailAdapter.AddOrderData(DataList);
                        order_detail_TotalPrice.setText("مبلغ کل این سبد " + response.body().get(0).TotalPrice + " تومان بوده است");
                    }
                    else
                    {
                        orderDetailAdapter.AddOrderData(new ArrayList<UserOrderDetailApiModel>());
                        order_detail_TotalPrice.setText("مبلغ کل این سبد " + 0 + " تومان بوده است");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserOrderDetailApiModel>> call, @NonNull Throwable t)
            {
                LoadingDialog.dismiss();
                super.onFailure(call, t);
            }
        });

    }

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}