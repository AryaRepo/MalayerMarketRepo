package company.aryasoft.app.malayermarket.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;
import java.util.List;

import company.aryasoft.app.malayermarket.Adapters.ProductPagerAdapter;
import company.aryasoft.app.malayermarket.Adapters.PropertiesAdapters;
import company.aryasoft.app.malayermarket.Adapters.SimilarProductsAdapter;
import company.aryasoft.app.malayermarket.Adapters.SliderTransformer;
import company.aryasoft.app.malayermarket.ApiModels.ProductPropertyApiModel;
import company.aryasoft.app.malayermarket.ApiModels.SimilarProductApiModel;
import company.aryasoft.app.malayermarket.BuildConfig;
import company.aryasoft.app.malayermarket.DBLayer.FavouriteCart;
import company.aryasoft.app.malayermarket.DBLayer.ProductFavouriteCart;
import company.aryasoft.app.malayermarket.Models.DetailModel;
import company.aryasoft.app.malayermarket.R;
import company.aryasoft.app.malayermarket.UtilityAndHelper.HelperModule;
import company.aryasoft.app.malayermarket.UtilityAndHelper.ShoppingCartManger;
import me.relex.circleindicator.CircleIndicator;

public class DetailFragment extends Fragment
{
    private TextView txtProductPrimaryCostDetail;
    private ViewPager vpPagerDetail;
    private TwoWayView listSimilarProducts;
    private CircleIndicator indicator;
    private ImageButton btnShare = null;
    private DetailModel detailModel = null;
    private ListView lst_product_properties = null;
    private ImageButton btn_add_to_my_basket = null;
    private Dialog DLG = null;
    private BottomSheetBehavior bottomSheetBehavior;
    private Button btnOpenFeaturesPanel;
    private ImageButton btnCloseFeaturesPanel;
    private ImageButton btn_add_to_basket = null;

