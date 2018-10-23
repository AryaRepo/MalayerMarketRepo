package company.aryasoft.app.malayermarket.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import co.ronash.pushe.Pushe;
import company.aryasoft.app.malayermarket.Adapters.IntroAdapter;
import company.aryasoft.app.malayermarket.R;
import company.aryasoft.app.malayermarket.UtilityAndHelper.SharedPreferencesHelper;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class IntroActivity extends AppCompatActivity implements Runnable
{
    private int currentPage = 0;
    private ViewPager intro_pager = null;
    private MediaPlayer mediaPlayer = null;
    private String[] StrText = new String[8];
    private boolean isStoppedPlayer = false;

    @Override
    public void onBackPressed()
    {
    }

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onStop()
    {
        super.onStop();
        try
        {
            if (!isStoppedPlayer)
            {
                if (mediaPlayer != null)
                {
                    mediaPlayer.stop();
                }
            }
        } catch (Exception ignored)
        {
        }

    }

    private void FillText()
    {
//        StrText[0] = "میخوای دیگه از این به بعد فروشگاه نری؟";
//        StrText[1] = "میخوای هرچیزی خواستی توی خونت خرید کنی \n \nسه سوته برات بیاد! ☺";
//        StrText[2] = "میخوای پولشو آنلاین یا در خونت پرداخت کنی؟! ☺";
//        StrText[3] = "سوپرمارکت آنلاین تحولی در فروشگاه های اینترنتی";
//        StrText[4] = "تنوع محصولاتی خیلی زیاد";
//        StrText[5] = "امنیت در خرید";
//        StrText[6] = "تحویل مواد خرید شما در کمترین زمان ممکن";
//        StrText[7] = "مارو یادت نره\n \nسوپرمارکت آنلاین فروشگاه خودت";
        //----------------------------------------------------
        StrText[0] = "میخوای دیگه از این به بعد فروشگاه نری؟";
        StrText[1] = "میخوای هرچیزی خواستی توی خونت خرید کنی \n \nسه سوته برات بیاد! ☺";
        StrText[2] = "میخوای پولشو آنلاین یا در خونت پرداخت کنی؟! ☺";
        StrText[3] = "ملایر مارکت تحولی در فروشگاه های اینترنتی";
        StrText[4] = "تنوع محصولاتی خیلی زیاد";
        StrText[5] = "امنیت در خرید";
        StrText[6] = "تحویل مواد خرید شما در کمترین زمان ممکن";
        StrText[7] = "مارو یادت نره\n \nملایر مارکت فروشگاه خودت";
    }



    private void changeSliderPage()
    {
        if (currentPage <= intro_pager.getAdapter().getCount())
        {
            intro_pager.setCurrentItem(currentPage++, true);
            intro_pager.postDelayed(this, 5000);
        }

    }

    public void addPage(IntroAdapter v)
    {
        if (v.getCount() < 8)
        {
            v.pagenum++; //or maxPageCount = fragmentPosition+2
            v.notifyDataSetChanged(); //notifyDataSetChanged is important here.
        }
        else
        {

            isStoppedPlayer = true;
            mediaPlayer.stop();
            handler.removeCallbacksAndMessages(this);
            finish();
        }
    }

    private Handler handler;
    private IntroAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_intro);
        playSound(this, "inspiration.mp3");
        intro_pager = findViewById(R.id.intro_pager);
        RelativeLayout rel_skip = findViewById(R.id.rel_skip);
        rel_skip.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new Handler().post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mediaPlayer.stop();
                        isStoppedPlayer = true;
                        SharedPreferencesHelper.WriteBoolean("FirstTime", true);
                        intro_pager.getHandler().removeCallbacks(IntroActivity.this);
                        startActivity(new Intent(IntroActivity.this, LandActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        finish();
                    }
                });

            }

        });
        intro_pager.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });
        FillText();
        mPagerAdapter = new IntroAdapter(getSupportFragmentManager(), StrText);
        intro_pager.setAdapter(mPagerAdapter);
        handler = new Handler();
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mPagerAdapter.getCount() <= 8)
                {
                    changeSliderPage();
                }
                else
                {
                    handler.removeCallbacks(this);
                }
            }
        });
        Pushe.initialize(this, true);

    }

    public void playSound(final Context context, final String fileName)
    {
        mediaPlayer = new MediaPlayer();
        try
        {
            AssetFileDescriptor afd = context.getAssets().openFd(fileName);
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mediaPlayer.prepare();
        } catch (final Exception e)
        {
            e.printStackTrace();
        }
        mediaPlayer.start();
    }

    @Override
    public void run()
    {
        if (currentPage == 8)
        {
            SharedPreferencesHelper.WriteBoolean("FirstTime", true);
            intro_pager.getHandler().removeCallbacks(this);
            startActivity(new Intent(IntroActivity.this, LandActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        }
        else
        {
            addPage(mPagerAdapter);
            changeSliderPage();
        }
    }
}
