package company.aryasoft.app.malayermarket.Activities;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;
import cn.pedant.SweetAlert.SweetAlertDialog;
import company.aryasoft.app.malayermarket.Adapters.FavouriteCartDetailAdapter;
import company.aryasoft.app.malayermarket.DBLayer.ProductFavouriteCart;
import company.aryasoft.app.malayermarket.R;
import company.aryasoft.app.malayermarket.UtilityAndHelper.HelperModule;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FavouriteCartDetailActivity extends AppCompatActivity
{

    private FavouriteCartDetailAdapter favouriteCartDetailAdapter = null;
    private List<ProductFavouriteCart> ListFavouriteCart=null;

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
        setContentView(R.layout.activity_fav_basket_detail);
        initViews();
    }

    private void FindBasketDetail(long BasketId)
    {
        ListFavouriteCart=new ArrayList<>();
        ListFavouriteCart=ProductFavouriteCart.find(ProductFavouriteCart.class,"basket_id="+BasketId);
        favouriteCartDetailAdapter.AddNewFavItems(ListFavouriteCart);
    }

    private void initViews()
    {
        Toolbar toolbar = findViewById(R.id.include_basket_detail_toolbar);
        toolbar.setTitle("اطلاعات سبد");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        long basketId = getIntent().getLongExtra("BasketId", -1);
        ListView fav_basket_detail_list = findViewById(R.id.fav_basket_detail_list);
        favouriteCartDetailAdapter = new FavouriteCartDetailAdapter(this);
        fav_basket_detail_list.setAdapter(favouriteCartDetailAdapter);
        //-----------------------------------
        SweetAlertDialog loadingDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        loadingDialog.setTitleText(getString(R.string.loading_title));
        loadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        loadingDialog.setCancelable(false);
        //-------------------------------
        loadingDialog.show();
        FindBasketDetail(basketId);
        loadingDialog.dismiss();
    }
}
