package company.aryasoft.app.malayermarket.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import company.aryasoft.app.malayermarket.Activities.ProductActivity;
import company.aryasoft.app.malayermarket.Adapters.HomeRecyclerAdapter;
import company.aryasoft.app.malayermarket.Adapters.SliderPagerAdapter;
import company.aryasoft.app.malayermarket.Adapters.SliderTransformer;
import company.aryasoft.app.malayermarket.Adapters.SpecialRecyclerAdapter;
import company.aryasoft.app.malayermarket.ApiModels.DataCollectionsApiModel;
import company.aryasoft.app.malayermarket.ApiModels.SpecialProductApiModel;
import company.aryasoft.app.malayermarket.ApiModels.SliderModelApi;
import company.aryasoft.app.malayermarket.Modules.BasicProductModule;
import company.aryasoft.app.malayermarket.Modules.OnFailureDataLoadListener;
import company.aryasoft.app.malayermarket.R;
import company.aryasoft.app.malayermarket.Transformers.SimpleCardsPagerTransformer;
import company.aryasoft.app.malayermarket.UtilityAndHelper.ProductType;
import company.aryasoft.app.malayermarket.UtilityAndHelper.VectorConfig;
import company.aryasoft.app.malayermarket.UtilityAndHelper.VectorsModel;
import me.relex.circleindicator.CircleIndicator;


public class HomeFragment extends Fragment implements OnFailureDataLoadListener
{
    //region Variables And Objects
    private ViewSwitcher viewSwitcher1 = null;
    private RelativeLayout rel_section1 = null;
    private RelativeLayout rel_section2 = null;
    private FragmentPagerAdapter adapterViewPager;
    private RelativeLayout vp_container;
    private Handler handler;
    private ViewPager vpPager;
    private int currentPage = 0;
    private int CollectionOffsetNumber = 0;
    private ScrollView mainScrollView = null;
    private Context FragmentContext = null;
    private RecyclerView rec_section1_home_frg = null;
    private RecyclerView rec_section2_home_frg = null;
    private CircleIndicator indicator = null;
    private SpecialRecyclerAdapter NewlyProductSpecialRecyclerAdapter;
    private SpecialRecyclerAdapter BestProductSpecialRecyclerAdapter;
    private HomeRecyclerAdapter HomeAdapter;
    private BasicProductModule BasicProductModule = null;
    private LinearLayoutManager CollectionLayoutManager = null;
    private TextView txt_show_more_sp1 = null;
    private TextView txt_show_more_sp2 = null;
    private SweetAlertDialog LoadingDialog = null;
    private boolean IsCollectionLoading;
    private boolean IsCollectionDataEnded;
    //endregion

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    public HomeFragment()
    {

    }
//
//    public static HomeFragment GetInstance()
//    {
//        return new HomeFragment();
//    }


    public HomeFragment(SpecialRecyclerAdapter NewlyProductSpecialRecyclerAdapter, SpecialRecyclerAdapter BestProductSpecialRecyclerAdapter, HomeRecyclerAdapter HomeAdapter)
    {
        this.NewlyProductSpecialRecyclerAdapter = NewlyProductSpecialRecyclerAdapter;
        this.BestProductSpecialRecyclerAdapter = BestProductSpecialRecyclerAdapter;
        this.HomeAdapter = HomeAdapter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_home_layout, container, false);
        FragmentContext = view.getContext();
        LoadingDialog = new SweetAlertDialog(FragmentContext, SweetAlertDialog.PROGRESS_TYPE);
        LoadingDialog.setTitleText(getString(R.string.loading_title));
        LoadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        LoadingDialog.setCancelable(false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        initViews(view);
        BasicProductModule = new BasicProductModule(view.getContext());
        BasicProductModule.SetOnFailureLoadDataListener(this);
        initEvents();
    }

