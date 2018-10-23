package company.aryasoft.app.malayermarket.Adapters;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
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
import java.util.Objects;

import company.aryasoft.app.malayermarket.Activities.DetailActivity;
import company.aryasoft.app.malayermarket.ApiModels.DataCollectionsApiModel;
import company.aryasoft.app.malayermarket.ApiModels.SearchModelApi;
import company.aryasoft.app.malayermarket.ApiModels.SimpleProductApiModel;
import company.aryasoft.app.malayermarket.R;
import company.aryasoft.app.malayermarket.UtilityAndHelper.HelperModule;
import company.aryasoft.app.malayermarket.UtilityAndHelper.MyBounceInterpolator;
import company.aryasoft.app.malayermarket.UtilityAndHelper.ProductCountChanged;
import company.aryasoft.app.malayermarket.UtilityAndHelper.ShoppingCartManger;

public class ProductRecyclerAdapter extends RecyclerView.Adapter<ProductRecyclerAdapter.RecyclerViewHolder>
{
    private Context ViewContext;
    private ArrayList<SimpleProductApiModel> ProductList ;
    private ProductCountChanged productCountChanged = null;
    private Drawable ButtonBasket = null;
    public ArrayList<SimpleProductApiModel> GetProductList()
    {
        return ProductList;
    }
    public ProductRecyclerAdapter(Context ViewContext)
    {
        this.ViewContext = ViewContext;
        this.ProductList = new ArrayList<>();
    }

    public void ClearItems()
    {
        this.ProductList.clear();
        this.notifyDataSetChanged();

    }

    private SpannableStringBuilder SetColorCoverPrice(int CoverPrice)
    {
        String myStrCoverPrice = "روی جلد " + CoverPrice + ViewContext.getResources().getString(R.string.price_unit);
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

    public void SetOnCountChange(ProductCountChanged productCountChanged)
    {
        this.productCountChanged = productCountChanged;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View RecyclerItemView = LayoutInflater.from(ViewContext).inflate(R.layout.product_rec_item_layout, parent, false);
        return new RecyclerViewHolder(RecyclerItemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder,  int position)
    {
        if (ShoppingCartManger.IsProductExist(ProductList.get(position).ProductID))
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
        if (!ProductList.get(position).ImageName.equals("nophoto.png"))
        {
            Glide.with(ViewContext).load(ViewContext.getResources().getString(R.string.ProductImageFolder) + ProductList.get(position).ImageName).into(holder.img_product_product_activity);
        }
        else
        {
            Glide.with(ViewContext).load(R.drawable.no_img).into(holder.img_product_product_activity);
        }
        holder.txt_product_name_product_activity.setText(ProductList.get(position).ProductTitle);

        holder.product_primary_price_product_activity.setText(SetColorCoverPrice(ProductList.get(position).CoverPrice));
        holder.product_sell_price_product_activity.setText(SetColorSalesPrice(ProductList.get(position).SalesPrice));

        if (ProductList.get(position).CoverPrice != ProductList.get(position).SalesPrice)
        {
            HelperModule.strikeThroughTextView(holder.product_primary_price_product_activity);
        }
        if (ProductList.get(position).LabelTitle == null)
        {
            holder.product_label_product_activity.setVisibility(View.GONE);
        }
        else if (ProductList.get(position).LabelTitle != null)
        {
            holder.product_label_product_activity.setText(ProductList.get(position).LabelTitle);
        }
        holder.product_add_product.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!ShoppingCartManger.IsProductExist(ProductList.get(holder.getAdapterPosition()).ProductID))
                {
                    ShoppingCartManger.AddProductToCart(ProductList.get(holder.getAdapterPosition()).ProductID);
                    if (productCountChanged != null)
                    {
                        productCountChanged.OnCountChanged(ShoppingCartManger.GetShoppingCart().size());
                    }
                    ButtonBasket = v.getResources().getDrawable(R.drawable.remove_my_cart_btn);
                    holder.product_add_product.setBackground(ButtonBasket);
                    holder.product_add_product_text.setText("حذف از سبد");
                }
                else
                {
                    ShoppingCartManger.RemoveProductById(ProductList.get(holder.getAdapterPosition()).ProductID);
                    ButtonBasket = v.getResources().getDrawable(R.drawable.add_my_cart_btn);
                    holder.product_add_product.setBackground(ButtonBasket);
                    holder.product_add_product_text.setText("اضافه به سبد");
                }
                notifyDataSetChanged();
            }
        });
        holder.product_item_content.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ViewContext.startActivity(new
                        Intent(ViewContext, DetailActivity.class)
                        .putExtra("ProductID", ProductList.get(holder.getAdapterPosition()).ProductID)
                );
            }
        });
    }

    public void AddProductItem(ArrayList<SimpleProductApiModel> ProductList)
    {
        this.ProductList.addAll(ProductList);
        this.notifyDataSetChanged();
    }


    @Override
    public int getItemCount()
    {
        return ProductList.size();
    }
    //---------------------------------------------------------VIEW HOLDER--------------------------
    class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView img_product_product_activity = null;
        private TextView txt_product_name_product_activity = null;
        private TextView product_primary_price_product_activity = null;
        private TextView product_sell_price_product_activity = null;
        private TextView product_label_product_activity = null;
        private RelativeLayout product_item_content = null;
        private RelativeLayout product_add_product = null;
        private TextView product_add_product_text = null;
        RecyclerViewHolder(View itemView)
        {
            super(itemView);
            //--------------
            img_product_product_activity = itemView.findViewById(R.id.img_product_product_activity_single);
            txt_product_name_product_activity = itemView.findViewById(R.id.txt_product_name_product_activity_single);
            product_primary_price_product_activity = itemView.findViewById(R.id.product_primary_price_product_activity_single);
            product_sell_price_product_activity = itemView.findViewById(R.id.product_sell_price_product_activity_single);
            product_label_product_activity = itemView.findViewById(R.id.product_label_product_activity_single);
            product_item_content = itemView.findViewById(R.id.product_item_content);
            product_add_product = itemView.findViewById(R.id.product_add_product);
            product_add_product_text = itemView.findViewById(R.id.product_add_product_text);
        }
    }
//---------------------------------------------------------VIEW HOLDER------------------------------
}
