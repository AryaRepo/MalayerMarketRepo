package company.aryasoft.app.malayermarket.Adapters;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import company.aryasoft.app.malayermarket.Activities.DetailActivity;
import company.aryasoft.app.malayermarket.Activities.ProductActivity;
import company.aryasoft.app.malayermarket.ApiModels.DataCollectionsApiModel;
import company.aryasoft.app.malayermarket.Models.SavedShoppingCartModel;
import company.aryasoft.app.malayermarket.R;
import company.aryasoft.app.malayermarket.UtilityAndHelper.HelperModule;
import company.aryasoft.app.malayermarket.UtilityAndHelper.MyBounceInterpolator;
import company.aryasoft.app.malayermarket.UtilityAndHelper.OnProductReserveChanged;
import company.aryasoft.app.malayermarket.UtilityAndHelper.ProductCountChanged;
import company.aryasoft.app.malayermarket.UtilityAndHelper.ProductType;
import company.aryasoft.app.malayermarket.UtilityAndHelper.ShoppingCartManger;
import company.aryasoft.app.malayermarket.UtilityAndHelper.VectorConfig;
import company.aryasoft.app.malayermarket.UtilityAndHelper.VectorsModel;

public class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeRecyclerAdapter.RecyclerViewHolder>
{
    private Context ViewContext = null;
    private ArrayList<DataCollectionsApiModel> DataCollectionList;
    private SpecialRecyclerAdapter BestSpecialRecyclerAdapter = null;
    private SpecialRecyclerAdapter NewlySpecialRecyclerAdapter = null;
    private ProductCountChanged onBestProductReserveChanged = null;
    private ProductCountChanged onNewlyProductReserveChanged = null;
    private Drawable ButtonBasket = null;

    public HomeRecyclerAdapter(Context ViewContext)
    {
        this.ViewContext = ViewContext;
        this.DataCollectionList = new ArrayList<>();
    }

    public void SetOnBestCountChange(SpecialRecyclerAdapter BestSpecialRecyclerAdapter, ProductCountChanged productCountChanged)
    {
        this.BestSpecialRecyclerAdapter = BestSpecialRecyclerAdapter;
        this.onBestProductReserveChanged = productCountChanged;
    }

    public void SetOnNewlyCountChange(SpecialRecyclerAdapter NewlySpecialRecyclerAdapter, ProductCountChanged productCountChanged)
    {
        this.NewlySpecialRecyclerAdapter = NewlySpecialRecyclerAdapter;
        this.onNewlyProductReserveChanged = productCountChanged;
    }

    public void ClearItems()
    {
        DataCollectionList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View RecyclerItemView = LayoutInflater.from(ViewContext).inflate(R.layout.main_rec_item_layout, parent, false);
        return new RecyclerViewHolder(RecyclerItemView);
    }

