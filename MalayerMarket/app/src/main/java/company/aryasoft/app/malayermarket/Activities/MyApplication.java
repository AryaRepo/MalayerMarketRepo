package company.aryasoft.app.malayermarket.Activities;

import android.app.Application;
import android.content.Context;

import com.orm.SugarContext;

import company.aryasoft.app.malayermarket.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;


public class MyApplication extends Application
{
    private static Context context;
    public static Context getContext()
    {
        return context;
    }
    @Override
    public void onCreate()
    {
        super.onCreate();
        context = this;
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
            .setDefaultFontPath("fonts/iransans.ttf")
            .setFontAttrId(R.attr.fontPath)
            .build());
        SugarContext.init(this);
    }
}
