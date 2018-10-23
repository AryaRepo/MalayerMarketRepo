package company.aryasoft.app.malayermarket.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import company.aryasoft.app.malayermarket.ApiModels.ProductCommentApiModel;
import company.aryasoft.app.malayermarket.Fragments.CommentsFragment;
import company.aryasoft.app.malayermarket.Fragments.DetailFragment;
import company.aryasoft.app.malayermarket.Models.DetailModel;

public class DetailPagerAdapter extends FragmentPagerAdapter
{
    private String[] titleList = new String[]{"      نظرات      ", "      جزئیات      "};
    private DetailModel detailModel;

    public DetailPagerAdapter(FragmentManager FragmentManager, DetailModel detailModel)
    {
        super(FragmentManager);
        this.detailModel=detailModel;
    }

    @Override
    public Fragment getItem(int position)
    {
        Fragment DisplayedFragment;
        switch (position)
        {
            case 1:
                DisplayedFragment = DetailFragment.newInstance(detailModel);
                break;
            case 0:
                DisplayedFragment = CommentsFragment.newInstance(detailModel.CommentsList,detailModel.ProductID);
                break;
            default:
                DisplayedFragment = DetailFragment.newInstance(detailModel);
        }

        return DisplayedFragment;
    }

    @Override
    public int getCount()
    {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return titleList[position];
    }
}
