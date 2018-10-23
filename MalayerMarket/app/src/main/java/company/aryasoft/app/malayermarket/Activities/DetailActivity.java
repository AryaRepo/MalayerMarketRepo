package company.aryasoft.app.malayermarket.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.nio.charset.Charset;
import java.util.ArrayList;
import cn.pedant.SweetAlert.SweetAlertDialog;
import company.aryasoft.app.malayermarket.Adapters.DetailPagerAdapter;
import company.aryasoft.app.malayermarket.ApiModels.BasicProductInfoApiModel;
import company.aryasoft.app.malayermarket.ApiModels.ProductCommentApiModel;
import company.aryasoft.app.malayermarket.ApiModels.ProductPropertyApiModel;
import company.aryasoft.app.malayermarket.ApiModels.SimilarProductApiModel;
import company.aryasoft.app.malayermarket.BuildConfig;
import company.aryasoft.app.malayermarket.Models.DetailModel;
import company.aryasoft.app.malayermarket.Modules.DetailProductModule;
import company.aryasoft.app.malayermarket.Modules.OnFailureDataLoadListener;
import company.aryasoft.app.malayermarket.R;
import company.aryasoft.app.malayermarket.UtilityAndHelper.HelperModule;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DetailActivity extends AppCompatActivity implements OnFailureDataLoadListener
{
    private TabLayout detailTabs;
    private ViewPager mainViewPager;
    private DetailModel detailModel = null;
    private TextView txt_product_name_detail = null;
    private SweetAlertDialog LoadingDialog = null;
    private DetailProductModule detailProductModule;

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        detailProductModule = new DetailProductModule(this);
        detailProductModule.setOnFailureDataLoadListener(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_detail);
        initViews();
        if (detailModel.ProductID == -1)
        {
            initIntentData();
        }
    }

    private void initViews()
    {
        LoadingDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        LoadingDialog.setTitleText(getString(R.string.loading_title));
        LoadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        LoadingDialog.setCancelable(false);
        Glide.with(this).load(R.drawable.bg_splash).into((ImageView) findViewById(R.id.img_bg_detail));
        detailTabs = findViewById(R.id.detail_tabs);
        txt_product_name_detail = findViewById(R.id.txt_product_name_detail);
        mainViewPager = findViewById(R.id.main_viewpager_detail);
        ImageButton btnBackDetail = findViewById(R.id.btn_back_detail);
        ImageButton btnShowBasket = findViewById(R.id.btn_show_basket);
        //-----------------
        LoadingDialog.show();
        detailModel = new DetailModel();
        if (getIntent() != null)
        {
            if (getIntent().getIntExtra("ProductID", -1) != -1)
            {
                detailModel.ProductID = getIntent().getIntExtra("ProductID", -1);
            }
            else
            {
                detailModel.ProductID = getIntent().getIntExtra("SimilarProductId", -1);
            }
            if (detailModel.ProductID != -1)
            {
                GetDetailsInfo(detailModel.ProductID);
            }
            btnShowBasket.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    startActivity(new Intent(DetailActivity.this, ShoppingCartActivity.class));
                    onBackPressed();
                }
            });
            btnBackDetail.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    onBackPressed();
                   // finish();
                }
            });
        }
    }

    private void initIntentData()
    {
        try
        {
            Uri uri = getIntent().getData();
            String RawData = uri != null ? uri.toString().replace(BuildConfig.BaseServerKey, "") : null;
            byte[] decodeResult = Base64.decode(RawData.getBytes(), Base64.DEFAULT);
            String productUrl = new String(decodeResult, Charset.forName("UTF-8"));
            String P_Id;
            int index = 0;
            for (int i = 0; i < productUrl.length(); ++i)
            {
                if (productUrl.charAt(i) == '=')
                {
                    index = i;
                    break;
                }
            }
            ++index;
            StringBuilder P_IdBuilder = new StringBuilder();
            for (int i = index; i < productUrl.length(); ++i)
            {
                P_IdBuilder.append(productUrl.charAt(i));
            }
            P_Id = P_IdBuilder.toString();
            detailModel.ProductID = Integer.parseInt(P_Id);
            GetDetailsInfo(Integer.parseInt(P_Id));
        } catch (Exception ignored)
        {
        }
    }

    private void GetDetailsInfo(final int ProductId)
    {
        detailProductModule.GetDetailsInfo(ProductId);
        detailProductModule.setOnGetDetailsInfoListener(new DetailProductModule.OnGetDetailsInfoListener()
        {
            @Override
            public void OnGetDetailsInfo(BasicProductInfoApiModel BasicProductInfoData)
            {
                if (BasicProductInfoData != null)
                {
                    detailModel.ProductTitle = BasicProductInfoData.ProductTitle;
                    detailModel.CoverPrice = BasicProductInfoData.CoverPrice;
                    detailModel.SalesPrice = BasicProductInfoData.SalesPrice;
                    detailModel.ImageName = BasicProductInfoData.ImageName;
                    txt_product_name_detail.setText(detailModel.ProductTitle);
                    detailModel.Description = BasicProductInfoData.Description == null ? "توضیحات ندارد" : BasicProductInfoData.Description;
                    detailModel.point = BasicProductInfoData.point == null ? 0 : BasicProductInfoData.point;
                    if (!(BasicProductInfoData.Gallary.isEmpty()))
                    {
                        detailModel.Gallary = new ArrayList<String>();
                        for (int i = 0; i < BasicProductInfoData.Gallary.size(); ++i)
                        {
                            if (BasicProductInfoData.Gallary.get(i) != null)
                            {
                                detailModel.Gallary.add(BasicProductInfoData.Gallary.get(i).ImageName);
                            }
                        }

                    }
                }
                GetComments(ProductId);
            }
        });
    }

    private void GetComments(final int ProductId)
    {
        detailProductModule.GetProductComment(ProductId, 0);
        detailProductModule.setOnGetProductCommentListener(new DetailProductModule.OnGetProductCommentListener()
        {
            @Override
            public void OnGetProductComment(ArrayList<ProductCommentApiModel> ProductCommentData)
            {
                detailModel.CommentsList = new ArrayList<>(ProductCommentData);
                GetSimilarProduct(ProductId);
            }
        });
        ///----------------------
    }

    private void GetSimilarProduct(final int ProductId)
    {
        detailProductModule.GetSimilarProduct(ProductId);
        detailProductModule.setOnGetSimilarProductListener(new DetailProductModule.OnGetSimilarProductListener()
        {
            @Override
            public void GetSimilarProduct(ArrayList<SimilarProductApiModel> SimilarProductData)
            {
                detailModel.SimilarProductList = new ArrayList<>();
                detailModel.SimilarProductList.addAll(SimilarProductData);
                GetProductProperty(ProductId);
            }
        });
    }

    private void GetProductProperty(int ProductId)
    {
        detailProductModule.GetProductProperty(ProductId);
        detailProductModule.setOnGetProductPropertyListener(new DetailProductModule.OnGetProductPropertyListener()
        {
            @Override
            public void GetProductProperty(ArrayList<ProductPropertyApiModel> ProductPropertyData)
            {
                detailModel.PropertyList = new ArrayList<>();
                detailModel.PropertyList.addAll(ProductPropertyData);
                setupViewPager(mainViewPager);
                detailTabs.setupWithViewPager(mainViewPager);
                HelperModule.changeTabsFont(DetailActivity.this, detailTabs);
                LoadingDialog.dismiss();
            }
        });
    }

    private void setupViewPager(ViewPager viewPager)
    {
        detailTabs = findViewById(R.id.detail_tabs);
        mainViewPager = findViewById(R.id.main_viewpager_detail);
        DetailPagerAdapter adapter = new DetailPagerAdapter(getSupportFragmentManager(), detailModel);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
    }


    @Override
    public void OnFailureLoadData(Throwable t)
    {
        if (!t.getMessage().equals("unexpected end of stream"))
        {
            if (LoadingDialog.isShowing())
            {
                LoadingDialog.dismiss();
            }
        }
    }
}


