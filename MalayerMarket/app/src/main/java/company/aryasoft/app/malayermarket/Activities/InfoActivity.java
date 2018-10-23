package company.aryasoft.app.malayermarket.Activities;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import company.aryasoft.app.malayermarket.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class InfoActivity extends AppCompatActivity
{

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private SpannableStringBuilder SetColorSalesPrice(String SalePrice)
    {
        SpannableStringBuilder SalesSpan = new SpannableStringBuilder(SalePrice);
        SalesSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#F44336")), 104, 127, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //------------------------------------
        SpannableStringBuilder SalesSpan2 = new SpannableStringBuilder(SalesSpan);
        SalesSpan2.setSpan(new ForegroundColorSpan(Color.parseColor("#F44336")), 206, 230, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return SalesSpan2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_info);
        TextView info_text = findViewById(R.id.info_text);
        if (getIntent().getIntExtra("info_type", -1) == 1)
        {
            //contact_us
            info_text.setText(SetColorSalesPrice(getString(R.string.contact_us_text)));
        }
        else if (getIntent().getIntExtra("info_type", -1) == 2)
        {
            //about-us
            info_text.setText(getString(R.string.about_us));
        }
        else if (getIntent().getIntExtra("info_type", -1) == 3)
        {
            //roles
            info_text.setText(getString(R.string.roles));
        }
        setFooterBgAnimation();
    }

    private void setFooterBgAnimation()
    {
        ImageView img_about_bg = findViewById(R.id.img_about_bg);
        final ImageView img_about_cloud1 = findViewById(R.id.img_about_cloud1);
        final ImageView img_about_cloud2 = findViewById(R.id.img_about_cloud2);
        Glide.with(this).load(R.drawable.bg_splash).into(img_about_bg);
        Glide.with(this).load(R.drawable.about_cloud).into(img_about_cloud1);
        Glide.with(this).load(R.drawable.about_cloud).into(img_about_cloud2);
        Handler AnimHandler = new Handler();
        Thread AnimThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
                animator.setRepeatCount(-1);
                animator.setInterpolator(new LinearInterpolator());
                animator.setDuration(25000);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
                {

                    public void onAnimationUpdate(ValueAnimator animation)
                    {
                        float width = (float) img_about_cloud1.getWidth();
                        float translationX = width * ((Float) animation.getAnimatedValue()).floatValue();
                        img_about_cloud1.setTranslationX(-translationX);
                        img_about_cloud2.setTranslationX(width - translationX);
                    }
                });
                animator.start();
            }
        });
        AnimHandler.post(AnimThread);

    }
}
