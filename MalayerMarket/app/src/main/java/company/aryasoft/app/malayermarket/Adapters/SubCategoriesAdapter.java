package company.aryasoft.app.malayermarket.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import company.aryasoft.app.malayermarket.ApiModels.ProductGroupsApiModel;
import company.aryasoft.app.malayermarket.R;


public class SubCategoriesAdapter extends RecyclerView.Adapter<SubCategoriesAdapter.SubCategoryRecyclerViewHolder>
{
    private Context context = null;
    private ArrayList<ProductGroupsApiModel> DataList = null;
    private SubCategoryItemClick itemClick = null;

    public void setItemClick(SubCategoryItemClick itemClick)
    {
        this.itemClick = itemClick;
    }

    public SubCategoriesAdapter(Context context)
    {
        this.context = context;
        this.DataList = new ArrayList<>();
    }

    @Override
    public SubCategoryRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View RecyclerItemView = LayoutInflater.from(context).inflate(R.layout.sub_categories_item, parent, false);
        return new SubCategoriesAdapter.SubCategoryRecyclerViewHolder(RecyclerItemView);
    }

    public void RefreshData(ArrayList<ProductGroupsApiModel> DataList)
    {
        if (this.DataList.size() > 0)
        {
            this.DataList.clear();
        }
        this.DataList.addAll(DataList);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final SubCategoryRecyclerViewHolder holder, int position)
    {
        holder.txt_sub_category.setText(DataList.get(position).ProductGroupTitle);
        if (DataList.get(position).ImageName.equals("nophoto.png"))
        {
            Glide.with(context).load(R.drawable.no_img).into(holder.img_sub_category);
        }
        else
        {
            Glide.with(context).load(context.getResources().getString(R.string.CategoryGroupImageFolder) + DataList.get(position).ImageName).into(holder.img_sub_category);
        }
//        Animation anim = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
//        anim.start();
//        holder.itemView.setAnimation(anim);
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (itemClick != null)
                {
                    itemClick.OnClick(holder.getAdapterPosition());
                }
            }
        });
    }


    @Override
    public int getItemCount()
    {
        return this.DataList.size();
    }

    public interface SubCategoryItemClick
    {
        void OnClick(int Position);
    }

    class SubCategoryRecyclerViewHolder extends RecyclerView.ViewHolder
    {
        TextView txt_sub_category;
        ImageView img_sub_category;

        public SubCategoryRecyclerViewHolder(View itemView)
        {
            super(itemView);
            txt_sub_category = (TextView) itemView.findViewById(R.id.txt_sub_category);
            img_sub_category = (ImageView) itemView.findViewById(R.id.img_sub_category);

        }
    }
}
