package company.aryasoft.app.malayermarket.Adapters;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;

import company.aryasoft.app.malayermarket.Activities.DetailActivity;
import company.aryasoft.app.malayermarket.ApiModels.SpecialProductApiModel;
import company.aryasoft.app.malayermarket.R;
import company.aryasoft.app.malayermarket.UtilityAndHelper.HelperModule;
import company.aryasoft.app.malayermarket.UtilityAndHelper.MyBounceInterpolator;
import company.aryasoft.app.malayermarket.UtilityAndHelper.ProductCountChanged;
import company.aryasoft.app.malayermarket.UtilityAndHelper.ProductType;
import company.aryasoft.app.malayermarket.UtilityAndHelper.ShoppingCartManger;

public class SpecialRecyclerAdapter extends RecyclerView.Adapter<SpecialRecyclerAdapter.RecyclerViewHolder>
{

    private Context ViewContext = null;
    private ArrayList<SpecialProductApiModel> DataSpecialList = null;
    private SpecialRecyclerAdapter SpecialRecyclerAdapter = null;
    private ProductType productType = ProductType.None;
    private HomeRecyclerAdapter HomeAdapter = null;
    private RecyclerViewHolder Vh;
    private Drawable ButtonBasket = null;

    public void SetProductType(ProductType productType)
    {
        this.productType = productType;
        DataSpecialList = new ArrayList<>();
        notifyDataSetChanged();
    }

    private ProductCountChanged productCountChanged = null;

    public SpecialRecyclerAdapter(Context ViewContext)
    {
        this.ViewContext = ViewContext;
        DataSpecialList = new ArrayList<>();
    }

    //--------------
    public SpecialRecyclerAdapter(Context ViewContext, ProductType productType)
    {
        this.ViewContext = ViewContext;
        this.productType = productType;
        DataSpecialList = new ArrayList<>();
    }

    public void SetOnCountChange(HomeRecyclerAdapter HomeAdapter, SpecialRecyclerAdapter SpecialRecyclerAdapter, ProductCountChanged productCountChanged)
    {
        this.productCountChanged = productCountChanged;
        this.SpecialRecyclerAdapter = SpecialRecyclerAdapter;
        this.HomeAdapter = HomeAdapter;
    }

    private SpannableStringBuilder SetColorCoverPrice(int CoverPrice)
    {
        String myStrCoverPrice = "روی جلد " + CoverPrice +  " تومان";
        SpannableStringBuilder Span_CoverPrice = new SpannableStringBuilder(myStrCoverPrice);
        Span_CoverPrice.setSpan(new ForegroundColorSpan(Color.parseColor("#F44336")), String.valueOf("روی جلد").length(), myStrCoverPrice.length() - ViewContext.getResources().getString(R.string.price_unit).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return Span_CoverPrice;
    }

    private SpannableStringBuilder SetColorSalesPrice(int SalePrice)
    {
        String myStrSalesPrice = "برای شما " + SalePrice + " تومان";
        SpannableStringBuilder SalesSpan = new SpannableStringBuilder(myStrSalesPrice);
        SalesSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#43A047")), String.valueOf("برای شما ").length(), myStrSalesPrice.length() - +" تومان".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return SalesSpan;
    }


    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View RecyclerItemView = LayoutInflater.from(ViewContext).inflate(R.layout.special_rec_item_layout, parent, false);
        switch (productType)
        {
            case NewlyOrBestSellingSingleMode:
                RecyclerItemView = LayoutInflater.from(ViewContext).inflate(R.layout.product_rec_item_layout, parent, false);
                break;
        }
        return new RecyclerViewHolder(RecyclerItemView);
    }

    public void ClearItems()
    {
        DataSpecialList.clear();
        this.notifyDataSetChanged();
    }

