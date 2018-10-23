package company.aryasoft.app.malayermarket.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import company.aryasoft.app.malayermarket.DBLayer.FavouriteCart;
import company.aryasoft.app.malayermarket.DBLayer.ProductFavouriteCart;
import company.aryasoft.app.malayermarket.R;

public class FavouriteCartDetailAdapter extends BaseAdapter
{
    private class DetailFavouriteCartViewHolder
    {
        TextView txt_order_product_row;
        TextView txt_fav_product_name;
        TextView txt_fav_product_count;
        ImageButton fav_plus;
        ImageButton fav_mines;
    }

    private LayoutInflater layoutInflater = null;
    private Context context = null;
    private List<ProductFavouriteCart> ListFavouriteCart = null;
    private DetailFavouriteCartViewHolder holder = null;
    private int row_number = 0;
    private boolean FirstLoad = true;

    public FavouriteCartDetailAdapter(Context context)
    {
        this.context = context;
        this.ListFavouriteCart = new ArrayList<>();
        layoutInflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount()
    {
        return ListFavouriteCart.size();
    }

    @Override
    public Object getItem(int position)
    {
        return ListFavouriteCart.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    public void AddNewFavItems(List<ProductFavouriteCart> ListFavouriteCart)
    {
        this.ListFavouriteCart.clear();
        this.ListFavouriteCart.addAll(ListFavouriteCart);
        this.notifyDataSetChanged();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        View MyView = convertView;
        if (convertView == null)
        {
            MyView = layoutInflater.inflate(R.layout.fav_cart_detail_item_layout, null);
            holder = new DetailFavouriteCartViewHolder();
        }
        holder.txt_order_product_row = (TextView) MyView.findViewById(R.id.txt_order_product_row);
        holder.txt_fav_product_name = (TextView) MyView.findViewById(R.id.txt_fav_product_name);
        holder.txt_fav_product_count = (TextView) MyView.findViewById(R.id.txt_fav_product_count);
        holder.fav_plus = (ImageButton) MyView.findViewById(R.id.fav_plus);
        holder.fav_mines = (ImageButton) MyView.findViewById(R.id.fav_mines);
        holder.fav_plus.setTag(position);
        holder.fav_mines.setTag(position);
        //--------------------------
        if (FirstLoad)
        {
            holder.txt_order_product_row.setText(++row_number + "");
        }
        holder.txt_fav_product_name.setText(ListFavouriteCart.get(position).ProductTitle);
        holder.txt_fav_product_count.setText(ListFavouriteCart.get(position).ProductCount + "");
        //--------------------------
        holder.fav_plus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ProductFavouriteCart data = ProductFavouriteCart.findById(ProductFavouriteCart.class, ListFavouriteCart.get(Integer.parseInt(v.getTag().toString())).getId());
                int P_count = data.ProductCount;
                data.ProductCount++;
                data.save();
                ++P_count;
                FirstLoad = false;
                ListFavouriteCart.get(Integer.parseInt(v.getTag().toString())).ProductCount++;
                notifyDataSetChanged();
            }
        });
        holder.fav_mines.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ProductFavouriteCart data = ProductFavouriteCart.findById(ProductFavouriteCart.class, ListFavouriteCart.get(Integer.parseInt(v.getTag().toString())).getId());
                int P_count = data.ProductCount;
                if (P_count - 1 != 0)
                {
                    data.ProductCount--;
                    data.save();
                    holder.txt_fav_product_count.setText(--P_count + "");
                    ListFavouriteCart.get(Integer.parseInt(v.getTag().toString())).ProductCount--;
                    FirstLoad = false;
                    notifyDataSetChanged();
                }
                else
                {
                    data.delete();
                    ListFavouriteCart.remove(Integer.parseInt(v.getTag().toString()));
                    FirstLoad = false;
                    notifyDataSetChanged();
                    ///holder.txt_fav_product_count.setText("0);
                }
            }
        });
        //--------------------------
        if (FirstLoad)
        {
            Animation anim = AnimationUtils.loadAnimation(context, R.anim.fly_in_from_top_corner);
            MyView.setAnimation(anim);
            anim.start();
        }

        //--------------------
        return MyView;
    }
}

