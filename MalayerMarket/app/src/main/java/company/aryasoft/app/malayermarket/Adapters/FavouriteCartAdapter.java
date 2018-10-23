package company.aryasoft.app.malayermarket.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
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
import company.aryasoft.app.malayermarket.Activities.FavouriteCartDetailActivity;
import company.aryasoft.app.malayermarket.Activities.ShoppingCartActivity;
import company.aryasoft.app.malayermarket.DBLayer.FavouriteCart;
import company.aryasoft.app.malayermarket.DBLayer.ProductFavouriteCart;
import company.aryasoft.app.malayermarket.Models.SavedShoppingCartModel;
import company.aryasoft.app.malayermarket.R;
import company.aryasoft.app.malayermarket.UtilityAndHelper.ShoppingCartManger;

public class FavouriteCartAdapter extends BaseAdapter
{
    private class FavouriteCartViewHolder
    {
        TextView txt_fav_title;
        TextView txt_fav_quick_info;
        Button btn_order_fav_cart;
        Button btn_view_cart;
        ImageButton img_btn_del_cart;
    }

    private int BasketItemCount = 0;
    private LayoutInflater layoutInflater ;
    private Context context ;
    private List<FavouriteCart> ListFavouriteCart = null;
    private FavouriteCartViewHolder holder = null;

    public FavouriteCartAdapter(Context context)
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

    public void AddNewFavItems(List<FavouriteCart> ListFavouriteCart)
    {
        this.ListFavouriteCart.clear();
        this.ListFavouriteCart.addAll(ListFavouriteCart);
        this.notifyDataSetChanged();

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View MyView = convertView;
        if (convertView == null)
        {
            MyView = layoutInflater.inflate(R.layout.favourite_cart_item, null);
            holder = new FavouriteCartViewHolder();
        }
        holder.txt_fav_title = MyView.findViewById(R.id.txt_fav_title);
        holder.txt_fav_quick_info = MyView.findViewById(R.id.txt_fav_quick_info);
        holder.btn_order_fav_cart = MyView.findViewById(R.id.btn_order_fav_cart);
        holder.btn_view_cart = MyView.findViewById(R.id.btn_view_cart);
        holder.img_btn_del_cart = MyView.findViewById(R.id.img_btn_del_cart);
        //--------------------------
        holder.btn_view_cart.setTag(position);
        holder.img_btn_del_cart.setTag(position);
        holder.btn_order_fav_cart.setTag(position);
        //-----------
        holder.txt_fav_title.setText(ListFavouriteCart.get(position).getCartName());
        BasketItemCount = ProductFavouriteCart.find(ProductFavouriteCart.class, "basket_id=" + ListFavouriteCart.get(position).getId()).size();
        holder.txt_fav_quick_info.setText("تعداد محصولاتون توی این سبدتون " + BasketItemCount + " تاست .");
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.fly_in_from_top_corner);
        MyView.setAnimation(anim);
        anim.start();
        //--------------------
        holder.img_btn_del_cart.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("حذف سبد")
                        .setContentText("کاربرگرامی آیا مایل به حذف این سبد هستید؟")
                        .setConfirmText("بله ")
                        .setCancelText("خیر حذف نکن")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                        {
                            @Override
                            public void onClick(SweetAlertDialog sDialog)
                            {
                                int pos = (int) v.getTag();
                                FavouriteCart.deleteAll(FavouriteCart.class, "id=" + ListFavouriteCart.get(pos).getId());
                                ListFavouriteCart.remove(pos);
                                notifyDataSetChanged();
                                sDialog.dismiss();
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener()
                        {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog)
                            {

                                sweetAlertDialog.dismiss();
                            }
                        })
                        .show();
            }
        });
        holder.btn_view_cart.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int Pos = (int) v.getTag();
                context.startActivity(new Intent(context, FavouriteCartDetailActivity.class).putExtra("BasketId", ListFavouriteCart.get(Pos).getId()));
            }
        });
        holder.btn_order_fav_cart.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final int Pos = (int) v.getTag();
                if (ShoppingCartManger.GetShoppingCart().size() <= 0)
                {
                    //show loading
                    //add to cart
                    //goto shopping cart
                    AddFavCartToShoppingCart(Pos, null);
                }
                else
                {
                    if (BasketItemCount > 0)
                    {
                        android.support.v7.app.AlertDialog.Builder NotifyDialogAlert = new android.support.v7.app.AlertDialog.Builder(context);
                        NotifyDialogAlert.setCancelable(false);
                        View AlertView = View.inflate(context, R.layout.dialog_notify, null);
                        TextView NotifyDialogTitle = AlertView.findViewById(R.id.txt_dialog_title);
                        TextView NotifyDialogMessage = AlertView.findViewById(R.id.txt_dialog_message);
                        NotifyDialogTitle.setText("پیغام سیستم");
                        NotifyDialogMessage.setText("کاربرگرامی با وجود اینکه سبد خریدتان محصولات دارد\n مایلید که محصولات این سبد به سبد فعلی اضافه شوند?");
                        NotifyDialogAlert.setView(AlertView);
                        NotifyDialogAlert.setNegativeButton("خیر", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        });
                        NotifyDialogAlert.setPositiveButton("بله", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                AddFavCartToShoppingCart(Pos, dialog);
                            }
                        });
                        //--------------------
                        NotifyDialogAlert.show();
                    }
                    else
                    {
                        Toast.makeText(context, "کاربر عزیز سبد شما  محصولی ندارد.", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });
        return MyView;
    }

    private void AddFavCartToShoppingCart(int Position, DialogInterface dialog)
    {
        ArrayList<SavedShoppingCartModel> MyCart = new ArrayList<>();
        List<ProductFavouriteCart> temp = ProductFavouriteCart.find(ProductFavouriteCart.class, "basket_id=" + ListFavouriteCart.get(Position).getId());
        if (temp.size() == 0)
        {
            Toast.makeText(context, "هیچ کالایی در این سبد موجود نیست که به سبد خرید اضافه کنید.", Toast.LENGTH_SHORT).show();
            return;
        }

        for (int i = 0; i < temp.size(); ++i)
        {
            SavedShoppingCartModel ObjSv = new SavedShoppingCartModel();
            ObjSv.ProductId = temp.get(i).ProductId;
            ObjSv.ProductCount = temp.get(i).ProductCount;
            MyCart.add(ObjSv);
        }
        ShoppingCartManger.AddToShoppingCartCollectionItem(MyCart);

        if (dialog != null)
        {
            dialog.dismiss();
            context.startActivity(new Intent(context, ShoppingCartActivity.class));
            ((AppCompatActivity) context).finish();
        }
        else
        {
            context.startActivity(new Intent(context, ShoppingCartActivity.class));
            ((AppCompatActivity) context).finish();
        }
        Toast.makeText(context, "محصولات سبد علاقمندی ها به سبد خریدتان اضافه گردید.", Toast.LENGTH_SHORT).show();


    }
}

