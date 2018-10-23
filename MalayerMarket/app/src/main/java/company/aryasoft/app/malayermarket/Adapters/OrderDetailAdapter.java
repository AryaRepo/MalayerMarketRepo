package company.aryasoft.app.malayermarket.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import company.aryasoft.app.malayermarket.Activities.OrderDetailActivity;
import company.aryasoft.app.malayermarket.ApiModels.UserOrderDetailApiModel;
import company.aryasoft.app.malayermarket.ApiModels.UserOrdersListApiModel;
import company.aryasoft.app.malayermarket.R;


public class OrderDetailAdapter extends BaseAdapter
{
   private class OrderDetailViewHolder
    {
        TextView txt_order_product_name_item;
        TextView txt_order_product_count_item;
        TextView txt_order_product_price_item;
    }

    private LayoutInflater layoutInflater = null;
    private Context context = null;
    private ArrayList<UserOrderDetailApiModel> OrderList = null;
    private OrderDetailViewHolder holder = null;

    public OrderDetailAdapter(Context context)
    {
        this.context = context;
        this.OrderList = new ArrayList<>();
        layoutInflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount()
    {
        return OrderList.size();
    }

    public void AddOrderData(ArrayList<UserOrderDetailApiModel> OrderList)
    {
        this.OrderList.addAll(OrderList);
        this.notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position)
    {
        return OrderList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View MyView = convertView;
        if (convertView == null)
        {
            MyView = layoutInflater.inflate(R.layout.order_detail_list_item, null);
            holder = new OrderDetailViewHolder();
        }
        holder.txt_order_product_name_item = MyView.findViewById(R.id.txt_order_product_name_item_lay);
        holder.txt_order_product_count_item = MyView.findViewById(R.id.txt_order_product_count_item_lay);
        holder.txt_order_product_price_item = MyView.findViewById(R.id.txt_order_product_price_item_lay);
        //--------------------
        holder.txt_order_product_name_item.setText(OrderList.get(position).ProductTitle);
        holder.txt_order_product_count_item.setText(OrderList.get(position).Number+"");
        holder.txt_order_product_price_item.setText(OrderList.get(position).Price+"");
        //----------------
        return MyView;
    }
}