    public void AddCollectionData(ArrayList<DataCollectionsApiModel> DataCollectionList)
    {
        this.DataCollectionList.addAll(DataCollectionList);
        this.notifyDataSetChanged();
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

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position)
    {
        if (DataCollectionList.size() > 0)
        {
            if (DataCollectionList.get(position).ProductInfoList.size() > 0)
            {
                holder.txt_collection_names.setVisibility(View.VISIBLE);
                holder.txt_show_more.setVisibility(View.VISIBLE);
                holder.txt_collection_names.setText(DataCollectionList.get(position).CollectionTypeTitle);
                if (DataCollectionList.get(position).ProductInfoList.size() == 1)
                {
                    holder.rel_cell3.setVisibility(View.VISIBLE);
                    GetCell3Data(holder, position);
                }
                else if (DataCollectionList.get(position).ProductInfoList.size() == 2)
                {
                    holder.rel_cell2.setVisibility(View.VISIBLE);
                    holder.rel_cell3.setVisibility(View.VISIBLE);
                    GetCell3Data(holder, position);
                    GetCell2Data(holder, position);
                }
                else if (DataCollectionList.get(position).ProductInfoList.size() == 3)
                {
                    holder.rel_cell1.setVisibility(View.VISIBLE);
                    holder.rel_cell2.setVisibility(View.VISIBLE);
                    holder.rel_cell3.setVisibility(View.VISIBLE);
                    GetCell1Data(holder, position);
                    GetCell2Data(holder, position);
                    GetCell3Data(holder, position);

                }
                //------------------------------------Cell3
                holder.txt_show_more.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        ViewContext.startActivity(new
                                Intent(ViewContext, ProductActivity.class)
                                .putExtra("ToolbarTitle", DataCollectionList.get(holder.getAdapterPosition()).CollectionTypeTitle)
                                .putExtra("CollectionTypeID", DataCollectionList.get(holder.getAdapterPosition()).CollectionTypeID)
                                .putExtra("ActionType", 4)

                        );


                    }
                });
            }
        }
        initVector(holder);
    }

    private void GetCell1Data(final RecyclerViewHolder holder, final int position)
    {
        if (ShoppingCartManger.IsProductExist(DataCollectionList.get(position).ProductInfoList.get(2).ProductID))
        {
            ButtonBasket = this.ViewContext.getResources().getDrawable(R.drawable.remove_my_cart_btn);
            holder.product_cell1_btn.setBackground(ButtonBasket);
            holder.product_cell1_text.setText("حذف از سبد");
        }
        else
        {
            ButtonBasket = this.ViewContext.getResources().getDrawable(R.drawable.add_my_cart_btn);
            holder.product_cell1_btn.setBackground(ButtonBasket);
            holder.product_cell1_text.setText("اضافه به سبد");
        }
        holder.product_primary_price_cell_1.setText(SetColorCoverPrice(DataCollectionList.get(position).ProductInfoList.get(2).CoverPrice));
        holder.product_sell_price_cell_1.setText(SetColorSalesPrice(DataCollectionList.get(position).ProductInfoList.get(2).SalesPrice));
        if (DataCollectionList.get(position).ProductInfoList.get(2).CoverPrice != DataCollectionList.get(position).ProductInfoList.get(2).SalesPrice)
        {
            HelperModule.strikeThroughTextView(holder.product_primary_price_cell_1);
        }
        if (DataCollectionList.get(position).ProductInfoList.get(2).LabelTitle == null)
        {
            holder.product_label_cell_1.setVisibility(View.GONE);
        }
        else if (DataCollectionList.get(position).ProductInfoList.get(2).LabelTitle != null)
        {
            holder.product_label_cell_1.setText(DataCollectionList.get(position).ProductInfoList.get(2).LabelTitle);
        }
        holder.txt_product_name_cell_1.setText(DataCollectionList.get(position).ProductInfoList.get(2).ProductTitle);

        if (!DataCollectionList.get(position).ProductInfoList.get(2).ImageName.equals("nophoto.png"))
        {
            Glide.with(ViewContext).load(ViewContext.getResources().getString(R.string.ProductImageFolder) + DataCollectionList.get(position).ProductInfoList.get(2).ImageName).into(holder.img_product_cell_1);
        }
        else
        {
            Glide.with(ViewContext).load(R.drawable.no_img).into(holder.img_product_cell_1);
        }
        holder.product_cell1_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!ShoppingCartManger.IsProductExist(DataCollectionList.get(holder.getAdapterPosition()).ProductInfoList.get(2).ProductID))
                {

                    ShoppingCartManger.AddProductToCart(DataCollectionList.get(holder.getAdapterPosition()).ProductInfoList.get(2).ProductID);
                    ButtonBasket = v.getContext().getResources().getDrawable(R.drawable.remove_my_cart_btn);
                    holder.product_cell1_btn.setBackground(ButtonBasket);
                    holder.product_cell1_text.setText("حذف از سبد");
                    NewlySpecialRecyclerAdapter.ItemChangedByIndex(DataCollectionList.get(holder.getAdapterPosition()).ProductInfoList.get(2).ProductID, true);
                    BestSpecialRecyclerAdapter.ItemChangedByIndex(DataCollectionList.get(holder.getAdapterPosition()).ProductInfoList.get(2).ProductID, true);
                }
                else
                {
                    ShoppingCartManger.RemoveProductById(DataCollectionList.get(holder.getAdapterPosition()).ProductInfoList.get(2).ProductID);
                    ButtonBasket = v.getContext().getResources().getDrawable(R.drawable.add_my_cart_btn);
                    holder.product_cell1_btn.setBackground(ButtonBasket);
                    holder.product_cell1_text.setText("اضافه به سبد");
                    NewlySpecialRecyclerAdapter.ItemChangedByIndex(DataCollectionList.get(holder.getAdapterPosition()).ProductInfoList.get(2).ProductID, false);
                    BestSpecialRecyclerAdapter.ItemChangedByIndex(DataCollectionList.get(holder.getAdapterPosition()).ProductInfoList.get(2).ProductID, false);
                }

                onBestProductReserveChanged.OnCountChanged(ShoppingCartManger.GetShoppingCart().size());
                onNewlyProductReserveChanged.OnCountChanged(ShoppingCartManger.GetShoppingCart().size());
                notifyDataSetChanged();
            }
        });
        holder.product_cell1_content.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ViewContext.startActivity(new
                        Intent(ViewContext, DetailActivity.class)
                        .putExtra("ProductID", DataCollectionList.get(holder.getAdapterPosition()).ProductInfoList.get(2).ProductID));

            }
        });
    }

    private void GetCell2Data(final RecyclerViewHolder holder, final int position)
    {
        if (ShoppingCartManger.IsProductExist(DataCollectionList.get(position).ProductInfoList.get(1).ProductID))
        {
            ButtonBasket = this.ViewContext.getResources().getDrawable(R.drawable.remove_my_cart_btn);
            holder.product_cell2_btn.setBackground(ButtonBasket);
            holder.product_cell2_text.setText("حذف از سبد");
        }
        else
        {
            ButtonBasket = this.ViewContext.getResources().getDrawable(R.drawable.add_my_cart_btn);
            holder.product_cell2_btn.setBackground(ButtonBasket);
            holder.product_cell2_text.setText("اضافه به سبد");
        }
        //------------------------------------Cell2
        holder.product_primary_price_cell_2.setText(SetColorCoverPrice(DataCollectionList.get(position).ProductInfoList.get(1).CoverPrice));
        holder.product_sell_price_cell_2.setText(SetColorSalesPrice(DataCollectionList.get(position).ProductInfoList.get(1).SalesPrice));
        if (DataCollectionList.get(position).ProductInfoList.get(1).CoverPrice != DataCollectionList.get(position).ProductInfoList.get(1).SalesPrice)
        {
            HelperModule.strikeThroughTextView(holder.product_primary_price_cell_2);
        }
        if (DataCollectionList.get(position).ProductInfoList.get(1).LabelTitle == null)
        {
            holder.product_label_cell_2.setVisibility(View.GONE);
        }
        else if (DataCollectionList.get(position).ProductInfoList.get(1).LabelTitle != null)
        {
            holder.product_label_cell_2.setText(DataCollectionList.get(position).ProductInfoList.get(1).LabelTitle);
        }
        holder.txt_product_name_cell_2.setText(DataCollectionList.get(position).ProductInfoList.get(1).ProductTitle);
        if (!DataCollectionList.get(position).ProductInfoList.get(1).ImageName.equals("nophoto.png"))
        {
            Glide.with(ViewContext).load(ViewContext.getResources().getString(R.string.ProductImageFolder) + DataCollectionList.get(position).ProductInfoList.get(1).ImageName).into(holder.img_product_cell_2);
        }
        else
        {
            Glide.with(ViewContext).load(R.drawable.no_img).into(holder.img_product_cell_2);
        }
        holder.product_cell2_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!ShoppingCartManger.IsProductExist(DataCollectionList.get(holder.getAdapterPosition()).ProductInfoList.get(1).ProductID))
                {
                    ShoppingCartManger.AddProductToCart(DataCollectionList.get(holder.getAdapterPosition()).ProductInfoList.get(1).ProductID);
                    ButtonBasket = v.getContext().getResources().getDrawable(R.drawable.remove_my_cart_btn);
                    holder.product_cell2_btn.setBackground(ButtonBasket);
                    holder.product_cell2_text.setText("حذف از سبد");
                    NewlySpecialRecyclerAdapter.ItemChangedByIndex(DataCollectionList.get(holder.getAdapterPosition()).ProductInfoList.get(1).ProductID, true);
                    BestSpecialRecyclerAdapter.ItemChangedByIndex(DataCollectionList.get(holder.getAdapterPosition()).ProductInfoList.get(1).ProductID, true);
                    // ItemChanged(DataCollectionList.get(holder.getAdapterPosition()).ProductInfoList.get(1).ProductID, true,holder);
                }
                else
                {
                    ShoppingCartManger.RemoveProductById(DataCollectionList.get(holder.getAdapterPosition()).ProductInfoList.get(1).ProductID);
                    ButtonBasket = v.getContext().getResources().getDrawable(R.drawable.add_my_cart_btn);
                    holder.product_cell2_btn.setBackground(ButtonBasket);
                    holder.product_cell2_text.setText("اضافه به سبد");
                    NewlySpecialRecyclerAdapter.ItemChangedByIndex(DataCollectionList.get(holder.getAdapterPosition()).ProductInfoList.get(1).ProductID, false);
                    BestSpecialRecyclerAdapter.ItemChangedByIndex(DataCollectionList.get(holder.getAdapterPosition()).ProductInfoList.get(1).ProductID, false);
                }
                onBestProductReserveChanged.OnCountChanged(ShoppingCartManger.GetShoppingCart().size());
                onNewlyProductReserveChanged.OnCountChanged(ShoppingCartManger.GetShoppingCart().size());
                notifyDataSetChanged();
            }
        });
        holder.product_cell2_content.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ViewContext.startActivity(new
                        Intent(ViewContext, DetailActivity.class)
                        .putExtra("ProductID", DataCollectionList.get(holder.getAdapterPosition()).ProductInfoList.get(1).ProductID));

            }
        });
    }


    private void GetCell3Data(final RecyclerViewHolder holder, final int position)
    {
        if (ShoppingCartManger.IsProductExist(DataCollectionList.get(position).ProductInfoList.get(0).ProductID))
        {
            ButtonBasket = ViewContext.getResources().getDrawable(R.drawable.remove_my_cart_btn);
            holder.product_cell3_btn.setBackground(ButtonBasket);
            holder.product_cell3_text.setText("حذف از سبد");
        }
        else
        {
            ButtonBasket = ViewContext.getResources().getDrawable(R.drawable.add_my_cart_btn);
            holder.product_cell3_btn.setBackground(ButtonBasket);
            holder.product_cell3_text.setText("اضافه به سبد");
        }
        //------------------------------------Cell3
        holder.product_primary_price_cell_3.setText(SetColorCoverPrice(DataCollectionList.get(position).ProductInfoList.get(0).CoverPrice));
        holder.product_sell_price_cell_3.setText(SetColorSalesPrice(DataCollectionList.get(position).ProductInfoList.get(0).SalesPrice));
        if (DataCollectionList.get(position).ProductInfoList.get(0).CoverPrice != DataCollectionList.get(position).ProductInfoList.get(0).SalesPrice)
        {
            HelperModule.strikeThroughTextView(holder.product_primary_price_cell_3);
        }
        if (DataCollectionList.get(position).ProductInfoList.get(0).LabelTitle == null)
        {
            holder.product_label_cell_3.setVisibility(View.GONE);
        }
        else if (DataCollectionList.get(position).ProductInfoList.get(0).LabelTitle != null)
        {
            holder.product_label_cell_3.setText(DataCollectionList.get(position).ProductInfoList.get(0).LabelTitle);
        }
        holder.txt_product_name_cell_3.setText(DataCollectionList.get(position).ProductInfoList.get(0).ProductTitle);
        if (!DataCollectionList.get(position).ProductInfoList.get(0).ImageName.equals("nophoto.png"))
        {
            Glide.with(ViewContext).load(ViewContext.getResources().getString(R.string.ProductImageFolder) + DataCollectionList.get(position).ProductInfoList.get(0).ImageName).into(holder.img_product_cell_3);
        }
        else
        {
            Glide.with(ViewContext).load(R.drawable.no_img).into(holder.img_product_cell_3);
        }
        holder.product_cell3_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!ShoppingCartManger.IsProductExist(DataCollectionList.get(holder.getAdapterPosition()).ProductInfoList.get(0).ProductID))
                {
                    ShoppingCartManger.AddProductToCart(DataCollectionList.get(holder.getAdapterPosition()).ProductInfoList.get(0).ProductID);
                    ButtonBasket = ViewContext.getResources().getDrawable(R.drawable.remove_my_cart_btn);
                    holder.product_cell3_btn.setBackground(ButtonBasket);
                    holder.product_cell3_text.setText("حذف از سبد");
                    NewlySpecialRecyclerAdapter.ItemChangedByIndex(DataCollectionList.get(holder.getAdapterPosition()).ProductInfoList.get(0).ProductID, true);
                    BestSpecialRecyclerAdapter.ItemChangedByIndex(DataCollectionList.get(holder.getAdapterPosition()).ProductInfoList.get(0).ProductID, true);
                    //  ItemChanged(DataCollectionList.get(holder.getAdapterPosition()).ProductInfoList.get(0).ProductID, true,holder);
                }
                else
                {
                    ShoppingCartManger.RemoveProductById(DataCollectionList.get(holder.getAdapterPosition()).ProductInfoList.get(0).ProductID);
                    ButtonBasket = ViewContext.getResources().getDrawable(R.drawable.add_my_cart_btn);
                    holder.product_cell3_btn.setBackground(ButtonBasket);
                    holder.product_cell3_text.setText("اضافه به سبد");
                    NewlySpecialRecyclerAdapter.ItemChangedByIndex(DataCollectionList.get(holder.getAdapterPosition()).ProductInfoList.get(0).ProductID, false);
                    BestSpecialRecyclerAdapter.ItemChangedByIndex(DataCollectionList.get(holder.getAdapterPosition()).ProductInfoList.get(0).ProductID, false);
                    //ItemChanged(DataCollectionList.get(holder.getAdapterPosition()).ProductInfoList.get(0).ProductID, false,holder);
                }

                onBestProductReserveChanged.OnCountChanged(ShoppingCartManger.GetShoppingCart().size());
                onNewlyProductReserveChanged.OnCountChanged(ShoppingCartManger.GetShoppingCart().size());
                notifyDataSetChanged();
            }
        });
        holder.product_cell3_content.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ViewContext.startActivity(new
                        Intent(ViewContext, DetailActivity.class)
                        .putExtra("ProductID", DataCollectionList.get(holder.getAdapterPosition()).ProductInfoList.get(0).ProductID));

            }
        });
    }


    private void initVector(RecyclerViewHolder holder)
    {
        ArrayList<VectorsModel> vectorsList = new ArrayList<>();
        vectorsList.add(new VectorsModel(R.drawable.chevron_left, holder.txt_show_more, VectorConfig.MyDirType.start));
        VectorConfig.SetVectors(ViewContext.getResources(), vectorsList);
    }

    @Override
    public int getItemCount()
    {
        ArrayList<DataCollectionsApiModel> TempData = new ArrayList<>();
        for (int i = 0; i < DataCollectionList.size(); ++i)
        {
            if (DataCollectionList.get(i).ProductInfoList.size() != 0)
            {
                //count++;
                TempData.add(DataCollectionList.get(i));
            }
        }
        DataCollectionList.clear();
        DataCollectionList.addAll(TempData);
        return DataCollectionList.size();
    }

    //---------------------------------------------------------VIEW HOLDER--------------------------
    class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        RelativeLayout rel_cell1, rel_cell2, rel_cell3;
        TextView txt_collection_names = null;
        TextView txt_show_more = null;
        //-----------------------------------------------------------Cell1
        ImageView img_product_cell_1 = null;
        TextView txt_product_name_cell_1 = null;
        TextView product_primary_price_cell_1 = null;
        TextView product_sell_price_cell_1 = null;
        TextView product_label_cell_1 = null;
        RelativeLayout product_cell1_btn = null;
        RelativeLayout product_cell1_content = null;
        TextView product_cell1_text = null;
        //-----------------------------------------------------------Cell2
        ImageView img_product_cell_2 = null;
        TextView txt_product_name_cell_2 = null;
        TextView product_primary_price_cell_2 = null;
        TextView product_sell_price_cell_2 = null;
        TextView product_label_cell_2 = null;
        RelativeLayout product_cell2_btn = null;
        RelativeLayout product_cell2_content = null;
        TextView product_cell2_text = null;
        //-----------------------------------------------------------Cell3
        ImageView img_product_cell_3 = null;
        TextView txt_product_name_cell_3 = null;
        TextView product_primary_price_cell_3 = null;
        TextView product_sell_price_cell_3 = null;
        TextView product_label_cell_3 = null;
        RelativeLayout product_cell3_btn = null;
        RelativeLayout product_cell3_content = null;
        TextView product_cell3_text = null;

        //==============
        public RecyclerViewHolder(View itemView)
        {
            super(itemView);
            //---------
            txt_collection_names = itemView.findViewById(R.id.txt_collection_names);
            rel_cell1 = itemView.findViewById(R.id.rel_cell1);
            rel_cell2 = itemView.findViewById(R.id.rel_cell2);
            rel_cell3 = itemView.findViewById(R.id.rel_cell3);
            txt_show_more = itemView.findViewById(R.id.txt_show_more);
            initViewsCell1(itemView);
            initViewsCell2(itemView);
            initViewsCell3(itemView);
        }

        private void initViewsCell1(View itemView)
        {
            img_product_cell_1 = itemView.findViewById(R.id.img_product_cell_1);
            txt_product_name_cell_1 = itemView.findViewById(R.id.txt_product_name_cell_1);
            product_primary_price_cell_1 = itemView.findViewById(R.id.product_primary_price_cell_1);
            product_sell_price_cell_1 = itemView.findViewById(R.id.product_sell_price_cell_1);
            product_label_cell_1 = itemView.findViewById(R.id.product_label_cell_1);
            product_cell1_btn = itemView.findViewById(R.id.product_cell1_btn);
            product_cell1_content = itemView.findViewById(R.id.product_cell1_content);
            product_cell1_text = itemView.findViewById(R.id.product_cell1_text);
        }

        private void initViewsCell2(View itemView)
        {
            img_product_cell_2 = itemView.findViewById(R.id.img_product_cell_2);
            txt_product_name_cell_2 = itemView.findViewById(R.id.txt_product_name_cell_2);
            product_primary_price_cell_2 = itemView.findViewById(R.id.product_primary_price_cell_2);
            product_sell_price_cell_2 = itemView.findViewById(R.id.product_sell_price_cell_2);
            product_label_cell_2 = itemView.findViewById(R.id.product_label_cell_2);
            product_cell2_btn = itemView.findViewById(R.id.product_cell2_btn);
            product_cell2_content = itemView.findViewById(R.id.product_cell2_content);
            product_cell2_text = itemView.findViewById(R.id.product_cell2_text);
        }

        private void initViewsCell3(View itemView)
        {
            img_product_cell_3 = itemView.findViewById(R.id.img_product_cell_3);
            txt_product_name_cell_3 = itemView.findViewById(R.id.txt_product_name_cell_3);
            product_primary_price_cell_3 = itemView.findViewById(R.id.product_primary_price_cell_3);
            product_sell_price_cell_3 = itemView.findViewById(R.id.product_sell_price_cell_3);
            product_label_cell_3 = itemView.findViewById(R.id.product_label_cell_3);
            product_cell3_btn = itemView.findViewById(R.id.product_cell3_btn);
            product_cell3_content = itemView.findViewById(R.id.product_cell3_content);
            product_cell3_text = itemView.findViewById(R.id.product_cell3_text);
        }
    }
//---------------------------------------------------------VIEW HOLDER------------------------------
}
