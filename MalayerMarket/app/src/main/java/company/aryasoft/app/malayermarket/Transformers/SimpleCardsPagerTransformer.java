package company.aryasoft.app.malayermarket.Transformers;

import android.os.Build;
import android.support.v4.view.ViewPager;
import android.view.View;

public class SimpleCardsPagerTransformer implements ViewPager.PageTransformer
{

    @Override
    public void transformPage(View page, float position)
    {
        float absPosition = Math.abs(position);

        if (absPosition >= 1)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                page.setElevation(2);
            }
            page.setScaleY(0.8f);
        }
        else
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                page.setElevation(((1 - absPosition) * 10 + 2));
            }
            page.setScaleY((0.8f - 1) * absPosition + 1);
        }
    }

}