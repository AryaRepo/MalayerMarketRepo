package company.aryasoft.app.malayermarket.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import company.aryasoft.app.malayermarket.R;

public class ProductImageFragment extends Fragment
{

    public static ProductImageFragment newInstance(String imgIg)
    {
        ProductImageFragment productImageFragment = new ProductImageFragment();
        Bundle args = new Bundle();
        args.putString("img_id", imgIg);
        productImageFragment.setArguments(args);
        return productImageFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragmet_product_image, container, false);
        ImageView imgFrgDetail=(ImageView)view.findViewById(R.id.img_frg_detail);
        Glide.with(view.getContext()).load(view.getContext().getResources().getString(R.string.ProductImageFolder)+getArguments().getString("img_id")).apply(new RequestOptions()
                .centerCrop()
                .error(R.drawable.no_img)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH)).into(imgFrgDetail);
        return view;
    }

}