    private void initViews(View view)
    {
        viewSwitcher1 = view.findViewById(R.id.viewSwitcher1);
        ImageView dialog_img_Loading_home = view.findViewById(R.id.dialog_img_Loading_home);
        Glide.with(view.getContext()).load(R.drawable.my_load).into(dialog_img_Loading_home);
        vpPager = view.findViewById(R.id.view_pager_slider1);
        vp_container = view.findViewById(R.id.vp_container);
        mainScrollView = view.findViewById(R.id.myscr);
        txt_show_more_sp1 = view.findViewById(R.id.txt_show_more_sp1);
        txt_show_more_sp2 = view.findViewById(R.id.txt_show_more_sp2);
        rel_section1 = view.findViewById(R.id.rel_section1);
        rel_section2 = view.findViewById(R.id.rel_section2);
        RecyclerView rec_home_frg = view.findViewById(R.id.rec_home_frg);
        CollectionLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rec_home_frg.setLayoutManager(CollectionLayoutManager);
        rec_home_frg.setAdapter(HomeAdapter);
        rec_section1_home_frg = view.findViewById(R.id.rec_section1_home_frg);
        rec_section2_home_frg = view.findViewById(R.id.rec_section2_home_frg);
        indicator = view.findViewById(R.id.slider_indicator);
        ArrayList<VectorsModel> vectorsList = new ArrayList<>();
        vectorsList.add(new VectorsModel(R.drawable.chevron_left, txt_show_more_sp1, VectorConfig.MyDirType.start));
        vectorsList.add(new VectorsModel(R.drawable.chevron_left, txt_show_more_sp2, VectorConfig.MyDirType.start));
        VectorConfig.SetVectors(getResources(), vectorsList);

    }


    private int calculatePagerMargin()
    {
        float density = getResources().getDisplayMetrics().density;
        //int partialWidth = (int) (16 * density); // 16dp
        return (int) (30 * density);
    }

    private int calculatePagePadding(int pageMargin)
    {
        return 110 + pageMargin;
    }

