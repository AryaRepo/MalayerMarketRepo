package company.aryasoft.app.malayermarket.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import company.aryasoft.app.malayermarket.Adapters.FavouriteCartAdapter;
import company.aryasoft.app.malayermarket.DBLayer.FavouriteCart;
import company.aryasoft.app.malayermarket.R;
import company.aryasoft.app.malayermarket.UtilityAndHelper.HelperModule;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FavouriteCartActivity extends AppCompatActivity
{
    private FavouriteCartAdapter favouriteCartAdapter = null;
    private RelativeLayout rel_add_new_cart = null;
    private Dialog DLG = null;
    private SweetAlertDialog LoadingDialog = null;

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
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
    protected void onRestart()
    {
        super.onRestart();
        //-----------------------------------
        new AsyncLoadFav().execute();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_cart);
        initViews();
    }

    private void initViews()
    {
        Toolbar toolbar = findViewById(R.id.include_favourite_cart_toolbar);
        toolbar.setTitle("سبدهای من");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //-----------------------------------
        LoadingDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        LoadingDialog.setTitleText(getString(R.string.loading_title));
        LoadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        LoadingDialog.setCancelable(false);
        //-------------------------------
        rel_add_new_cart = findViewById(R.id.rel_add_new_fav_cart);
        ListView listView_favourite_carts = findViewById(R.id.listView_favourite_carts);
        favouriteCartAdapter = new FavouriteCartAdapter(FavouriteCartActivity.this);
        listView_favourite_carts.setAdapter(favouriteCartAdapter);
        new AsyncLoadFav().execute();
        rel_add_new_cart.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                android.support.v7.app.AlertDialog.Builder NewCartAlert = new android.support.v7.app.AlertDialog.Builder(v.getContext());
                NewCartAlert.setCancelable(false);
                View AlertView = View.inflate(v.getContext(), R.layout.new_basket_layout, null);
                NewCartAlert.setView(AlertView);
                final EditText edt_basket_name = AlertView.findViewById(R.id.edt_basket_name);
                Button btn_save_basket = AlertView.findViewById(R.id.btn_save_basket);
                ImageButton btn_close_fav = AlertView.findViewById(R.id.btn_close_fav);
                btn_save_basket.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (!edt_basket_name.getText().toString().isEmpty())
                        {
                            List<FavouriteCart> MyBaskets = FavouriteCart.listAll(FavouriteCart.class);// + edt_basket_name.getText().toString());
                            boolean IsBasketExist = false;
                            for (int i = 0; i < MyBaskets.size(); ++i)
                            {
                                if (MyBaskets.get(i).getCartName().equals(edt_basket_name.getText().toString()))
                                {
                                    IsBasketExist = true;
                                    break;
                                }
                            }
                            if (!IsBasketExist)
                            {
                                FavouriteCart Tb = new FavouriteCart(edt_basket_name.getText().toString());
                                Tb.save();
                                Toast.makeText(FavouriteCartActivity.this, "سبد شما باموفقیت ایجاد شد.", Toast.LENGTH_SHORT).show();
                                DLG.dismiss();
                                List<FavouriteCart> FavData = FavouriteCart.listAll(FavouriteCart.class);
                                favouriteCartAdapter.AddNewFavItems(FavData);
                            }
                            else
                            {
                                Toast.makeText(FavouriteCartActivity.this, "لطفا سبدی با نام متفاوت ایجاد کنید.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                btn_close_fav.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        DLG.dismiss();
                    }
                });
                DLG = NewCartAlert.show();
            }
        });
    }

    private class AsyncLoadFav extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            LoadingDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    favouriteCartAdapter.AddNewFavItems(FavouriteCart.listAll(FavouriteCart.class));
                }
            });
            LoadingDialog.dismiss();
            return null;
        }
    }
}
