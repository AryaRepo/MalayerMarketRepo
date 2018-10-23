package company.aryasoft.app.malayermarket.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import java.util.Timer;
import java.util.TimerTask;
import cn.pedant.SweetAlert.OptAnimationLoader;
import cn.pedant.SweetAlert.SweetAlertDialog;
import company.aryasoft.app.malayermarket.Adapters.HomeRecyclerAdapter;
import company.aryasoft.app.malayermarket.Adapters.ProductRecyclerAdapter;
import company.aryasoft.app.malayermarket.Adapters.SpecialRecyclerAdapter;
import company.aryasoft.app.malayermarket.ApiModels.ApplicationUpdateModel;
import company.aryasoft.app.malayermarket.ApiModels.ProfileInfoApiModel;
import company.aryasoft.app.malayermarket.BuildConfig;
import company.aryasoft.app.malayermarket.Fragments.CategoryFragment;
import company.aryasoft.app.malayermarket.Fragments.HomeFragment;
import company.aryasoft.app.malayermarket.Fragments.SearchFragment;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ApiCallBack;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ApiServiceGenerator;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ServiceRequestApi;
import company.aryasoft.app.malayermarket.R;
import company.aryasoft.app.malayermarket.UtilityAndHelper.HelperModule;
import company.aryasoft.app.malayermarket.UtilityAndHelper.ProductCountChanged;
import company.aryasoft.app.malayermarket.UtilityAndHelper.SharedPreferencesHelper;
import company.aryasoft.app.malayermarket.UtilityAndHelper.ShoppingCartManger;
import retrofit2.Call;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
public class LandActivity extends AppCompatActivity implements View.OnClickListener
{
    private ServiceRequestApi requestApi;
    private activeFragment currentFragment = activeFragment.main;
    private DrawerLayout drawer;
    private ImageButton btn_show_shopping_cart;
    private RelativeLayout rel_search_fragment;
    private RelativeLayout rel_main_fragment;
    private RelativeLayout rel_category;
    //-----------
    private TableRow row_loginRegister;
    private TableRow row_manage_profile;
    private TableRow row_share_app;
    private TableRow row_orders;
    private TableRow row_fav_cart;
    private TableRow row_contact_us;
    private TableRow row_messages;
    private TableRow row_about_us;
    private TextView txt_greetings_land;
    private TextView txtSearchNav, txtMainNav, txtCategoryNav;
    private TextView txt_cart_counter;
    private ImageView btnSearchFragment, btnMainFragment, btnCategoryFragment;
    private ImageView profile_image;
    private Timer CartBadgeTimer;
    ///------------------------Adapters
    private SpecialRecyclerAdapter NewlyProductSpecialRecyclerAdapter;
    private SpecialRecyclerAdapter BestProductSpecialRecyclerAdapter;
    private ProductRecyclerAdapter ProductAdapter;
    private HomeRecyclerAdapter HomeAdapter;
    private SweetAlertDialog LoadingDialog;
    ///=====================
    private int UserId = 0;
    private int UserState = -1;
    private Dialog UpdateDL;


    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 120)
        {
            if (grantResults.length > 0)
            {
                new ShareAppAsync().execute();
            }
        }

    }

    private void InitLandActivityBasics()
    {
        GetShoppingCartCount(ShoppingCartManger.GetShoppingCart().size());
        if(this.NewlyProductSpecialRecyclerAdapter!=null)
        {
            this.NewlyProductSpecialRecyclerAdapter.notifyDataSetChanged();
        }
        if(this.BestProductSpecialRecyclerAdapter!=null)
        {
            this.BestProductSpecialRecyclerAdapter.notifyDataSetChanged();
        }
        if(this.HomeAdapter!=null)
        {
            this.HomeAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        InitLandActivityBasics();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        InitLandActivityBasics();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.action_settings)
        {
            drawer.openDrawer(GravityCompat.END);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        if (currentFragment == activeFragment.main)
        {
            HelperModule.ShowExitAppDialog(this);
            return;
        }
        LoadHomeFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        UserState = SharedPreferencesHelper.ReadInt("UserState");
        UserId = SharedPreferencesHelper.ReadInt("UserId");
        InitLandActivityBasics();
        LoadUserProfileName();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        InitLandActivityBasics();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        SharedPreferencesHelper.InitPreferences(getApplicationContext());

        if (!SharedPreferencesHelper.ReadBoolean("FirstTime"))
        {
            startActivity(new Intent(LandActivity.this, IntroActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();

        }
        else
        {
            setContentView(R.layout.activity_land);
            requestApi = ApiServiceGenerator.GetApiService();
            InitViews();
            InitEvents();
            UpdateApplication();
        }
    }

    private void InitViews()
    {
        Toolbar toolbar = findViewById(R.id.include_land_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        profile_image = findViewById(R.id.profile_image);
        Glide.with(this).load(R.drawable.bg_splash).into(((ImageView) findViewById(R.id.img_bg_land)));
        txt_greetings_land = findViewById(R.id.txt_greetings_land);
        txt_cart_counter = findViewById(R.id.txt_cart_counter);
        //---------------------
        LoadingDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        LoadingDialog.setTitleText(getString(R.string.loading_title));
        LoadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        LoadingDialog.setCancelable(false);
        //----------------------
        row_loginRegister = findViewById(R.id.row_loginRegister);
        row_manage_profile = findViewById(R.id.row_manage_profile);
        row_share_app = findViewById(R.id.row_share_app);
        row_orders = findViewById(R.id.row_orders);
        row_fav_cart = findViewById(R.id.row_fav_cart);
        row_messages= findViewById(R.id.row_messages);
        row_contact_us = findViewById(R.id.row_contact_us);
        row_about_us = findViewById(R.id.row_about_us);
        TextView nav_menu_title_loginRegister = findViewById(R.id.nav_menu_title_loginRegister);
        if (SharedPreferencesHelper.ReadInt("UserState") == 3)
        {
            nav_menu_title_loginRegister.setText("خروج از حساب کاربری");
        }
        row_loginRegister.setOnClickListener(this);
        row_manage_profile.setOnClickListener(this);
        row_share_app.setOnClickListener(this);
        row_orders.setOnClickListener(this);
        row_messages.setOnClickListener(this);
        row_fav_cart.setOnClickListener(this);
        row_contact_us.setOnClickListener(this);
        row_about_us.setOnClickListener(this);
        btn_show_shopping_cart = findViewById(R.id.btn_show_shopping_cart);
        rel_search_fragment = findViewById(R.id.rel_search_fragment);
        ImageView img_drawer_bg = findViewById(R.id.img_drawer_bg);
        Glide.with(this).load(R.drawable.bg_splash).into(img_drawer_bg);
        rel_main_fragment = findViewById(R.id.rel_main_fragment);
        rel_category = findViewById(R.id.rel_category);
        drawer = findViewById(R.id.drawer_layout);
        btn_show_shopping_cart.setOnClickListener(this);
        rel_search_fragment.setOnClickListener(this);
        rel_main_fragment.setOnClickListener(this);
        rel_category.setOnClickListener(this);
        btnSearchFragment = findViewById(R.id.img_search_nav);
        btnCategoryFragment = findViewById(R.id.img_category_nav);
        btnMainFragment = findViewById(R.id.img_main_fragment_nav);
        txtCategoryNav = findViewById(R.id.txt_category_nav);
        txtMainNav = findViewById(R.id.txt_main_fragment_nav);
        txtSearchNav = findViewById(R.id.txt_search_nav);
        HomeAdapter = new HomeRecyclerAdapter(this);
        ProductAdapter = new ProductRecyclerAdapter(this);
        NewlyProductSpecialRecyclerAdapter = new SpecialRecyclerAdapter(this);
        BestProductSpecialRecyclerAdapter = new SpecialRecyclerAdapter(this);
        changeNavTextColor(txtSearchNav, "#BDBDBD");
        changeNavTextColor(txtMainNav, "#757575");
        changeNavTextColor(txtCategoryNav, "#BDBDBD");
        changeVectorColor(btnSearchFragment, R.color.deselected_nav_icon_color);
        changeVectorColor(btnCategoryFragment, R.color.deselected_nav_icon_color);
        changeVectorColor(btnMainFragment, R.color.selected_nav_icon_color);
        GetShoppingCartCount(ShoppingCartManger.GetShoppingCart().size());
        RelativeLayout include_bottom_nav = findViewById(R.id.include_bottom_nav);
        Animation slide_up = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        include_bottom_nav.startAnimation(slide_up);
        //--------------
        HelperModule.LoadFragment(R.id.main_placeholder, new HomeFragment(NewlyProductSpecialRecyclerAdapter, BestProductSpecialRecyclerAdapter, HomeAdapter), getSupportFragmentManager(), R.anim.zoom_enter, R.anim.zoom_exit);
        showActionBar(true);
        currentFragment = activeFragment.main;

    }

    private void InitEvents()
    {
        NewlyProductSpecialRecyclerAdapter.SetOnCountChange(HomeAdapter, BestProductSpecialRecyclerAdapter, new ProductCountChanged()
        {
            @Override
            public void OnCountChanged(int Count)
            {
                GetShoppingCartCount(Count);
            }
        });
        BestProductSpecialRecyclerAdapter.SetOnCountChange(HomeAdapter, NewlyProductSpecialRecyclerAdapter, new ProductCountChanged()
        {
            @Override
            public void OnCountChanged(int Count)
            {
                GetShoppingCartCount(Count);
            }
        });
        HomeAdapter.SetOnBestCountChange(BestProductSpecialRecyclerAdapter, new ProductCountChanged()
        {
            @Override
            public void OnCountChanged(int Count)
            {
                GetShoppingCartCount(Count);
            }
        });
        HomeAdapter.SetOnNewlyCountChange(NewlyProductSpecialRecyclerAdapter, new ProductCountChanged()
        {
            @Override
            public void OnCountChanged(int Count)
            {
                GetShoppingCartCount(Count);
            }
        });
        ProductAdapter.SetOnCountChange(new ProductCountChanged()
        {
            @Override
            public void OnCountChanged(int Count)
            {
                GetShoppingCartCount(Count);
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == rel_search_fragment.getId())
        {
            if (currentFragment != activeFragment.search)
            {
                LoadSearchFragment();

            }
        }
        else if (v.getId() == rel_main_fragment.getId())
        {
            if (currentFragment != activeFragment.main)
            {
                LoadHomeFragment();
            }
        }
        else if (v.getId() == rel_category.getId())
        {
            if (currentFragment != activeFragment.category)
            {
                LoadCategoryFragment();
            }
        }
        else if (v.getId() == btn_show_shopping_cart.getId())
        {
            if (ShoppingCartManger.GetShoppingCart().size() == 0)
            {
                Toast.makeText(this, "سبد خرید شما خالی است.", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(LandActivity.this, ShoppingCartActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));

        }
        else if (v.getId() == row_loginRegister.getId())
        {
            drawer.closeDrawer(Gravity.END);
            new Handler().postDelayed(new Runnable()
            {
                public void run()
                {
                    if (UserState == 3)
                    {
                        android.support.v7.app.AlertDialog.Builder NotifyDialogAlert = new android.support.v7.app.AlertDialog.Builder(LandActivity.this);
                        NotifyDialogAlert.setCancelable(false);
                        View AlertView = View.inflate(LandActivity.this, R.layout.dialog_notify, null);
                        TextView NotifyDialogTitle = AlertView.findViewById(R.id.txt_dialog_title);
                        TextView NotifyDialogMessage = AlertView.findViewById(R.id.txt_dialog_message);
                        NotifyDialogTitle.setText("خروج از حساب");
                        NotifyDialogMessage.setText("دوست من آیا مایل به خروج از حساب کاربری خود هستید؟");
                        NotifyDialogAlert.setView(AlertView);
                        NotifyDialogAlert.setPositiveButton("بله", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                SharedPreferencesHelper.ClearAllPreferences();
                                SharedPreferencesHelper.WriteBoolean("FirstTime", true);
                                dialog.dismiss();
                                System.exit(0);
                            }
                        });
                        NotifyDialogAlert.setNegativeButton("خیر", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        });
                        //--------------------
                        NotifyDialogAlert.show();
                    }
                    else
                    {
                        startActivity(new Intent(LandActivity.this, RegistrationActivity.class));
                        finish();
                    }
                }
            }, 150);


        }
        else if (v.getId() == row_manage_profile.getId())
        {
            drawer.closeDrawer(Gravity.END);
            new Handler().postDelayed(new Runnable()
            {
                public void run()
                {
                    if (UserState == 3)
                    {
                        startActivity(new Intent(LandActivity.this, ProfileActivity.class));
                    }
                    else
                    {
                        drawer.closeDrawers();
                        HelperModule.ShowSignInAlert(LandActivity.this);
                    }
                }
            }, 150);

        }
        else if (v.getId() == row_messages.getId())
        {
            drawer.closeDrawer(Gravity.END);
            new Handler().postDelayed(new Runnable()
            {
                public void run()
                {
                    if (SharedPreferencesHelper.ReadInt("UserState") == 3)
                    {
                        startActivity(new Intent(LandActivity.this, TicketsMessageActivity.class));
                    }
                    else
                    {
                        HelperModule.ShowSignInAlert(LandActivity.this);
                    }
                }
            }, 150);
        }
        else if (v.getId() == row_fav_cart.getId())
        {
            drawer.closeDrawer(Gravity.END);
            new Handler().postDelayed(new Runnable()
            {
                public void run()
                {
                    startActivity(new Intent(LandActivity.this, FavouriteCartActivity.class));
                }
            }, 150);
        }
        else if (v.getId() == row_share_app.getId())
        {
            drawer.closeDrawer(Gravity.END);
            //----------------------------------
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 120);
                    return;
                }
                new ShareAppAsync().execute();
                return;
            }
            new ShareAppAsync().execute();
        }
        else if (v.getId() == row_orders.getId())
        {
            drawer.closeDrawer(Gravity.END);
            new Handler().postDelayed(new Runnable()
            {
                public void run()
                {
                    if (SharedPreferencesHelper.ReadInt("UserState") == 3)
                    {
                        startActivity(new Intent(LandActivity.this, OrdersActivity.class));
                    }
                    else
                    {
                        HelperModule.ShowSignInAlert(LandActivity.this);
                    }
                }
            }, 150);

        }
        else if (v.getId() == row_contact_us.getId())
        {
            drawer.closeDrawer(Gravity.END);
            new Handler().postDelayed(new Runnable()
            {
                public void run()
                {
                    startActivity(new Intent(LandActivity.this, InfoActivity.class).putExtra("info_type", 1));
                }
            }, 150);

        }
        else if (v.getId() == row_about_us.getId())
        {
            drawer.closeDrawer(Gravity.END);
            new Handler().postDelayed(new Runnable()
            {
                public void run()
                {
                    startActivity(new Intent(LandActivity.this, InfoActivity.class).putExtra("info_type", 2));
                }
            }, 150);
        }
    }


    private void LoadSearchFragment()
    {
        changeNavTextColor(txtSearchNav, "#757575");
        changeNavTextColor(txtMainNav, "#BDBDBD");
        changeNavTextColor(txtCategoryNav, "#BDBDBD");
        NewlyProductSpecialRecyclerAdapter.ClearItems();
        BestProductSpecialRecyclerAdapter.ClearItems();
        ProductAdapter.ClearItems();
        HomeAdapter.ClearItems();
        changeVectorColor(btnSearchFragment, R.color.selected_nav_icon_color);
        changeVectorColor(btnCategoryFragment, R.color.deselected_nav_icon_color);
        changeVectorColor(btnMainFragment, R.color.deselected_nav_icon_color);
        HelperModule.LoadFragment(R.id.main_placeholder, new SearchFragment(ProductAdapter), getSupportFragmentManager(), R.anim.zoom_enter, R.anim.zoom_exit);
        showActionBar(false);
        currentFragment = activeFragment.search;
    }

    private void LoadCategoryFragment()
    {
        changeNavTextColor(txtSearchNav, "#BDBDBD");
        changeNavTextColor(txtMainNav, "#BDBDBD");
        changeNavTextColor(txtCategoryNav, "#757575");
        NewlyProductSpecialRecyclerAdapter.ClearItems();
        BestProductSpecialRecyclerAdapter.ClearItems();
        ProductAdapter.ClearItems();
        HomeAdapter.ClearItems();
        //change nav icons color
        changeVectorColor(btnSearchFragment, R.color.deselected_nav_icon_color);
        changeVectorColor(btnCategoryFragment, R.color.selected_nav_icon_color);
        changeVectorColor(btnMainFragment, R.color.deselected_nav_icon_color);
        HelperModule.LoadFragment(R.id.main_placeholder, new CategoryFragment(), getSupportFragmentManager(), R.anim.zoom_enter, R.anim.zoom_exit);
        showActionBar(true);
        currentFragment = activeFragment.category;
    }

    private void LoadHomeFragment()
    {

        changeNavTextColor(txtSearchNav, "#BDBDBD");
        changeNavTextColor(txtMainNav, "#757575");
        changeNavTextColor(txtCategoryNav, "#BDBDBD");
        NewlyProductSpecialRecyclerAdapter.ClearItems();
        BestProductSpecialRecyclerAdapter.ClearItems();
        ProductAdapter.ClearItems();
        HomeAdapter.ClearItems();
        changeVectorColor(btnSearchFragment, R.color.deselected_nav_icon_color);
        changeVectorColor(btnCategoryFragment, R.color.deselected_nav_icon_color);
        changeVectorColor(btnMainFragment, R.color.selected_nav_icon_color);
        HelperModule.LoadFragment(R.id.main_placeholder, new HomeFragment(NewlyProductSpecialRecyclerAdapter, BestProductSpecialRecyclerAdapter, HomeAdapter), getSupportFragmentManager(), R.anim.zoom_enter, R.anim.zoom_exit);
        showActionBar(true);
        currentFragment = activeFragment.main;
    }

    private void UpdateApplication()
    {
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                CheckAppUpdates(BuildConfig.VERSION_NAME);
            }
        }, 2500);
    }

    private void LoadUserProfileName()
    {
        if (UserId != -1)
        {
            final RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .error(R.drawable.user_profile)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH);
            Call<ProfileInfoApiModel> CallProfile = requestApi.GetUserProfileImage(UserId);
            CallProfile.enqueue(new ApiCallBack<ProfileInfoApiModel>(LandActivity.this, CallProfile)
            {
                @Override
                public void onResponse(Call<ProfileInfoApiModel> call, Response<ProfileInfoApiModel> response)
                {
                    txt_greetings_land.setText(" سلام " + response.body().Fname + " عزیز ☺ ");
                    Glide.with(LandActivity.this).load(getResources().getString(R.string.UsersImageFolder) + response.body().ImageName).apply(options).into(profile_image);
                }

                @Override
                public void onFailure(@NonNull Call<ProfileInfoApiModel> call, @NonNull Throwable t)
                {
                    txt_greetings_land.setText("کاربر عزیز خوش اومدی");
                    Glide.with(LandActivity.this).load(R.drawable.user_profile).into(profile_image);
                }
            });
        }
        else
        {
            txt_greetings_land.setText("کاربرمهمان خوش اومدی");
            Glide.with(LandActivity.this).load(R.drawable.user_profile).into(profile_image);
        }
    }

    private void CheckAppUpdates(final String VersionName)
    {
        final AnimationSet mModalInAnim = (AnimationSet) OptAnimationLoader.loadAnimation(this, cn.pedant.SweetAlert.R.anim.modal_in);
        final AnimationSet mModalOutAnim = (AnimationSet) OptAnimationLoader.loadAnimation(this, cn.pedant.SweetAlert.R.anim.modal_out);
        Call<ApplicationUpdateModel> UpdateCall = requestApi.CheckUpdates();
        UpdateCall.enqueue(new ApiCallBack<ApplicationUpdateModel>(this, UpdateCall)
        {
            @Override
            public void onResponse(Call<ApplicationUpdateModel> call, final Response<ApplicationUpdateModel> response)
            {
                if (response.body() != null)
                {
                    if (!(response.body().VersionName.equals(VersionName)))
                    {
                        //region--------------------------UpdateApp
                        String AlertMessage = "";
                        android.support.v7.app.AlertDialog.Builder UpdateAppDialog = new android.support.v7.app.AlertDialog.Builder(LandActivity.this);
                        UpdateAppDialog.setCancelable(false);
                        View AlertView = View.inflate(LandActivity.this, R.layout.update_dlg, null);
                        TextView NotifyDialogTitle = AlertView.findViewById(R.id.txt_dialog_title_upd);
                        TextView NotifyDialogMessage = AlertView.findViewById(R.id.txt_dialog_message_upd);
                        if (response.body().ForceUpdate)
                        {
                            NotifyDialogTitle.setText("نسخه جدید ملایر مارکت رسید ! (آپدیت اجباری) ");
                        }
                        else
                        {
                            NotifyDialogTitle.setText("نسخه جدید ملایر مارکت رسید !");
                        }
                        AlertMessage = "سلام سلام نسخه جدید برنامه مون رو دانلود کن دوست من.☺ ☺ ☺";
                        AlertMessage += "\n";
                        AlertMessage += "\n";
                        AlertMessage += "شماره نسخه : " + response.body().VersionName;
                        AlertMessage += "\n\n";
                        AlertMessage += "{ تغییرات جدید } ";
                        AlertMessage += "\n\n";
                        //----------------------------
                        String[] EditInfoParts = response.body().Info;
                        for (int i = 0; i < EditInfoParts.length; ++i)
                        {
                            AlertMessage += EditInfoParts[i];
                            if (i != EditInfoParts.length - 1)
                            {
                                AlertMessage += "\n\n";
                            }
                        }
                        //----------------------------
                        NotifyDialogMessage.setText(AlertMessage);
                        UpdateAppDialog.setView(AlertView);
                        UpdateAppDialog.setPositiveButton("آپدیت میکنم", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                UpdateDL.dismiss();
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(response.body().AppUrl));
                                Intent browserChooserIntent = Intent.createChooser(browserIntent, "باز کردن با");
                                startActivity(browserChooserIntent);
                                finish();
                                //System.exit(0);
                            }
                        });
                        if (!response.body().ForceUpdate)
                        {
                            UpdateAppDialog.setNegativeButton("الان نه !", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    UpdateDL.dismiss();
                                }
                            });
                        }
                        UpdateDL = UpdateAppDialog.create();
                        UpdateDL.setOnDismissListener(new DialogInterface.OnDismissListener()
                        {
                            @Override
                            public void onDismiss(DialogInterface dialog)
                            {
                                ((ViewGroup) UpdateDL.getWindow().getDecorView()).getChildAt(0).startAnimation(mModalOutAnim);
                            }
                        });
                        ((ViewGroup) UpdateDL.getWindow().getDecorView()).getChildAt(0).startAnimation(mModalInAnim);
                        UpdateDL.getWindow().getDecorView().getBackground().setAlpha(255);
                        UpdateDL.show();
                        //endregion--------------------
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApplicationUpdateModel> call, @NonNull Throwable t)
            {
                super.onFailure(call, t);
            }
        });

    }

    private class ShareAppAsync extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            LoadingDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            HelperModule.ShareApplication(LandActivity.this);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            LoadingDialog.dismiss();
        }
    }

    private void changeVectorColor(ImageView imageView, int colorId)
    {
        imageView.setColorFilter(ContextCompat.getColor(this,
                colorId), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    private void changeNavTextColor(TextView textView, String color)
    {
        textView.setTextColor(Color.parseColor(color));
    }

    private void showActionBar(boolean show)
    {
        try
        {
            if (show)
            {
                if (!getSupportActionBar().isShowing())
                {
                    getSupportActionBar().show();
                }
            }
            else
            {
                getSupportActionBar().hide();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void GetShoppingCartCount(int Count)
    {
        if (Count > 0)
        {
            txt_cart_counter.setText(Count + "");
            if (CartBadgeTimer == null)
            {
                CartBadgeTimer = new Timer();

                CartBadgeTimer.scheduleAtFixedRate(new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                YoYo.with(Techniques.Shake).interpolate(new LinearInterpolator()).duration(1000).playOn(btn_show_shopping_cart);
                                YoYo.with(Techniques.Shake).interpolate(new LinearInterpolator()).duration(1000).playOn(txt_cart_counter);
                            }
                        });

                    }
                }, 0, 5000);
            }
        }
        else if (CartBadgeTimer != null)
        {
            txt_cart_counter.setText("");
            CartBadgeTimer.cancel();
            CartBadgeTimer = null;
        }
    }

    private enum activeFragment
    {
        search,
        main,
        category
    }
}
