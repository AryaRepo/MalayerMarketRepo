package company.aryasoft.app.malayermarket.Adapters;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import company.aryasoft.app.malayermarket.Activities.SubCategoryActivity;
import company.aryasoft.app.malayermarket.ApiModels.ProductGroupsApiModel;
import company.aryasoft.app.malayermarket.R;
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>
{
    private Context context;
    private ArrayList<ProductGroupsApiModel> GroupList;
    public CategoryAdapter(Context context)
    {
        this.context=context;
        this.GroupList=new ArrayList<>();

    }
    public void AddCategoryData(ArrayList<ProductGroupsApiModel> GroupList)
    {
        this.GroupList.addAll(GroupList);
        ProductGroupsApiModel first_item=this.GroupList.get(0);
        ProductGroupsApiModel end_item=this.GroupList.get(this.GroupList.size()-1);
        this.GroupList.set(this.GroupList.size()-1,first_item);
        this.GroupList.set(0,end_item);
        this.notifyDataSetChanged();
    }
    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_item_grid, parent, false);
        return new CategoryAdapter.CategoryViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryViewHolder holder, int position)
    {
        //-----------
        holder.txt_category_title.setText(GroupList.get(position).ProductGroupTitle);
        Glide.with(context).load(context.getResources().getString(R.string.CategoryGroupImageFolder)+GroupList.get(position).ImageName).into(holder.img_category_grid);
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                context.startActivity(new Intent(context, SubCategoryActivity.class).putExtra("ProductGroupID", GroupList.get(holder.getAdapterPosition()).ProductGroupID));
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return GroupList.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder
    {
        TextView txt_category_title;
        ImageView img_category_grid;
        private CategoryViewHolder(View itemView)
        {
            super(itemView);
            txt_category_title = itemView.findViewById(R.id.txt_category_title_grid);
            img_category_grid = itemView.findViewById(R.id.img_category_grid);

        }
    }
}
