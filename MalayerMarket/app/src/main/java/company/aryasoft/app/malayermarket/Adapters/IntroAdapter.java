package company.aryasoft.app.malayermarket.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import company.aryasoft.app.malayermarket.Fragments.IntroFragment;

public class IntroAdapter extends FragmentStatePagerAdapter
{
    String[] str;
   public static int pagenum=1;
    public IntroAdapter(FragmentManager fm, String[] str)
    {
        super(fm);
        this.str=str;
    }

    @Override
    public Fragment getItem(int position)
    {

        return  IntroFragment.newInstance(str[position],position,false);
    }

    @Override
    public int getCount()
    {
        return pagenum;
    }
}
