package company.aryasoft.app.malayermarket.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import company.aryasoft.app.malayermarket.Models.ShoppingCartModel;
import company.aryasoft.app.malayermarket.R;
import company.aryasoft.app.malayermarket.UtilityAndHelper.MyBounceInterpolator;
import company.aryasoft.app.malayermarket.UtilityAndHelper.ShoppingCartManger;

public class ShoppingCartAdapter extends BaseAdapter
{
    private class ShoppingCartViewHolder
    {
        TextView txt_product_name;
        TextView txt_product_count;
        TextView txt_product_price;
        ImageView img_product;
        ImageButton btn_increase_product;
        ImageButton btn_decrease_product;
        ImageButton btn_delete_product;
    }

    private LayoutInflater layoutInflater;
    private Context context;
    private ArrayList<ShoppingCartModel> ListShoppingCart = null;
    private ShoppingCartAdapter.ShoppingCartViewHolder holder = null;
    private OnDataChangeListener mOnDataChangeListener;
    private MyBounceInterpolator interpolator;
    private int ProductCount = 0;

    public interface OnDataChangeListener
    {
        void onDataChanged();
    }

    public void setOnDataChangeListener(OnDataChangeListener onDataChangeListener)
    {
        mOnDataChangeListener = onDataChangeListener;
    }

    public ShoppingCartAdapter(Context context)
    {
        this.context = context;
        this.ListShoppingCart = new ArrayList<>();
        layoutInflater = LayoutInflater.from(this.context);
        interpolator = new MyBounceInterpolator(0.2, 20);
    }

    public void AddToListShoppingCart(ArrayList<ShoppingCartModel> ListShoppingCart)
    {
        this.ListShoppingCart = ListShoppingCart;
        this.notifyDataSetChanged();
        if (mOnDataChangeListener != null)
        {
            mOnDataChangeListener.onDataChanged();
        }
    }

    @Override
    public int getCount()
    {
        return ListShoppingCart.size();
    }

    @Override
    public Object getItem(int position)
    {
        return ListShoppingCart.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent)
    {
        View MyView = convertView;
        if (convertView == null)
        {
            MyView = layoutInflater.inflate(R.layout.shopping_cart_item, null);
            holder = new ShoppingCartAdapter.ShoppingCartViewHolder();
        }
        holder.txt_product_name = MyView.findViewById(R.id.txt_product_name_shopping_cart);
        holder.txt_product_price = MyView.findViewById(R.id.txt_product_price_shopping_cart);
        holder.txt_product_count = MyView.findViewById(R.id.txt_product_count_shopping_cart);
        //--------------------
        holder.img_product = MyView.findViewById(R.id.img_product_shopping_cart);
        Glide.with(context).load(R.drawable.no_img).into(holder.img_product);
        ///-------------------
        holder.btn_increase_product = MyView.findViewById(R.id.btn_increase_product_shopping_cart);
        holder.btn_decrease_product = MyView.findViewById(R.id.btn_decrease_product_shopping_cart);
        holder.btn_delete_product = MyView.findViewById(R.id.btn_delete_product_shopping_cart);
        //-----------
        holder.btn_delete_product.setTag(position);
        holder.btn_increase_product.setTag(position);
        holder.btn_decrease_product.setTag(position);
        holder.txt_product_name.setText(ListShoppingCart.get(position).ProductTitle);
        holder.txt_product_price.setText(" قیمت " + ListShoppingCart.get(position).ProductPrice + " تومان ");
        ProductCount = ListShoppingCart.get(position).ProductCount;
        holder.txt_product_count.setText(ProductCount + " تا ");
        if (!ListShoppingCart.get(position).ProductImage.equals("nophoto.png"))
        {
            final RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .error(R.drawable.no_img)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH);
            Glide.with(context).load(context.getResources().getString(R.string.ProductImageFolder) + ListShoppingCart.get(position).ProductImage).apply(options).into(holder.img_product);
        }
        holder.btn_increase_product.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Animation myAnim = AnimationUtils.loadAnimation(context, R.anim.my_push_btn);
                myAnim.setInterpolator(interpolator);
                v.startAnimation(myAnim);
                //--------------------------
                ShoppingCartManger.CartManagerIncrease(ListShoppingCart.get(Integer.parseInt(v.getTag().toString())).ProductId);
                ++ProductCount;
                holder.txt_product_count.setText(ProductCount + " تا ");
                ListShoppingCart.get(Integer.parseInt(v.getTag().toString())).ProductCount++;
                notifyDataSetChanged();
                if (mOnDataChangeListener != null)
                {
                    mOnDataChangeListener.onDataChanged();
                }
                //--------------------------------------
            }
        });
        holder.btn_delete_product.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                Animation myAnim = AnimationUtils.loadAnimation(context, R.anim.my_push_btn);
                myAnim.setInterpolator(interpolator);
                v.startAnimation(myAnim);
                new SweetAlertDialog(v.getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("حذف کالا از سبد")
                        .setContentText("آیا با حدف این کالا از سبد خرید موافقید؟")
                        .setCancelText("خیر،حذف نشه")
                        .setConfirmText("بله، حذف شه")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener()
                        {
                            @Override
                            public void onClick(SweetAlertDialog sDialog)
                            {
                                sDialog.cancel();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                        {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog)
                            {
                                ShoppingCartManger.RemoveProductById(ListShoppingCart.get(Integer.parseInt(v.getTag().toString())).ProductId);
                                ListShoppingCart.remove(Integer.parseInt(v.getTag().toString()));
                                notifyDataSetChanged();

                                if (mOnDataChangeListener != null)
                                {
                                    mOnDataChangeListener.onDataChanged();
                                }
                                sweetAlertDialog.dismiss();
                            }
                        })
                        .show();
            }
        });
        holder.btn_decrease_product.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Animation myAnim = AnimationUtils.loadAnimation(context, R.anim.my_push_btn);
                myAnim.setInterpolator(interpolator);
                v.startAnimation(myAnim);
                int count = ShoppingCartManger.CartManagerDecrease(ListShoppingCart.get(Integer.parseInt(v.getTag().toString())).ProductId);
                if (count == -1)
                {
                    ListShoppingCart.remove(Integer.parseInt(v.getTag().toString()));
                }
                else
                {
                    holder.txt_product_count.setText(count + "");
                    ListShoppingCart.get(Integer.parseInt(v.getTag().toString())).ProductCount = count;
                }
                notifyDataSetChanged();
                if (mOnDataChangeListener != null)
                {
                    mOnDataChangeListener.onDataChanged();
                }
            }
        });
        return MyView;
    }
}
