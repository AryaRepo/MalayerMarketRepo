package company.aryasoft.app.malayermarket.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import company.aryasoft.app.malayermarket.R;

/**
 * Created by MohamadAmin on 2/8/2018.
 */

public class BannerFragment extends Fragment
{
    public static BannerFragment newInstance(int imgIg)
    {
        BannerFragment bannerFragment = new BannerFragment();
        Bundle args = new Bundle();
        args.putInt("img_id", imgIg);
        bannerFragment.setArguments(args);
        return bannerFragment;
    }

    public static BannerFragment newInstance(String imgIg)
    {
        BannerFragment bannerFragment = new BannerFragment();
        Bundle args = new Bundle();
        args.putString("img_id", imgIg);
        bannerFragment.setArguments(args);
        return bannerFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_banner, container, false);
        ImageView imgFragmentBanner = (ImageView) view.findViewById(R.id.img_frg_banner);

        Glide.with(view.getContext()).load(getResources().getString(R.string.SliderImageFolder) + getArguments().getString("img_id")).into(imgFragmentBanner);
        return view;
    }
}
