package company.aryasoft.app.malayermarket.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import company.aryasoft.app.malayermarket.ApiModels.ProductPropertyApiModel;
import company.aryasoft.app.malayermarket.R;


public class PropertiesAdapters extends BaseAdapter
{
    class PropertiesViewHolder
    {
        public TextView txt_propertyName;
        public TextView txt_propertyValue;
    }
    private LayoutInflater layoutInflater = null;
    private Context context = null;
    private ArrayList<ProductPropertyApiModel> ListProperties = null;
    private PropertiesViewHolder holder = null;
    public PropertiesAdapters(Context context, ArrayList<ProductPropertyApiModel> ListProperties)
    {
        this.context = context;
        this.ListProperties = ListProperties;
        layoutInflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount()
    {
        return ListProperties.size();
    }

    @Override
    public Object getItem(int position)
    {
        return ListProperties.get(position);
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
            MyView = layoutInflater.inflate(R.layout.proerties_item_layout, null);
            holder = new PropertiesViewHolder();
        }

        holder.txt_propertyName =(TextView) MyView.findViewById(R.id.txt_propertyName);
        holder.txt_propertyValue =(TextView) MyView.findViewById(R.id.txt_propertyValue);
        //--------------------------------
        if(ListProperties.size()>0)
        {
            holder.txt_propertyName.setText(ListProperties.get(position).PropertyName);
            holder.txt_propertyValue.setText(ListProperties.get(position).value);
        }
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.fly_in_from_top_corner);
        MyView.setAnimation(anim);
        anim.start();
        return MyView;
    }
}

