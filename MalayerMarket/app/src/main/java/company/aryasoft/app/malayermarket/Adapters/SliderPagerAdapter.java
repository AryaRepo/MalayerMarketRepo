package company.aryasoft.app.malayermarket.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.Toast;

import java.util.ArrayList;

import company.aryasoft.app.malayermarket.ApiModels.SliderModelApi;
import company.aryasoft.app.malayermarket.Fragments.BannerFragment;

/**
 * Created by MohamadAmin on 2/8/2018.
 */

public class SliderPagerAdapter extends FragmentPagerAdapter
{
    private ArrayList<SliderModelApi> SliderList = null;

    public SliderPagerAdapter(FragmentManager fm, ArrayList<SliderModelApi> SliderList)
    {
        super(fm);
        this.SliderList = new ArrayList<>();
        if (SliderList != null)
        {
            this.SliderList = SliderList;
        }
    }

    @Override
    public Fragment getItem(int position)
    {
        return new BannerFragment().newInstance(SliderList.get(position).ImageName);
        //--------------
        //return new BannerFragment().newInstance(SliderList.get(position).ImageName);
    }

    @Override
    public int getCount()
    {
        return SliderList.size();
    }

    /*
        public int getCount()
        {
            return SliderList.size();
        }
        */
 /*   @Override
    public float getPageWidth(int position)
    {
        if (position == SliderList.size() - 1)
        {
            return 0.94f;
        }
        else
        {
            return 0.84f;
        }
    }
*/

}
