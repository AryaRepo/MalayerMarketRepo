package company.aryasoft.app.malayermarket.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import company.aryasoft.app.malayermarket.ApiModels.SimilarProductApiModel;
import company.aryasoft.app.malayermarket.R;

public class SimilarProductsAdapter extends BaseAdapter
{
    private class MyViewHolder
    {
        CircleImageView imgSimilarProductPhoto;
        TextView txtSimilarProductName;
    }

    private Context _Context;
    private ArrayList<SimilarProductApiModel> _List;
    private MyViewHolder holder;

    public SimilarProductsAdapter(Context _Context, ArrayList<SimilarProductApiModel> list)
    {
        this._Context = _Context;
        this._List = list;
    }

    @Override
    public int getCount()
    {
        return _List.size();
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View row = convertView;
        if (row == null)
        {
            row = LayoutInflater.from(_Context).inflate(R.layout.similar_product_item_layout, parent, false);
            holder = new MyViewHolder();
        }
        initViews(row);
        Glide.with(_Context).load(_Context.getResources().getString(R.string.ProductImageFolder)  +_List.get(position).ImageName)
                .apply(new RequestOptions()
                        .centerCrop()
                        .error(R.drawable.no_img)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .priority(Priority.HIGH))
                .into(holder.imgSimilarProductPhoto);
        holder.txtSimilarProductName.setText(_List.get(position).ProductTitle);
        return row;
    }

    private void initViews(View view)
    {
        holder.imgSimilarProductPhoto = (CircleImageView) view.findViewById(R.id.img_similar_product_photo);
        holder.txtSimilarProductName = (TextView) view.findViewById(R.id.txt_similar_product_name);
    }
}
