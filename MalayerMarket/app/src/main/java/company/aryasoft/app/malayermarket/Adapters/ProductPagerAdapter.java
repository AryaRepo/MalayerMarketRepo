package company.aryasoft.app.malayermarket.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import company.aryasoft.app.malayermarket.Fragments.ProductImageFragment;

public class ProductPagerAdapter extends FragmentPagerAdapter
{
    private String[] _Images;
    public ProductPagerAdapter(FragmentManager fm, String[] images)
    {
        super(fm);
        _Images=images;
    }

    @Override
    public Fragment getItem(int position)
    {
        return new ProductImageFragment().newInstance(_Images[position]);
    }

    @Override
    public int getCount()
    {
        return _Images.length;
    }
}
