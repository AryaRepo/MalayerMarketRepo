package company.aryasoft.app.malayermarket.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import cn.pedant.SweetAlert.SweetAlertDialog;
import company.aryasoft.app.malayermarket.Adapters.ProductRecyclerAdapter;
import company.aryasoft.app.malayermarket.ApiModels.SimpleProductApiModel;
import company.aryasoft.app.malayermarket.ModifiedInterfaceAndAbstracts.ModifiedTextWatcher;
import company.aryasoft.app.malayermarket.Modules.OnFailureDataLoadListener;
import company.aryasoft.app.malayermarket.Modules.SearchModule;
import company.aryasoft.app.malayermarket.R;
import company.aryasoft.app.malayermarket.UtilityAndHelper.HelperModule;

public class SearchFragment extends Fragment implements OnFailureDataLoadListener
{
    private EditText edt_search;
    private Context FragmentContext = null;
    private RecyclerView rec_search_frg = null;
    private ProductRecyclerAdapter ProductAdapter = null;
    private GridLayoutManager LayoutManager = null;
    private ImageButton btn_search = null;
    private boolean DataEnded = false;
    private boolean IsLoading = true;
    private int SearchPager = 0;
    private ViewSwitcher viewSwitcher_search_frg = null;
    private SweetAlertDialog LoadingDialog = null;
    private SearchModule SearchModule;

    @SuppressLint("ValidFragment")
    public SearchFragment(ProductRecyclerAdapter ProductAdapter)
    {
        this.ProductAdapter = ProductAdapter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        LoadingDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        LoadingDialog.setTitleText(getString(R.string.loading_title));
        LoadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        LoadingDialog.setCancelable(false);
        SearchModule = new SearchModule(getContext());
        SearchModule.SetOnFailureLoadDataListener(this);
        return inflater.inflate(R.layout.fragment_search_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        FragmentContext = view.getContext();
        initViews(view);

    }

    private void initViews(View view)
    {
        edt_search = view.findViewById( R.id.edt_search);
        viewSwitcher_search_frg =view.findViewById( R.id.viewSwitcher_search_frg);
        ImageView img_Loading_search = view.findViewById(R.id.img_Loading_search);
        Glide.with(getContext()).load(R.drawable.my_load).into(img_Loading_search);
        viewSwitcher_search_frg.setDisplayedChild(1);
        btn_search = view.findViewById( R.id.btn_search);
        rec_search_frg =view.findViewById( R.id.rec_search_frg);
        LayoutManager = new GridLayoutManager(FragmentContext, 2);
        rec_search_frg.setLayoutManager(LayoutManager);
        rec_search_frg.setAdapter(ProductAdapter);
        initEvents();
    }

    private void initEvents()
    {
        edt_search.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_SEARCH)
                {
                    Search(v.getText().toString());
                }
                return false;
            }
        });
        edt_search.addTextChangedListener(new ModifiedTextWatcher()
        {
            @Override
            public void afterTextChanged(Editable s)
            {
                if (TextUtils.isEmpty(s.toString().trim()))
                {
                    DataEnded = false;
                    IsLoading = false;
                    LoadingDialog.dismiss();
                }
            }
        });
        btn_search.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                viewSwitcher_search_frg.setDisplayedChild(0);
                HelperModule.hideSoftKey(getActivity(), edt_search);
                Search(edt_search.getText().toString());
            }
        });
        rec_search_frg.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);
                if (!DataEnded)
                {
                    if (ProductAdapter.getItemCount() >= 15)
                    {
                        int VisibleItemCount = LayoutManager.getChildCount();
                        int TotalItemCount = LayoutManager.getItemCount();
                        int PastVisibleItem = LayoutManager.findFirstVisibleItemPosition();
                        if (IsLoading)
                        {
                            return;
                        }
                        if ((VisibleItemCount + PastVisibleItem) >= TotalItemCount)
                        {
                            SearchPager += 15;
                            IsLoading = true;
                            LoadingDialog.show();
                            SearchModule.SearchLoadMore(edt_search.getText().toString(), SearchPager);
                            SearchModule.SetOnSearchingLoadMore(new SearchModule.OnSearchingLoadMoreListener()
                            {
                                @Override
                                public void OnSearchingLoadMore(ArrayList<SimpleProductApiModel> SearchedData)
                                {
                                    if (SearchedData.size() > 0)
                                    {
                                        ProductAdapter.AddProductItem(SearchedData);
                                        IsLoading = false;
                                    }
                                    else
                                    {
                                        DataEnded = true;
                                        IsLoading = false;
                                    }
                                    viewSwitcher_search_frg.setDisplayedChild(1);
                                    if (LoadingDialog.isShowing())
                                    {
                                        LoadingDialog.dismiss();
                                    }
                                }
                            });
                        }
                    }
                }
                else
                {
                    viewSwitcher_search_frg.setDisplayedChild(1);
                }
            }
        });
    }

    private void Search(String SearchText)
    {
        if (!(TextUtils.isEmpty(SearchText)))
        {
            viewSwitcher_search_frg.setDisplayedChild(0);
            ProductAdapter.GetProductList().clear();
            ProductAdapter.notifyDataSetChanged();
            SearchModule.Search(SearchText);
            SearchModule.SetSearchingListener(new SearchModule.OnSearchingListener()
            {
                @Override
                public void OnSearched(ArrayList<SimpleProductApiModel> SearchedData)
                {
                    ProductAdapter.AddProductItem(SearchedData);
                    viewSwitcher_search_frg.setDisplayedChild(1);
                }
            });
        }
        else
        {
            Toast.makeText(FragmentContext, "لطفا عبارت مورد جستجو را وارد کنید.", Toast.LENGTH_SHORT).show();
        }
        HelperModule.hideSoftKey(getActivity(), edt_search);
    }

    @Override
    public void OnFailureLoadData(Throwable t)
    {
        if (LoadingDialog.isShowing())
        {
            LoadingDialog.dismiss();
        }
        IsLoading = false;
        viewSwitcher_search_frg.setDisplayedChild(1);
    }
}