    private void initEvents()
    {
        BasicProductModule.BeginLoad();
        txt_show_more_sp1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getContext(), ProductActivity.class).putExtra("ActionType", 2).putExtra("ToolbarTitle", "جدیدترین ها"));
            }
        });
        txt_show_more_sp2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getContext(), ProductActivity.class).putExtra("ActionType", 3).putExtra("ToolbarTitle", "محبوب ترین ها"));
            }
        });

        BasicProductModule.SetOnSliderLoadedListener(new BasicProductModule.OnSliderLoadedListener()
        {
            @Override
            public void OnSliderLoaded(ArrayList<SliderModelApi> SliderData)
            {
                if (SliderData.size() != 0)
                {
                    adapterViewPager = new SliderPagerAdapter(getChildFragmentManager(), SliderData);
                    vpPager.setClipToPadding(false);
                    int pageMargin = calculatePagerMargin();
                 //   int viewPagerPadding = calculatePagePadding(pageMargin);
                 //   vpPager.setPageMargin(pageMargin);
                //    vpPager.setPadding(viewPagerPadding, 0, viewPagerPadding, 0);
                    vpPager.setAdapter(adapterViewPager);
                    vpPager.setPageTransformer(false, new SimpleCardsPagerTransformer());
                    //-----------------------------
                    indicator.setViewPager(vpPager);
                    handler = new Handler();
                    handler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            changeSliderPage();
                        }
                    });
                    //------------
                    vp_container.setVisibility(View.VISIBLE);
                }
            }
        });
        BasicProductModule.SetOnNewlyProductLoadedListener(new BasicProductModule.OnNewlyProductLoadedListener()
        {
            @Override
            public void OnNewlyProductLoaded(ArrayList<SpecialProductApiModel> NewlyProductData)
            {
                if (NewlyProductData.size() != 0)
                {
                    LinearLayoutManager layoutManager_newly = new LinearLayoutManager(FragmentContext, LinearLayoutManager.VERTICAL, false);
                    rec_section1_home_frg.setLayoutManager(layoutManager_newly);
                    NewlyProductSpecialRecyclerAdapter.SetProductType(ProductType.NewlyProduct);
                    rec_section1_home_frg.setAdapter(NewlyProductSpecialRecyclerAdapter);
                    NewlyProductSpecialRecyclerAdapter.AddSpecialData(NewlyProductData, true);
                    rel_section1.setVisibility(View.VISIBLE);
                }
            }
        });
        BasicProductModule.SetOnBestProductLoadedListener(new BasicProductModule.OnBestProductLoadedListener()
        {
            @Override
            public void OnBestProductLoaded(ArrayList<SpecialProductApiModel> BestProductData)
            {
                if (BestProductData.size() >= 3)
                {
                    LinearLayoutManager layoutManager_best = new LinearLayoutManager(FragmentContext, LinearLayoutManager.VERTICAL, false);
                    rec_section2_home_frg.setLayoutManager(layoutManager_best);
                    rec_section2_home_frg.setAdapter(BestProductSpecialRecyclerAdapter);
                    BestProductSpecialRecyclerAdapter.SetProductType(ProductType.BestSellingProduct);
                    BestProductSpecialRecyclerAdapter.AddSpecialData(BestProductData, true);
                    rel_section2.setVisibility(View.VISIBLE);
                }
            }
        });
        BasicProductModule.SetOnCollectionLoadedListener(new BasicProductModule.OnCollectionLoadedListener()
        {
            @Override
            public void OnCollectionLoaded(ArrayList<DataCollectionsApiModel> CollectionData)
            {

                HomeAdapter.AddCollectionData(CollectionData);
                mainScrollView.fullScroll(ScrollView.FOCUS_UP);
                mainScrollView.smoothScrollTo(0, 0);
                viewSwitcher1.setDisplayedChild(1);
                rec_section1_home_frg.addOnScrollListener(new RecyclerView.OnScrollListener()
                {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy)
                    {
                        super.onScrolled(recyclerView, dx, dy);
                        if (!IsCollectionDataEnded)
                        {
                            if (HomeAdapter.getItemCount() >= 15)
                            {
                                if (IsCollectionLoading)
                                {
                                    return;
                                }
                                int VisibleItemCount = CollectionLayoutManager.getChildCount();
                                int TotalItemCount = CollectionLayoutManager.getItemCount();
                                int PastVisibleItem = CollectionLayoutManager.findFirstVisibleItemPosition();
                                if ((VisibleItemCount + PastVisibleItem) >= TotalItemCount)
                                {
                                    if (!LoadingDialog.isShowing())
                                    {
                                        LoadingDialog.show();
                                    }
                                    IsCollectionLoading = true;
                                    CollectionOffsetNumber += 15;
                                    BasicProductModule.GetCollectionLoadMore(CollectionOffsetNumber);
                                    BasicProductModule.SetOnCollectionLoadMoreListener(new BasicProductModule.OnCollectionLoadMoreListener()
                                    {
                                        @Override
                                        public void OnCollectionLoadMore(ArrayList<DataCollectionsApiModel> CollectionData)
                                        {
                                            if (CollectionData.size() > 0)
                                            {
                                                HomeAdapter.AddCollectionData(CollectionData);
                                                IsCollectionLoading = false;
                                            }
                                            else
                                            {
                                                IsCollectionDataEnded = true;
                                                IsCollectionLoading = false;
                                            }
                                            if (LoadingDialog.isShowing())
                                            {
                                                LoadingDialog.dismiss();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }
                });
            }
        });

    }

    private void changeSliderPage()
    {
        if (currentPage == vpPager.getAdapter().getCount())
        {
            currentPage = 0;
        }
        vpPager.setCurrentItem(currentPage++, true);
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                changeSliderPage();
            }
        }, 4000);
    }

    @Override
    public void OnFailureLoadData(Throwable t)
    {

        viewSwitcher1.setDisplayedChild(1);
    }
}