    @Override
    public void onResume()
    {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK)
                {
                    if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                    {
                        closeProductFeatures();
                    }
                    if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                    {
                        getActivity().onBackPressed();
                    }


                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    public static DetailFragment newInstance(DetailModel detailModel)
    {
        DetailFragment DetailFragmentInstance = new DetailFragment();
        DetailFragmentInstance.detailModel = detailModel;
        return DetailFragmentInstance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        initViews(view);
        initEvents();
        SetupViewPager();
        HelperModule.strikeThroughTextView(txtProductPrimaryCostDetail);
        setupSimilarProductList(detailModel.SimilarProductList);
    }

    private void initViews(View view)
    {
        btn_add_to_my_basket = view.findViewById(R.id.btn_add_to_my_basket);
        TextView txt_product_sale_cost_detail = view.findViewById(R.id.txt_product_sale_cost_detail);
        txtProductPrimaryCostDetail =view.findViewById( R.id.txt_product_primary_cost_detail);
        TextView txt_product_detail = view.findViewById(R.id.txt_product_detail);
        TextView txt_score_detail = view.findViewById(R.id.txt_score_detail);
        vpPagerDetail = view.findViewById(R.id.view_pager_detail);
        listSimilarProducts =view.findViewById( R.id.list_similar_products);
        indicator = view.findViewById( R.id.indicator_detail);
        btn_add_to_basket = view.findViewById( R.id.btn_add_to_basket);
        lst_product_properties =view.findViewById( R.id.lst_product_properties);
        txt_product_detail.setText("توضیحات" + "\n" + "\n" + Html.fromHtml(detailModel.Description));
        if (detailModel.point == null)
        {
            txt_score_detail.setText("امتیاز ندارد");
        }
        else
        {
            txt_score_detail.setText(detailModel.point == 0 ? "امتیاز ندارد" : detailModel.point + " امتیاز ");
        }
        txtProductPrimaryCostDetail.setText(detailModel.CoverPrice == 0 ? "قیمت ندارد" : SetColorCoverPrice(detailModel.CoverPrice));
        txt_product_sale_cost_detail.setText(detailModel.CoverPrice == 0 ? "قیمت ندارد" : SetColorSalesPrice(detailModel.SalesPrice));
        btnShare = view.findViewById(R.id.btn_share_product);
        LinearLayout bottomSheet =view.findViewById(R.id.bottom_sheet2);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        btnOpenFeaturesPanel = view.findViewById(R.id.btn_open_features_panel);
        btnCloseFeaturesPanel =view.findViewById(R.id.btn_close_product_features_panel);
        //-----------
        RelativeLayout card_container_detail = view.findViewById(R.id.card_container_detail);
        Animation slide_up = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
        card_container_detail.startAnimation(slide_up);
    }

    private void initEvents()
    {
        btnShare.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent sharingIntent = new Intent();
                sharingIntent.setAction(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String message = getString(R.string.share_text) + "\n\n\n";
                String productAddress = BuildConfig.BaseServerKey + Base64.encodeToString(String.valueOf("api/Testapi/GetProductInfo/?ProductId=" + detailModel.ProductID).getBytes(), Base64.DEFAULT);// getResources().getString(R.string.product_full_address) + detailModel.ProductID;
                String shareBody = "<<" + detailModel.ProductTitle + ">>" + message + productAddress;
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "اشتراک گذاری از طریق:"));
            }
        });
        btnOpenFeaturesPanel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openProductFeatures();
            }
        });
        btn_add_to_basket.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!ShoppingCartManger.CartManagerIncrease(detailModel.ProductID))
                {
                    Toast.makeText(v.getContext(), "محصول شما در سبد خرید قرار گرفت", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(v.getContext(), "کاربر عزیز این محصول قبلا در سبد خرید شما اضافه شده.", Toast.LENGTH_LONG).show();
                }
            }
        });
        btnCloseFeaturesPanel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                closeProductFeatures();
            }
        });

        btn_add_to_my_basket.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (FavouriteCart.listAll(FavouriteCart.class).size() > 0)
                {
                    android.support.v7.app.AlertDialog.Builder BasketAlert = new android.support.v7.app.AlertDialog.Builder(v.getContext());
                    BasketAlert.setCancelable(false);
                    View AlertView = View.inflate(v.getContext(), R.layout.choose_fav_basket_layout, null);
                    BasketAlert.setView(AlertView);
                    final ir.hamsaa.RtlMaterialSpinner sp_basket_list = AlertView.findViewById(R.id.sp_basket_list);
                    final Button btn_save_basket = AlertView.findViewById(R.id.btn_save_basket);
                    final ImageButton btn_close_fav = AlertView.findViewById(R.id.btn_close_fav);
                    final EditText txt_product_basket_count = (EditText) AlertView.findViewById(R.id.txt_product_basket_count);
                    final List<FavouriteCart> FavList = FavouriteCart.listAll(FavouriteCart.class);
                    String[] CartNames = new String[FavList.size()];
                    for (int i = 0; i < FavList.size(); ++i)
                    {
                        CartNames[i] = FavList.get(i).getCartName();
                    }
                    btn_close_fav.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            DLG.dismiss();
                        }
                    });
                    ArrayAdapter<String> Adp = new ArrayAdapter<String>(v.getContext(), R.layout.my_spinner_item, CartNames);
                    sp_basket_list.setAdapter(Adp);
                    btn_save_basket.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            if (!(String.valueOf(txt_product_basket_count.getText().toString()).isEmpty() || String.valueOf(txt_product_basket_count.getText().toString()).length() <= 0) && sp_basket_list.getSelectedItemPosition() != 0)
                            {
                                Log.i("sp_basket_list", sp_basket_list.getSelectedItemPosition() + "");
                                if (Integer.parseInt(txt_product_basket_count.getText().toString()) != 0)
                                {

                                    List<ProductFavouriteCart> cart = ProductFavouriteCart.find(ProductFavouriteCart.class, "Product_Id=" + detailModel.ProductID + " and Basket_Id=" + FavList.get(sp_basket_list.getSelectedItemPosition() - 1).getId());
                                    if (cart.size() == 0)
                                    {
                                        FavouriteCart d = FavouriteCart.findById(FavouriteCart.class, FavList.get(sp_basket_list.getSelectedItemPosition() - 1).getId());
                                        long basketId = d.getId();
                                        ProductFavouriteCart pf = new ProductFavouriteCart();
                                        pf.BasketId = basketId;
                                        pf.ProductTitle = detailModel.ProductTitle;
                                        pf.ProductCount = Integer.parseInt(txt_product_basket_count.getText().toString());
                                        pf.ProductId = detailModel.ProductID;
                                        pf.save();
                                        Toast.makeText(getContext(), "محصول به سبدمن (علاقمندی) اضافه گردید", Toast.LENGTH_LONG).show();
                                        DLG.dismiss();
                                    }
                                    else
                                    {
                                        Toast.makeText(getContext(), "شما قبلا این محصول را در سبدتان ثبت کردید.", Toast.LENGTH_LONG).show();
                                        DLG.dismiss();
                                    }
                                }
                                else if (Integer.parseInt(txt_product_basket_count.getText().toString()) == 0)
                                {
                                    Toast.makeText(getContext(), "تعداد نمی تواند صفر باشد.", Toast.LENGTH_LONG).show();
                                }

                            }
                            else
                            {
                                Toast.makeText(getContext(), "لطفا تعداد کالا یا نام سبد را وارد نمایید.", Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                    DLG = BasketAlert.show();
                }
                else
                {
                    Toast.makeText(v.getContext(), "کاربرگرامی شما هنوز سبدی ایجاد نکردید.\n میتوانید از قسمت سبد من اینکار را انجام دهید.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private SpannableStringBuilder SetColorCoverPrice(int CoverPrice)
    {
        String myStrCoverPrice = "روی جلد " + CoverPrice + getContext().getResources().getString(R.string.price_unit);
        SpannableStringBuilder Span_CoverPrice = new SpannableStringBuilder(myStrCoverPrice);
        Span_CoverPrice.setSpan(new ForegroundColorSpan(Color.parseColor("#F44336")), String.valueOf("روی جلد").length(), myStrCoverPrice.length() - getContext().getResources().getString(R.string.price_unit).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return Span_CoverPrice;
    }

    private SpannableStringBuilder SetColorSalesPrice(int SalePrice)
    {
        String myStrSalesPrice = "برای شما " + SalePrice + " تومان";
        SpannableStringBuilder SalesSpan = new SpannableStringBuilder(myStrSalesPrice);
        SalesSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#43A047")), String.valueOf("برای شما ").length(), myStrSalesPrice.length() - +" تومان".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return SalesSpan;
    }

    private void initProperties()
    {
        ArrayList<ProductPropertyApiModel> propertyList = new ArrayList<>(detailModel.PropertyList);
        PropertiesAdapters propertiesAdapters = new PropertiesAdapters(getContext(), propertyList);
        lst_product_properties.setAdapter(propertiesAdapters);
    }

    private void openProductFeatures()
    {
        if (detailModel.PropertyList.size() > 0)
        {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
        else
        {
            Toast.makeText(getContext(), "هیج ویژگی برای این محصول ثبت نشده", Toast.LENGTH_SHORT).show();
        }
    }

    private void closeProductFeatures()
    {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void SetupViewPager()
    {
        ArrayList<String> ProductImages = new ArrayList<>();
        ProductImages.add(detailModel.ImageName);
        if (detailModel.Gallary.size() > 0)
        {
            ProductImages.addAll(detailModel.Gallary);
        }
        FragmentPagerAdapter adapterViewPager = new ProductPagerAdapter(getChildFragmentManager(), ProductImages.toArray(new String[detailModel.Gallary.size()]));
        vpPagerDetail.setAdapter(adapterViewPager);
        vpPagerDetail.setPageTransformer(true, new SliderTransformer());
        indicator.setViewPager(vpPagerDetail);
    }

    private void setupSimilarProductList(final ArrayList<SimilarProductApiModel> list)
    {
        SimilarProductsAdapter similarProductsAdapter = new SimilarProductsAdapter(getContext(), list);
        listSimilarProducts.setAdapter(similarProductsAdapter);
        listSimilarProducts.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
            {
                new Handler().post(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        detailModel = null;
                        getActivity().getIntent().removeExtra("ProductID");
                        Intent intent = getActivity().getIntent();
                        intent.putExtra("SimilarProductId", list.get(position).ProductID);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        getActivity().overridePendingTransition(0, 0);
                        getActivity().finish();
                        getActivity().overridePendingTransition(0, 0);
                        startActivity(intent);
                    }
                });
            }
        });
        initProperties();
    }


}
