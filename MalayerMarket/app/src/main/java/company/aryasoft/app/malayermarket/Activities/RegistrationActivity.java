package company.aryasoft.app.malayermarket.Activities;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Color;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;

import company.aryasoft.app.malayermarket.ApiModels.RecoverPasswordApiModel;
import company.aryasoft.app.malayermarket.ApiModels.RegisterAccountApiModel;
import company.aryasoft.app.malayermarket.ModifiedInterfaceAndAbstracts.ModifiedAnimationListener;
import company.aryasoft.app.malayermarket.ModifiedInterfaceAndAbstracts.ModifiedBottomSheetBehavior;
import company.aryasoft.app.malayermarket.ModifiedInterfaceAndAbstracts.ModifiedTextWatcher;
import company.aryasoft.app.malayermarket.Modules.LoginRegisterModule;
import company.aryasoft.app.malayermarket.R;
import company.aryasoft.app.malayermarket.UtilityAndHelper.SharedPreferencesHelper;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import company.aryasoft.app.malayermarket.UtilityAndHelper.HelperModule;

public class RegistrationActivity extends AppCompatActivity
{
    //region Variables and Objects
    private Boolean isLoginShowing = false;
    private EditText edt_phoneNumber_login;
    private EditText edt_password_login;
    private ImageButton btn_login;
    private EditText edt_phoneNumber_signUp;
    private EditText edt_phone_forget_pass;
    private EditText edt_password_signUp;
    private EditText edt_password_sign_up_rpt;
    private EditText edt_email_signUp;
    private ImageButton btn_signUp;
    private RelativeLayout relContainer;
    private RelativeLayout relShadow;
    private TextView txtShowSignUpTab, txtShowSignInTab;
    private BottomSheetBehavior bottomSheetBehavior;
    private TextView txtForgetPassword;
    private Button btnShowSignUpTab, btnShowSignInTab;
    private LinearLayout relSignUp, relSignIn;
    private ImageButton btn_submit_forget_password;
    private LoginRegisterModule LoginRegister;
    //endregion

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed()
    {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
        {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED)
        {
            super.onBackPressed();
            startActivity(new Intent(RegistrationActivity.this, LandActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_regisration);
        initViews();
        initEvents();
        showSignInTab();
    }

    private void initViews()
    {
        edt_phoneNumber_login = findViewById(R.id.edt_phone_sign_in);
        edt_password_login = findViewById(R.id.edt_password_sign_in);
        btn_login = findViewById(R.id.btn_sign_in);
        edt_phoneNumber_signUp = findViewById(R.id.edt_phone_sign_up);
        edt_phone_forget_pass = findViewById(R.id.edt_phone_forget_pass);
        edt_password_signUp = findViewById(R.id.edt_password_sign_up);
        edt_password_sign_up_rpt = findViewById(R.id.edt_password_sign_up_rpt);
        edt_email_signUp = findViewById(R.id.edt_email_sign_up);
        btn_signUp = findViewById(R.id.btn_sign_up);
        btnShowSignInTab = findViewById(R.id.btn_show_sign_in_tab);
        btn_submit_forget_password = findViewById(R.id.btn_submit_forget_password);
        btnShowSignUpTab = findViewById(R.id.btn_show_sign_up_tab);
        relSignIn = findViewById(R.id.rel_sign_in);
        relSignUp = findViewById(R.id.rel_sign_up);
        txtShowSignInTab = findViewById(R.id.txt_show_sign_in_tab);
        txtShowSignUpTab = findViewById(R.id.txt_show_sign_up_tab);
        relContainer = findViewById(R.id.rel_container);
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet1));
        txtForgetPassword = findViewById(R.id.txt_forget_password);
        relShadow = findViewById(R.id.rel_shadow);
        //-----------------------
        Glide.with(this).load(R.drawable.profile_bg).into((ImageView) findViewById(R.id.img_bg_reg));
        Glide.with(this).load(R.drawable.malayer_market_logo).into((ImageView) findViewById(R.id.sgn_logo));
        Glide.with(this).load(R.drawable.malayer_market_logo).into((ImageView) findViewById(R.id.login_logo));
        //----------------------
    }