    private void LoadData(RecyclerViewHolder holder, final ArrayList<SpecialProductApiModel> DataSpecialList)
    {
        Vh = holder;
        if (DataSpecialList.size() >= 3)
        {
            LoadCell3(holder, DataSpecialList);
            LoadCell2(holder, DataSpecialList);
            LoadCell1(holder, DataSpecialList);
            //------------------------------
            holder.sp_cell_3.setVisibility(View.VISIBLE);
            holder.sp_cell_2.setVisibility(View.VISIBLE);
            holder.sp_cell_1.setVisibility(View.VISIBLE);
            //-------------------------------
            holder.cell3_content.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ViewContext.startActivity(new
                            Intent(ViewContext, DetailActivity.class)
                            .putExtra("ProductID", DataSpecialList.get(0).ProductID));
                }
            });
            holder.cell2_content.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ViewContext.startActivity(new
                            Intent(ViewContext, DetailActivity.class)
                            .putExtra("ProductID", DataSpecialList.get(1).ProductID)
                    );
                }
            });
            holder.cell1_content.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ViewContext.startActivity(new
                            Intent(ViewContext, DetailActivity.class)
                            .putExtra("ProductID", DataSpecialList.get(2).ProductID));
                }
            });
        }
//        else if (DataSpecialList.size() == 2)
//        {
//            LoadCell3(holder, DataSpecialList);
//            LoadCell2(holder, DataSpecialList);
//            holder.sp_cell_3.setVisibility(View.VISIBLE);
//            holder.sp_cell_2.setVisibility(View.VISIBLE);
//            holder.cell3_content.setOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View v)
//                {
//                    ViewContext.startActivity(new
//                            Intent(ViewContext, DetailActivity.class)
//                            .putExtra("ProductID", DataSpecialList.get(0).ProductID));
//                }
//            });
//            holder.cell2_content.setOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View v)
//                {
//                    ViewContext.startActivity(new
//                            Intent(ViewContext, DetailActivity.class)
//                            .putExtra("ProductID", DataSpecialList.get(1).ProductID));
//                }
//            });
//        }
//        else if (DataSpecialList.size() == 1)
//        {
//            LoadCell3(holder, DataSpecialList);
//            holder.sp_cell_3.setVisibility(View.VISIBLE);
//            holder.cell3_content.setOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View v)
//                {
//                    ViewContext.startActivity(new
//                            Intent(ViewContext, DetailActivity.class)
//                            .putExtra("ProductID", DataSpecialList.get(0).ProductID)
//                    );
//                }
//            });
//        }
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position)
    {
        switch (productType)
        {
            case NewlyProduct:
                LoadData(holder, DataSpecialList);
                break;
            case BestSellingProduct:
                LoadData(holder, DataSpecialList);
                break;
            case NewlyOrBestSellingSingleMode:
                LoadNewlyOrBest(holder, position, DataSpecialList);
                holder.product_item_content.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        ViewContext.startActivity(new
                                Intent(ViewContext, DetailActivity.class).putExtra("ProductID", DataSpecialList.get(holder.getAdapterPosition()).ProductID));
                    }
                });
                break;
        }
    }

    public void AddSpecialData(ArrayList<SpecialProductApiModel> DataSpecialList,boolean isFirstLoad)
    {
        this.DataSpecialList.addAll(DataSpecialList);
        if(isFirstLoad)
        Collections.reverse(this.DataSpecialList);
        this.notifyDataSetChanged();

    }

    @Override
    public int getItemCount()
    {
        int count = 1;
        switch (productType)
        {
            case NewlyOrBestSellingSingleMode:
                count = DataSpecialList.size();
                break;
        }
        return count;
    }

    private void LoadNewlyOrBest(final RecyclerViewHolder holder, final int position, final ArrayList<SpecialProductApiModel> DataSpecialList)
    {
        if (ShoppingCartManger.IsProductExist(DataSpecialList.get(position).ProductID))
        {
            ButtonBasket = this.ViewContext.getResources().getDrawable(R.drawable.remove_my_cart_btn);
            holder.product_add_product.setBackground(ButtonBasket);
            holder.product_add_product_text.setText("حذف از سبد");
        }
        else
        {
            ButtonBasket = this.ViewContext.getResources().getDrawable(R.drawable.add_my_cart_btn);
            holder.product_add_product.setBackground(ButtonBasket);
            holder.product_add_product_text.setText("اضافه به سبد");
        }

        if (!DataSpecialList.get(position).ImageName.equals("nophoto.png"))
        {
            Glide.with(ViewContext).load(ViewContext.getResources().getString(R.string.ProductImageFolder) + DataSpecialList.get(position).ImageName).into(holder.img_product_product_activity_single);
        }
        else
        {
            Glide.with(ViewContext).load(R.drawable.no_img).into(holder.img_product_product_activity_single);
        }
        holder.txt_product_name_product_activity_single.setText(DataSpecialList.get(position).ProductTitle);
        holder.product_primary_price_product_activity_single.setText(SetColorCoverPrice(DataSpecialList.get(position).CoverPrice));
        holder.product_sell_price_product_activity_single.setText(SetColorSalesPrice(DataSpecialList.get(position).SalesPrice));
        if (DataSpecialList.get(position).CoverPrice != DataSpecialList.get(position).SalesPrice)
        {
            HelperModule.strikeThroughTextView(holder.product_primary_price_product_activity_single);
        }
        if (DataSpecialList.get(position).LabelTitle == null)
        {
            holder.product_label_product_activity_single.setVisibility(View.GONE);
        }
        else if (DataSpecialList.get(position).LabelTitle != null)
        {
            holder.product_label_product_activity_single.setText(DataSpecialList.get(position).LabelTitle);
        }
        holder.product_add_product.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!ShoppingCartManger.IsProductExist(DataSpecialList.get(position).ProductID))
                {
                    ShoppingCartManger.AddProductToCart(DataSpecialList.get(position).ProductID);
                    ///------------
                    ButtonBasket = v.getContext().getResources().getDrawable(R.drawable.remove_my_cart_btn);
                    holder.product_add_product.setBackground(ButtonBasket);
                    holder.product_add_product_text.setText("حذف از سبد");
                    //SpecialRecyclerAdapter.notifyDataSetChanged();
                    // HomeAdapter.notifyDataSetChanged();
                }
                else
                {
                    ButtonBasket = v.getContext().getResources().getDrawable(R.drawable.add_my_cart_btn);
                    ShoppingCartManger.RemoveProductById(DataSpecialList.get(position).ProductID);
                    holder.product_add_product.setBackground(ButtonBasket);
                    holder.product_add_product_text.setText("اضافه به سبد");
                    //SpecialRecyclerAdapter.notifyDataSetChanged();
                    //HomeAdapter.notifyDataSetChanged();
                }
                if (productCountChanged != null)
                {
                    productCountChanged.OnCountChanged(ShoppingCartManger.GetShoppingCart().size());
                }
                //notifyDataSetChanged();
            }
        });
    }

    void ItemChangedByIndex(int ProductId, boolean IsDelete)
    {
        for (int i = 0; i < DataSpecialList.size(); ++i)
        {
            if (DataSpecialList.get(i).ProductID == ProductId)
            {
                // SpecialRecyclerAdapter.notifyItemChanged(i);
                if (i == 0)
                {
                    if (IsDelete)
                    {
                        ButtonBasket = this.ViewContext.getResources().getDrawable(R.drawable.remove_my_cart_btn);
                        Vh.cell3_btn.setBackground(ButtonBasket);
                        Vh.cell3_btn_text.setText("حذف از سبد");
                    }
                    else
                    {
                        ButtonBasket = this.ViewContext.getResources().getDrawable(R.drawable.add_my_cart_btn);
                        Vh.cell3_btn.setBackground(ButtonBasket);
                        Vh.cell3_btn_text.setText("اضافه به سبد");
                    }
                }
                else if (i == 1)
                {
                    if (IsDelete)
                    {
                        ButtonBasket = this.ViewContext.getResources().getDrawable(R.drawable.remove_my_cart_btn);
                        Vh.cell2_btn.setBackground(ButtonBasket);
                        Vh.cell2_btn_text.setText("حذف از سبد");
                    }
                    else
                    {
                        ButtonBasket = this.ViewContext.getResources().getDrawable(R.drawable.add_my_cart_btn);
                        Vh.cell2_btn.setBackground(ViewContext.getResources().getDrawable(R.drawable.add_my_cart_btn));
                        Vh.cell2_btn_text.setText("اضافه به سبد");
                    }
                }
                else if (i == 2)
                {
                    if (IsDelete)
                    {
                        ButtonBasket = this.ViewContext.getResources().getDrawable(R.drawable.remove_my_cart_btn);
                        Vh.cell1_btn.setBackground(ButtonBasket);
                        Vh.cell1_btn_text.setText("حذف از سبد");
                    }
                    else
                    {
                        ButtonBasket = this.ViewContext.getResources().getDrawable(R.drawable.add_my_cart_btn);
                        Vh.cell1_btn.setBackground(ViewContext.getResources().getDrawable(R.drawable.add_my_cart_btn));
                        Vh.cell1_btn_text.setText("اضافه به سبد");
                    }
                }
                break;
            }
        }
    }

    private void ItemChanged(int ProductId, boolean IsDelete)
    {
        for (int i = 0; i < SpecialRecyclerAdapter.DataSpecialList.size(); ++i)
        {
            if (SpecialRecyclerAdapter.DataSpecialList.get(i).ProductID == ProductId)
            {
                if (i == 0)
                {
                    if (IsDelete)
                    {
                        ButtonBasket = this.ViewContext.getResources().getDrawable(R.drawable.remove_my_cart_btn);
                        SpecialRecyclerAdapter.Vh.cell3_btn.setBackground(ButtonBasket);
                        SpecialRecyclerAdapter.Vh.cell3_btn_text.setText("حذف از سبد");
                    }
                    else
                    {
                        ButtonBasket = this.ViewContext.getResources().getDrawable(R.drawable.add_my_cart_btn);
                        SpecialRecyclerAdapter.Vh.cell3_btn.setBackground(ButtonBasket);
                        SpecialRecyclerAdapter.Vh.cell3_btn_text.setText("اضافه به سبد");
                    }
                }
                else if (i == 1)
                {
                    if (IsDelete)
                    {
                        ButtonBasket = this.ViewContext.getResources().getDrawable(R.drawable.remove_my_cart_btn);
                        SpecialRecyclerAdapter.Vh.cell2_btn.setBackground(ButtonBasket);
                        SpecialRecyclerAdapter.Vh.cell2_btn_text.setText("حذف از سبد");
                    }
                    else
                    {
                        ButtonBasket = this.ViewContext.getResources().getDrawable(R.drawable.add_my_cart_btn);
                        SpecialRecyclerAdapter.Vh.cell2_btn.setBackground(ButtonBasket);
                        SpecialRecyclerAdapter.Vh.cell2_btn_text.setText("اضافه به سبد");
                    }
                }
                else if (i == 2)
                {
                    if (IsDelete)
                    {
                        ButtonBasket = this.ViewContext.getResources().getDrawable(R.drawable.remove_my_cart_btn);
                        SpecialRecyclerAdapter.Vh.cell1_btn.setBackground(ButtonBasket);
                        SpecialRecyclerAdapter.Vh.cell1_btn_text.setText("حذف از سبد");
                    }
                    else
                    {
                        ButtonBasket = this.ViewContext.getResources().getDrawable(R.drawable.add_my_cart_btn);
                        SpecialRecyclerAdapter.Vh.cell1_btn.setBackground(ButtonBasket);
                        SpecialRecyclerAdapter.Vh.cell1_btn_text.setText("اضافه به سبد");
                    }
                }

            }
        }
    }

    private void LoadCell3(final RecyclerViewHolder holder, final ArrayList<SpecialProductApiModel> DataSpecialList)
    {
        if (ShoppingCartManger.IsProductExist(DataSpecialList.get(0).ProductID))
        {
            ButtonBasket = this.ViewContext.getResources().getDrawable(R.drawable.remove_my_cart_btn);
            holder.cell3_btn.setBackground(ButtonBasket);
            holder.cell3_btn_text.setText("حذف از سبد");
        }
        else
        {
            ButtonBasket = this.ViewContext.getResources().getDrawable(R.drawable.add_my_cart_btn);
            holder.cell3_btn.setBackground(ButtonBasket);
            holder.cell3_btn_text.setText("اضافه به سبد");
        }
        //----------------------
        if (!DataSpecialList.get(0).ImageName.equals("nophoto.png"))
        {
            Glide.with(ViewContext).load(ViewContext.getResources().getString(R.string.ProductImageFolder) + DataSpecialList.get(0).ImageName).into(holder.img_product_cell_3_sp);
        }
        else
        {
            Glide.with(ViewContext).load(R.drawable.no_img).into(holder.img_product_cell_3_sp);
        }
        holder.txt_product_name_cell_3_sp.setText(DataSpecialList.get(0).ProductTitle);
        holder.product_primary_price_cell_3_sp.setText(SetColorCoverPrice(DataSpecialList.get(0).CoverPrice));
        holder.product_sell_price_cell_3_sp.setText(SetColorSalesPrice(DataSpecialList.get(0).SalesPrice));
        if (DataSpecialList.get(0).CoverPrice != DataSpecialList.get(0).SalesPrice)
        {
            HelperModule.strikeThroughTextView(holder.product_primary_price_cell_3_sp);
        }
        if (DataSpecialList.get(0).LabelTitle == null)
        {
            holder.product_label_cell_3_sp.setVisibility(View.GONE);
        }
        else if (DataSpecialList.get(0).LabelTitle != null)
        {
            holder.product_label_cell_3_sp.setText(DataSpecialList.get(0).LabelTitle);
        }
        holder.cell3_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!ShoppingCartManger.IsProductExist(DataSpecialList.get(0).ProductID))
                {
                    ShoppingCartManger.AddProductToCart(DataSpecialList.get(0).ProductID);
                    ItemChanged(DataSpecialList.get(0).ProductID, true);
                    //----------------
                    ButtonBasket =v.getContext().getResources().getDrawable(R.drawable.remove_my_cart_btn);
                    holder.cell3_btn.setBackground(ButtonBasket);
                    holder.cell3_btn_text.setText("حذف از سبد");
                    //-----------------
                }
                else
                {
                    ShoppingCartManger.RemoveProductById(DataSpecialList.get(0).ProductID);
                    ItemChanged(DataSpecialList.get(0).ProductID, false);
                    //----------------
                    ButtonBasket =v.getContext().getResources().getDrawable(R.drawable.add_my_cart_btn);
                    holder.cell3_btn.setBackground(ButtonBasket);
                    holder.cell3_btn_text.setText("اضافه به سبد");
                    //-----------------
                }
                productCountChanged.OnCountChanged(ShoppingCartManger.GetShoppingCart().size());
                HomeAdapter.notifyDataSetChanged();

                // SpecialRecyclerAdapter.notifyDataSetChanged();
                //ItemChangedByIndex(DataSpecialList.get(0).ProductID);
            }
        });
    }

    private void LoadCell2(final RecyclerViewHolder holder, final ArrayList<SpecialProductApiModel> DataSpecialList)
    {
        if (ShoppingCartManger.IsProductExist(DataSpecialList.get(1).ProductID))
        {
            ButtonBasket = ViewContext.getResources().getDrawable(R.drawable.remove_my_cart_btn);
            holder.cell2_btn.setBackground(ButtonBasket);
            holder.cell2_btn_text.setText("حذف از سبد");
        }
        else
        {
            ButtonBasket = ViewContext.getResources().getDrawable(R.drawable.add_my_cart_btn);
            holder.cell2_btn.setBackground(ButtonBasket);
            holder.cell2_btn_text.setText("اضافه به سبد");
        }
        if (!DataSpecialList.get(1).ImageName.equals("nophoto.png"))
        {
            Glide.with(ViewContext).load(ViewContext.getResources().getString(R.string.ProductImageFolder) + DataSpecialList.get(1).ImageName).into(holder.img_product_cell_2_sp);
        }
        else
        {
            Glide.with(ViewContext).load(R.drawable.no_img).into(holder.img_product_cell_2_sp);
        }
        holder.txt_product_name_cell_2_sp.setText(DataSpecialList.get(1).ProductTitle);
        holder.product_primary_price_cell_2_sp.setText(SetColorCoverPrice(DataSpecialList.get(1).CoverPrice));
        holder.product_sell_price_cell_2_sp.setText(SetColorSalesPrice(DataSpecialList.get(1).SalesPrice));
        if (DataSpecialList.get(1).CoverPrice != DataSpecialList.get(1).SalesPrice)
        {
            HelperModule.strikeThroughTextView(holder.product_primary_price_cell_2_sp);
        }
        if (DataSpecialList.get(1).LabelTitle == null)
        {
            holder.product_label_cell_2_sp.setVisibility(View.GONE);
        }
        else if (DataSpecialList.get(1).LabelTitle != null)
        {
            holder.product_label_cell_2_sp.setText(DataSpecialList.get(1).LabelTitle);
        }
        holder.cell2_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!ShoppingCartManger.IsProductExist(DataSpecialList.get(1).ProductID))
                {
                    ShoppingCartManger.AddProductToCart(DataSpecialList.get(1).ProductID);
                    ItemChanged(DataSpecialList.get(1).ProductID, true);
                    //----------------
                    ButtonBasket =v.getContext().getResources().getDrawable(R.drawable.remove_my_cart_btn);
                    holder.cell2_btn.setBackground(ButtonBasket);
                    holder.cell2_btn_text.setText("حذف از سبد");
                    //-----------------
                }
                else
                {
                    ShoppingCartManger.RemoveProductById(DataSpecialList.get(1).ProductID);
                    ItemChanged(DataSpecialList.get(1).ProductID, false);
                    //----------------
                    ButtonBasket =v.getContext().getResources().getDrawable(R.drawable.add_my_cart_btn);
                    holder.cell2_btn.setBackground(ButtonBasket);
                    holder.cell2_btn_text.setText("اضافه به سبد");
                    //-----------------
                }
                productCountChanged.OnCountChanged(ShoppingCartManger.GetShoppingCart().size());
                HomeAdapter.notifyDataSetChanged();
                //  SpecialRecyclerAdapter.notifyDataSetChanged();
                //ItemChangedByIndex(DataSpecialList.get(1).ProductID);
            }
        });
    }

    private void LoadCell1(final RecyclerViewHolder holder, final ArrayList<SpecialProductApiModel> DataSpecialList)
    {
        if (ShoppingCartManger.IsProductExist(DataSpecialList.get(2).ProductID))
        {
            ButtonBasket = ViewContext.getResources().getDrawable(R.drawable.remove_my_cart_btn);
            holder.cell1_btn.setBackground(ButtonBasket);
            holder.cell1_btn_text.setText("حذف از سبد");
        }
        else
        {
            ButtonBasket = ViewContext.getResources().getDrawable(R.drawable.add_my_cart_btn);
            holder.cell1_btn.setBackground(ButtonBasket);
            holder.cell1_btn_text.setText("اضافه به سبد");
        }
        if (!DataSpecialList.get(2).ImageName.equals("nophoto.png"))
        {
            Glide.with(ViewContext).load(ViewContext.getResources().getString(R.string.ProductImageFolder) + DataSpecialList.get(2).ImageName).into(holder.img_product_cell_1_sp);
        }
        else
        {
            Glide.with(ViewContext).load(R.drawable.no_img).into(holder.img_product_cell_1_sp);
        }
        holder.txt_product_name_cell_1_sp.setText(DataSpecialList.get(2).ProductTitle);
        holder.product_primary_price_cell_1_sp.setText(SetColorCoverPrice(DataSpecialList.get(2).CoverPrice));
        holder.product_sell_price_cell_1_sp.setText(SetColorSalesPrice(DataSpecialList.get(2).SalesPrice));
        if (DataSpecialList.get(2).CoverPrice != DataSpecialList.get(2).SalesPrice)
        {
            HelperModule.strikeThroughTextView(holder.product_primary_price_cell_1_sp);
        }

        if (DataSpecialList.get(2).LabelTitle == null)
        {
            holder.product_label_cell_1_sp.setVisibility(View.GONE);
        }
        else if (DataSpecialList.get(2).LabelTitle != null)
        {
            holder.product_label_cell_1_sp.setText(DataSpecialList.get(2).LabelTitle);
        }
        holder.cell1_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!ShoppingCartManger.IsProductExist(DataSpecialList.get(2).ProductID))
                {
                    ShoppingCartManger.AddProductToCart(DataSpecialList.get(2).ProductID);
                    ItemChanged(DataSpecialList.get(2).ProductID, true);
                    //----------------
                    ButtonBasket =v.getContext().getResources().getDrawable(R.drawable.remove_my_cart_btn);
                    holder.cell1_btn.setBackground(ButtonBasket);
                    holder.cell1_btn_text.setText("حذف از سبد");
                    //-----------------
                    //SpecialRecyclerAdapter.notifyDataSetChanged();
                    //HomeAdapter.notifyDataSetChanged();
                }
                else
                {
                    ShoppingCartManger.RemoveProductById(DataSpecialList.get(2).ProductID);
                    ItemChanged(DataSpecialList.get(2).ProductID, false);
                    //----------------
                    ButtonBasket =v.getContext().getResources().getDrawable(R.drawable.add_my_cart_btn);
                    holder.cell1_btn.setBackground(ButtonBasket);
                    holder.cell1_btn_text.setText("اضافه به سبد");
                    //-----------------
                    //SpecialRecyclerAdapter.notifyDataSetChanged();
                    //HomeAdapter.notifyDataSetChanged();
                }
                productCountChanged.OnCountChanged(ShoppingCartManger.GetShoppingCart().size());
                HomeAdapter.notifyDataSetChanged();
                // SpecialRecyclerAdapter.notifyDataSetChanged();
                //ItemChangedByIndex(DataSpecialList.get(2).ProductID);
            }
        });
    }

    //---------------------------------------------------------VIEW HOLDER--------------------------
    class RecyclerViewHolder extends RecyclerView.ViewHolder
    {

        //--------------------------------------------------SpecialItem(CollectionMode)
        //--------------------------------------------------Cell1
        ImageView img_product_cell_1_sp = null;
        TextView txt_product_name_cell_1_sp = null;
        TextView product_primary_price_cell_1_sp = null;
        TextView product_sell_price_cell_1_sp = null;
        TextView product_label_cell_1_sp = null;
        //ImageButton btn_plus_product_cell_1_sp = null;
        //TextView txt_product_count_cell_1_sp = null;
        //   ImageButton btn_mines_product_cell_1_sp = null;
        RelativeLayout sp_cell_1 = null;
        RelativeLayout cell1_content = null;
        RelativeLayout cell1_btn = null;
        TextView cell1_btn_text = null;
        //--------------------------------------------------------Cell2
        ImageView img_product_cell_2_sp = null;
        TextView txt_product_name_cell_2_sp = null;
        TextView product_primary_price_cell_2_sp = null;
        TextView product_sell_price_cell_2_sp = null;
        TextView product_label_cell_2_sp = null;
        // ImageButton btn_plus_product_cell_2_sp = null;
        //  TextView txt_product_count_cell_2_sp = null;
        //  ImageButton btn_mines_product_cell_2_sp = null;
        RelativeLayout sp_cell_2 = null;
        RelativeLayout cell2_content = null;
        RelativeLayout cell2_btn = null;
        TextView cell2_btn_text = null;
        //-------------------------------------------------Cell3
        ImageView img_product_cell_3_sp = null;
        TextView txt_product_name_cell_3_sp = null;
        TextView product_primary_price_cell_3_sp = null;
        TextView product_sell_price_cell_3_sp = null;
        TextView product_label_cell_3_sp = null;
        //ImageButton btn_plus_product_cell_3_sp = null;
        // TextView txt_product_count_cell_3_sp = null;
        // ImageButton btn_mines_product_cell_3_sp = null;
        RelativeLayout sp_cell_3 = null;
        RelativeLayout cell3_content = null;
        RelativeLayout cell3_btn = null;
        TextView cell3_btn_text = null;
        //--------------------------------------------------SpecialItem(CollectionMode)

        //---------SpecialItem(SingleMode)

        ImageView img_product_product_activity_single = null;
        TextView txt_product_name_product_activity_single = null;
        TextView product_primary_price_product_activity_single = null;
        TextView product_sell_price_product_activity_single = null;
        TextView product_label_product_activity_single = null;
        // ImageButton btn_plus_product_product_activity_single = null;
        // TextView txt_product_count_product_activity_single = null;
        //ImageButton btn_mines_product_product_activity_single = null;
        RelativeLayout product_add_product = null;
        RelativeLayout product_item_content = null;
        TextView product_add_product_text = null;

        //---------SpecialItem(CollectionMode)
        public RecyclerViewHolder(View itemView)
        {
            super(itemView);
            if (productType == ProductType.NewlyProduct || productType == ProductType.BestSellingProduct)
            {
                initViewsCell1(itemView);
                initViewsCell2(itemView);
                initViewsCell3(itemView);
            }
            else if (productType == ProductType.NewlyOrBestSellingSingleMode)
            {
                initViewSpecialItemSingleMode(itemView);
            }
        }

        private void initViewSpecialItemSingleMode(View itemView)
        {
            img_product_product_activity_single = itemView.findViewById(R.id.img_product_product_activity_single);
            txt_product_name_product_activity_single = itemView.findViewById(R.id.txt_product_name_product_activity_single);
            product_primary_price_product_activity_single = itemView.findViewById(R.id.product_primary_price_product_activity_single);
            product_sell_price_product_activity_single = itemView.findViewById(R.id.product_sell_price_product_activity_single);
            product_label_product_activity_single = itemView.findViewById(R.id.product_label_product_activity_single);
            //txt_product_count_product_activity_single = (TextView) itemView.findViewById(R.id.txt_product_count_product_activity_single);
            // btn_plus_product_product_activity_single = (ImageButton) itemView.findViewById(R.id.btn_plus_product_product_activity_single);
            //btn_mines_product_product_activity_single = (ImageButton) itemView.findViewById(R.id.btn_mines_product_product_activity_single);
            product_add_product = itemView.findViewById(R.id.product_add_product);
            product_item_content = itemView.findViewById(R.id.product_item_content);
            product_add_product_text = itemView.findViewById(R.id.product_add_product_text);
        }

        private void initViewsCell1(View itemView)
        {
            img_product_cell_1_sp = itemView.findViewById(R.id.img_product_cell_1_sp);
            txt_product_name_cell_1_sp = itemView.findViewById(R.id.txt_product_name_cell_1_sp);
            product_primary_price_cell_1_sp = itemView.findViewById(R.id.product_primary_price_cell_1_sp);
            product_sell_price_cell_1_sp = itemView.findViewById(R.id.product_sell_price_cell_1_sp);
            product_label_cell_1_sp = itemView.findViewById(R.id.product_label_cell_1_sp);
            // btn_plus_product_cell_1_sp = (ImageButton) itemView.findViewById(R.id.btn_plus_product_cell_1_sp);
            // txt_product_count_cell_1_sp = (TextView) itemView.findViewById(R.id.txt_product_count_cell_1_sp);
            //  btn_mines_product_cell_1_sp = (ImageButton) itemView.findViewById(R.id.btn_mines_product_cell_1_sp);
            sp_cell_1 = itemView.findViewById(R.id.sp_cell_1);
            cell1_content = itemView.findViewById(R.id.cell1_content);
            cell1_btn = itemView.findViewById(R.id.cell1_btn);
            cell1_btn_text = itemView.findViewById(R.id.cell1_btn_text);
        }

        private void initViewsCell2(View itemView)
        {
            img_product_cell_2_sp = itemView.findViewById(R.id.img_product_cell_2_sp);
            txt_product_name_cell_2_sp = itemView.findViewById(R.id.txt_product_name_cell_2_sp);
            product_primary_price_cell_2_sp = itemView.findViewById(R.id.product_primary_price_cell_2_sp);
            product_sell_price_cell_2_sp = itemView.findViewById(R.id.product_sell_price_cell_2_sp);
            product_label_cell_2_sp = itemView.findViewById(R.id.product_label_cell_2_sp);
            //   btn_plus_product_cell_2_sp = (ImageButton) itemView.findViewById(R.id.btn_plus_product_cell_2_sp);
            //   txt_product_count_cell_2_sp = (TextView) itemView.findViewById(R.id.txt_product_count_cell_2_sp);
            //   btn_mines_product_cell_2_sp = (ImageButton) itemView.findViewById(R.id.btn_mines_product_cell_2_sp);
            sp_cell_2 = itemView.findViewById(R.id.sp_cell_2);
            cell2_content = itemView.findViewById(R.id.cell2_content);
            cell2_btn = itemView.findViewById(R.id.cell2_btn);
            cell2_btn_text = itemView.findViewById(R.id.cell2_btn_text);
        }

        private void initViewsCell3(View itemView)
        {
            img_product_cell_3_sp = itemView.findViewById(R.id.img_product_cell_3_sp);
            txt_product_name_cell_3_sp = itemView.findViewById(R.id.txt_product_name_cell_3_sp);
            product_primary_price_cell_3_sp = itemView.findViewById(R.id.product_primary_price_cell_3_sp);
            product_sell_price_cell_3_sp = itemView.findViewById(R.id.product_sell_price_cell_3_sp);
            product_label_cell_3_sp = itemView.findViewById(R.id.product_label_cell_3_sp);
            //  btn_plus_product_cell_3_sp = (ImageButton) itemView.findViewById(R.id.btn_plus_product_cell_3_sp);
            // txt_product_count_cell_3_sp = (TextView) itemView.findViewById(R.id.txt_product_count_cell_3_sp);
            // btn_mines_product_cell_3_sp = (ImageButton) itemView.findViewById(R.id.btn_mines_product_cell_3_sp);
            sp_cell_3 = itemView.findViewById(R.id.sp_cell_3);
            cell3_content = itemView.findViewById(R.id.cell3_content);
            cell3_btn = itemView.findViewById(R.id.cell3_btn);
            cell3_btn_text = itemView.findViewById(R.id.cell3_btn_text);
        }
    }
}
