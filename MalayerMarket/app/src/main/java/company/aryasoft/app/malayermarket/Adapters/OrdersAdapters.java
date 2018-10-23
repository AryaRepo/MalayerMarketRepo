package company.aryasoft.app.malayermarket.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import company.aryasoft.app.malayermarket.Activities.OrderDetailActivity;
import company.aryasoft.app.malayermarket.ApiModels.UserOrdersListApiModel;
import company.aryasoft.app.malayermarket.R;


public class OrdersAdapters extends RecyclerView.Adapter<OrdersAdapters.OrderRecyclerViewHolder>
{
    private ArrayList<UserOrdersListApiModel> OrderList = null;
    private Context context = null;

    public OrdersAdapters(Context context)
    {
        this.context = context;
        this.OrderList = new ArrayList<>();
    }

    @Override
    public OrdersAdapters.OrderRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View RecyclerItemView = LayoutInflater.from(context).inflate(R.layout.orders_list_item, parent, false);
        return new OrdersAdapters.OrderRecyclerViewHolder(RecyclerItemView);
    }

    @Override
    public void onBindViewHolder(final OrdersAdapters.OrderRecyclerViewHolder holder, int position)
    {
        if (OrderList.get(position).PaymentStatus)
        {
            holder.txt_invoice_num.setText(holder.txt_invoice_num.getText() + " - " + "پرداخت شده");
        }
        else
        {
            holder.txt_invoice_num.setText(holder.txt_invoice_num.getText() + " - " + "هنوز پرداخت نکردید");
        }
        //-----------------------------------------

        if (OrderList.get(position).PaymentTypeId == 1)
        {
            if (OrderList.get(position).PaymentStatus)
            {
                String myStr = "نحوه پرداخت : " +" آنلاین"+" - پرداخت شد";
                SpannableStringBuilder myspan = new SpannableStringBuilder(myStr);
                myspan.setSpan(new ForegroundColorSpan(Color.parseColor("#4CAF50")),23 ,myStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.txt_invoice_num.setText(myspan);
            }
            else if (!OrderList.get(position).PaymentStatus)
            {
                String myStr = "نحوه پرداخت : " +" آنلاین"+" - پرداخت نشده";
                SpannableStringBuilder myspan = new SpannableStringBuilder(myStr);
                myspan.setSpan(new ForegroundColorSpan(Color.parseColor("#FE1743")),23 ,myStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.txt_invoice_num.setText(myspan);
            }
        }
        else if (OrderList.get(position).PaymentTypeId == 2)
        {
            if (OrderList.get(position).PaymentStatus)
            {
                String myStr = "نحوه پرداخت : " +" درب منزل"+" - پرداخت شد";
                SpannableStringBuilder myspan = new SpannableStringBuilder(myStr);
                myspan.setSpan(new ForegroundColorSpan(Color.parseColor("#4CAF50")),23 ,myStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.txt_invoice_num.setText(myspan);
            }
            else if (!OrderList.get(position).PaymentStatus)
            {
                String myStr = "نحوه پرداخت : " +" درب منزل"+" - پرداخت نشده";
                SpannableStringBuilder myspan = new SpannableStringBuilder(myStr);
                myspan.setSpan(new ForegroundColorSpan(Color.parseColor("#FE1743")),23 ,myStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.txt_invoice_num.setText(myspan);
            }
        }
//        else
//        {
//            String myStr = "نحوه پرداخت : " + (OrderList.get(position).PaymentTypeId == 1) + " درب منزل";
//            SpannableStringBuilder myspan = new SpannableStringBuilder(myStr);
//            myspan.setSpan(new ForegroundColorSpan(Color.parseColor("#43A047")), String.valueOf("نحوه پرداخت : ").length(), myStr.length() - +" درب منزل".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            holder.txt_invoice_num.setText(myspan);
//        }

        //-----------------------------------------
        switch (OrderList.get(position).OrderStateID)
        {
            case 1:
                holder.txt_ic_status.setText("ثبت اولیه توسط مشتری");
                holder.img_ic_status.setColorFilter(Color.parseColor("#2196F3"), android.graphics.PorterDuff.Mode.MULTIPLY);
                break;
            case 2:
                holder.txt_ic_status.setText("درحال بررسی توسط مدیر");
                holder.img_ic_status.setColorFilter(Color.parseColor("#FFEE58"), android.graphics.PorterDuff.Mode.MULTIPLY);
                break;
            case 3:
                holder.txt_ic_status.setText("تایید سفارش توسط مدیر");
                holder.img_ic_status.setColorFilter(Color.parseColor("#FF9800"), android.graphics.PorterDuff.Mode.MULTIPLY);
                break;
            case 4:
                holder.txt_ic_status.setText("تحویل پیک");
                holder.img_ic_status.setColorFilter(Color.parseColor("#4CAF50"), android.graphics.PorterDuff.Mode.MULTIPLY);
                break;
            case 5:
                holder.txt_ic_status.setText("تحویل مشتری");
                holder.img_ic_status.setColorFilter(Color.parseColor("#4CAF50"), android.graphics.PorterDuff.Mode.MULTIPLY);
                break;
            case 6:
                holder.txt_ic_status.setText("تایید نهایی");
                holder.img_ic_status.setColorFilter(Color.parseColor("#4CAF50"), android.graphics.PorterDuff.Mode.MULTIPLY);
                break;
        }
        //-----------------------
        holder.txt_invoice_date.setText(OrderList.get(position).OrderDate);
        holder.txt_invoice_delivery_date.setText(OrderList.get(position).CustomerDeliveryTime);
        holder.btn_show_order_cart.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int OrderId = OrderList.get(holder.getAdapterPosition()).OrderID;
                context.startActivity(new Intent(context, OrderDetailActivity.class).putExtra("OrderId", OrderId));
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return OrderList.size();
    }

    public void AddOrderListData(ArrayList<UserOrdersListApiModel> OrderList)
    {
        this.OrderList.addAll(OrderList);
        this.notifyDataSetChanged();
    }

    class OrderRecyclerViewHolder extends RecyclerView.ViewHolder
    {
        TextView txt_invoice_date;
        TextView txt_ic_status;
        TextView txt_invoice_delivery_date;
        ImageView img_ic_status;
        Button btn_show_order_cart;
        TextView txt_invoice_num;

        public OrderRecyclerViewHolder(View itemView)
        {
            super(itemView);
            txt_invoice_num = (TextView) itemView.findViewById(R.id.txt_invoice_num);
            txt_invoice_date = (TextView) itemView.findViewById(R.id.txt_invoice_date);
            txt_ic_status = (TextView) itemView.findViewById(R.id.txt_ic_status);
            txt_invoice_delivery_date = (TextView) itemView.findViewById(R.id.txt_invoice_delivery_date);
            img_ic_status = (ImageView) itemView.findViewById(R.id.img_ic_status);
            btn_show_order_cart = (Button) itemView.findViewById(R.id.btn_show_order_cart);
        }
    }
}