    private void initEvents()
    {
        LoginRegister = new LoginRegisterModule(this);
        txtForgetPassword.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openRetrievePasswordPanel();
            }
        });
        btnShowSignUpTab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isLoginShowing)
                {
                    applyRevealEffect(txtShowSignUpTab, "#aaE91E63");
                    showSignUpTab();
                }
            }
        });
        btnShowSignInTab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!isLoginShowing)
                {
                    applyRevealEffect(txtShowSignInTab, "#99FE1743");
                    showSignInTab();
                }
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                LoginUser();
            }
        });
        btn_signUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                RegisterUser();
            }
        });
        bottomSheetBehavior.setBottomSheetCallback(new ModifiedBottomSheetBehavior()
        {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState)
            {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                {
                    closeRetrievePasswordPanel();
                }
            }
        });
        btn_submit_forget_password.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!(HelperModule.IsEditTextEmpty(edt_phone_forget_pass)))
                {
                    RetrievePassword();
                }
                else
                {
                    Toast.makeText(RegistrationActivity.this, "کاربرگرامی لطفا شماره تلفن همراه خود را وارد کنید", Toast.LENGTH_SHORT).show();
                    applyShakeAnim(edt_phone_forget_pass);
                    vibrateDevice(200);
                }
            }
        });
        edt_phoneNumber_signUp.addTextChangedListener(new ModifiedTextWatcher()
        {
            @Override
            public void afterTextChanged(Editable s)
            {
                if (s.length() == 11)
                {
                    edt_password_signUp.requestFocus();
                }
            }
        });
        edt_phoneNumber_login.addTextChangedListener(new ModifiedTextWatcher()
        {
            @Override
            public void afterTextChanged(Editable s)
            {
                if (s.length() == 11)
                {
                    edt_password_login.requestFocus();
                }
            }
        });
        LoginRegister.setOnActiveUserListener(new LoginRegisterModule.OnActiveUserListener()
        {
            @Override
            public void OnActivated()
            {
                btnShowSignUpTab.setEnabled(false);
                SharedPreferencesHelper.WriteInt("UserState", 2);
                if (!(HelperModule.IsEditTextEmpty(edt_phoneNumber_signUp, edt_password_signUp)))
                {
                    edt_phoneNumber_login.setText(edt_phoneNumber_signUp.getText().toString());
                    edt_password_login.setText(edt_password_signUp.getText().toString());
                }
                LoginUser();
            }
        });
        edt_email_signUp.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_NEXT)
                {
                    HelperModule.hideSoftKey(v.getContext(), v);
                }
                return true;
            }
        });
        edt_password_login.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_NEXT)
                {
                    HelperModule.hideSoftKey(v.getContext(), v);
                }
                return true;
            }
        });
    }

    private void RetrievePassword()
    {
        closeRetrievePasswordPanel();
        RecoverPasswordApiModel recoverPasswordApiModel = new RecoverPasswordApiModel();
        recoverPasswordApiModel.MobileNumber = edt_phone_forget_pass.getText().toString();
        LoginRegister.RetrievePassword(recoverPasswordApiModel);
    }


    private void LoginUser()
    {
        if (HelperModule.IsEditTextEmpty(edt_phoneNumber_login, edt_password_login))
        {
            applyShakeAnim(edt_phoneNumber_login, edt_password_login);
            vibrateDevice(200);
            Toast.makeText(RegistrationActivity.this, "کاربر گرامی نام کاربری یا رمز عبور وارد نشده", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!(edt_phoneNumber_login.getText().toString().startsWith("09") && edt_phoneNumber_login.getText().toString().length() == 11))
        {
            Toast.makeText(this, "قالب شماره تلفن وارد شده صحیح نیست.", Toast.LENGTH_SHORT).show();
            return;
        }
        LoginRegister.LoginUser(edt_phoneNumber_login.getText().toString(), edt_password_login.getText().toString());
    }

    private void RegisterUser()
    {
        if (HelperModule.IsEditTextEmpty(edt_phoneNumber_signUp, edt_password_signUp, edt_password_sign_up_rpt))
        {
            applyShakeAnim(edt_phoneNumber_signUp, edt_password_signUp, edt_password_sign_up_rpt);
            vibrateDevice(200);
            Toast.makeText(RegistrationActivity.this, "کاربر گرامی نام کاربری یا رمز عبور وارد نشده", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!(edt_phoneNumber_signUp.getText().toString().startsWith("09") && edt_phoneNumber_signUp.getText().toString().length() == 11))
        {
            Toast.makeText(this, "قالب شماره تلفن وارد شده صحیح نیست.", Toast.LENGTH_SHORT).show();
            applyShakeAnim(edt_phoneNumber_signUp);
            vibrateDevice(200);
            return;
        }
        if (HelperModule.IsEditTextEmpty(edt_password_signUp, edt_password_sign_up_rpt))
        {
            applyShakeAnim(edt_password_signUp, edt_password_sign_up_rpt);
            vibrateDevice(200);
            Toast.makeText(RegistrationActivity.this, "کاربر گرامی رمز عبور به همراه تکرار رمز عبور را وارد نکردید ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (edt_password_signUp.getText().toString().length() < 6 && edt_password_sign_up_rpt.getText().toString().length() < 6)
        {
            Toast.makeText(RegistrationActivity.this, "حداقل میزان پسورد 6 حرف است", Toast.LENGTH_SHORT).show();
            return;
        }


        if (!(edt_password_signUp.getText().toString().equals(edt_password_sign_up_rpt.getText().toString())))
        {
            Toast.makeText(this, "کاربر عزیز رمز عبور و تکرار رمز عبور شما باهم مغایرت ندارند .لطفا دوباره بررسی کنید.", Toast.LENGTH_LONG).show();
            return;
        }

        RegisterAccountApiModel objRegister = new RegisterAccountApiModel();
        objRegister.MobileNumber = HelperModule.arabicToDecimal(edt_phoneNumber_signUp.getText().toString());
        objRegister.Password = HelperModule.arabicToDecimal(edt_password_signUp.getText().toString());
        objRegister.RePassword = objRegister.Password;
        if (edt_email_signUp.getText().toString().length() > 0)
        {
            if (HelperModule.EmailValidation(edt_email_signUp.getText().toString()))
            {
                objRegister.Email = edt_email_signUp.getText().toString();
            }
            else
            {
                applyShakeAnim(edt_phoneNumber_signUp, edt_password_signUp, edt_email_signUp);
                vibrateDevice(200);
                Toast.makeText(RegistrationActivity.this, "کاربر گرامی ایمیل وارد شده در قالب صحیح ایمیل وارد نشده.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        LoginRegister.RegisterUser(objRegister);
        //-------------------------------------
    }

    //region Animations
    private void vibrateDevice(long vibrateDuration)
    {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(vibrateDuration);
    }

    private void applyShakeAnim(View... views)
    {
        for (View v : views)
        {
            YoYo.with(Techniques.Shake).duration(500).interpolate(new AccelerateInterpolator()).playOn(v);
        }
    }

    private void applyRevealEffect(TextView txt, String color)
    {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
        {
            int cx = (txt.getLeft() + txt.getRight()) / 2;
            int cy = (txt.getTop() + txt.getBottom()) / 2;
            int finalRadius = Math.max(relContainer.getWidth(), relContainer.getHeight());
            Animator anim = ViewAnimationUtils.createCircularReveal(relContainer, cx, cy, 0, finalRadius);
            relContainer.setBackgroundColor(Color.parseColor(color));
            anim.start();
        }
    }

    private void showSignInTab()
    {
        relSignUp.setVisibility(View.GONE);
        relSignIn.setVisibility(View.VISIBLE);
        txtShowSignInTab.setVisibility(View.VISIBLE);
        txtShowSignUpTab.setVisibility(View.INVISIBLE);
        isLoginShowing = true;
        signInAnimation();
    }

    private void showSignUpTab()
    {
        relSignUp.setVisibility(View.VISIBLE);
        relSignIn.setVisibility(View.GONE);
        txtShowSignUpTab.setVisibility(View.VISIBLE);
        txtShowSignInTab.setVisibility(View.INVISIBLE);
        isLoginShowing = false;
        signUpAnimation();
    }

    private void applyAlphaAnim(View v, float f, float t, long duration)
    {
        AlphaAnimation alphaAnimation = new AlphaAnimation(f, t);
        alphaAnimation.setDuration(duration);
        alphaAnimation.setInterpolator(new AccelerateInterpolator());
        v.startAnimation(alphaAnimation);
    }

    private void applyTranslateAnim(View v, int fX, int tX, int fY, int tY, long duration)
    {
        TranslateAnimation translateAnimation = new TranslateAnimation(fX, tX, fY, tY);
        translateAnimation.setDuration(duration);
        translateAnimation.setInterpolator(new AccelerateInterpolator());
        v.startAnimation(translateAnimation);
    }

    private void applyAnimSet()
    {
        AnimationSet animationSet = new AnimationSet(true);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, -5);
        translateAnimation.setDuration(100);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(translateAnimation);
        animationSet.setDuration(300);
        animationSet.setInterpolator(new AccelerateInterpolator());
        relContainer.startAnimation(animationSet);
    }

    private void signInAnimation()
    {
        AlphaAnimation alphaAnimation1 = new AlphaAnimation(0.0f, 1.0f);
        final AlphaAnimation alphaAnimation2 = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation1.setDuration(200);
        alphaAnimation2.setDuration(200);
        alphaAnimation1.setInterpolator(new AccelerateInterpolator());
        alphaAnimation2.setInterpolator(new AccelerateInterpolator());
        edt_phoneNumber_login.startAnimation(alphaAnimation1);
        alphaAnimation1.setAnimationListener(new ModifiedAnimationListener()
        {
            @Override
            public void onAnimationEnd(Animation animation)
            {
                edt_password_login.startAnimation(alphaAnimation2);
            }
        });
    }

    private void signUpAnimation()
    {
        AlphaAnimation alphaAnimation1 = new AlphaAnimation(0.0f, 1.0f);
        final AlphaAnimation alphaAnimation2 = new AlphaAnimation(0.0f, 1.0f);
        final AlphaAnimation alphaAnimation3 = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation1.setDuration(200);
        alphaAnimation2.setDuration(200);
        alphaAnimation3.setDuration(200);
        alphaAnimation1.setInterpolator(new AccelerateInterpolator());
        alphaAnimation2.setInterpolator(new AccelerateInterpolator());
        alphaAnimation3.setInterpolator(new AccelerateInterpolator());
        edt_email_signUp.startAnimation(alphaAnimation1);
        alphaAnimation1.setAnimationListener(new ModifiedAnimationListener()
        {
            @Override
            public void onAnimationEnd(Animation animation)
            {
                edt_phoneNumber_signUp.startAnimation(alphaAnimation2);
                alphaAnimation2.setAnimationListener(new ModifiedAnimationListener()
                {
                    @Override
                    public void onAnimationEnd(Animation animation)
                    {
                        edt_password_signUp.startAnimation(alphaAnimation3);
                    }
                });
            }
        });
        //----------------------------------------------------------------------------
    }

    private void closeRetrievePasswordPanel()
    {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        applyAlphaAnim(relContainer, 0, 1, 300);
        applyTranslateAnim(relContainer, 0, 0, 1, 0, 300);
        applyAlphaAnim(relShadow, 1, 0, 300);
        relContainer.setVisibility(View.VISIBLE);
        relShadow.setVisibility(View.GONE);
    }

    private void openRetrievePasswordPanel()
    {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        applyAnimSet();
        applyAlphaAnim(relShadow, 0, 1, 300);
        relContainer.setVisibility(View.GONE);
        relShadow.setVisibility(View.VISIBLE);

    }
}
