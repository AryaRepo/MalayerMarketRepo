package company.aryasoft.app.malayermarket.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;
import company.aryasoft.app.malayermarket.ApiModels.UserInfoApiModel;
import company.aryasoft.app.malayermarket.Modules.OnFailureDataLoadListener;
import company.aryasoft.app.malayermarket.Modules.ProfileManagementModule;
import company.aryasoft.app.malayermarket.R;
import company.aryasoft.app.malayermarket.UtilityAndHelper.HelperModule;
import company.aryasoft.app.malayermarket.UtilityAndHelper.SharedPreferencesHelper;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class ProfileActivity extends AppCompatActivity implements OnFailureDataLoadListener
{
    private LinearLayout btnEditProfile, Btn_Edit_Password;
    private CircleImageView profile_person_image;
    private ImageView img_bg_pro;
    private TextView user_profile_name;
    private TextView user_profile_subscription_code;
    private TextView txt_UserProfile_txtEmail;
    private TextView txt_UserProfile_address;
    private UserInfoApiModel UserInfo;
    private SweetAlertDialog LoadingDialog;
    private ProfileManagementModule profileManagementModule;
    private Dialog EditPasswordAlert;
    private int UserId = -1;
    private int UserState = 0;
    @Override
    protected void onRestart()
    {
        super.onRestart();
        ///-----------------------
        UserResidenceStatus();
    }

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(ProfileActivity.this, LandActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        UserId = SharedPreferencesHelper.ReadInt("UserId");
        profileManagementModule = new ProfileManagementModule(this);
        profileManagementModule.setOnFailureDataLoadListener(this);
        initViews();
        UserResidenceStatus();
        btnEditProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class).putExtra("UserState", UserState).putExtra("UserId", UserId).putExtra("UserInfo", new Gson().toJson(UserInfo)));
            }
        });
        Btn_Edit_Password.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                EditPassword();
            }
        });
    }

    private void initViews()
    {
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        Btn_Edit_Password = findViewById(R.id.btn_edit_password);
        profile_person_image = findViewById(R.id.profile_person_image);
        img_bg_pro = findViewById(R.id.img_bg_pro);
        user_profile_name = findViewById(R.id.user_profile_name);
        user_profile_subscription_code = findViewById(R.id.user_profile_subscription_code);
        txt_UserProfile_txtEmail = findViewById(R.id.txt_UserProfile_txtEmail);
        txt_UserProfile_address = findViewById(R.id.txt_UserProfile_address);
        LoadingDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        LoadingDialog.setTitleText(getString(R.string.loading_title));
        LoadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        LoadingDialog.setCancelable(false);
    }

    private void EditPassword()
    {
        final String MobileNumber = SharedPreferencesHelper.ReadString("MobileNumber");
        final String OldPassword = SharedPreferencesHelper.ReadString("CurrentPassword");
        android.support.v7.app.AlertDialog.Builder NewPasswordDialogAlert = new android.support.v7.app.AlertDialog.Builder(ProfileActivity.this);
        View AlertView = View.inflate(ProfileActivity.this, R.layout.change_password_layout_dialog, null);
        NewPasswordDialogAlert.setView(AlertView);
        final EditText edt_new_password = AlertView.findViewById(R.id.edt_new_password);
        final EditText edt_new_password_rpt = AlertView.findViewById(R.id.edt_new_password_rpt);
        final Button btn_change_password = AlertView.findViewById(R.id.btn_change_password);
        EditPasswordAlert = NewPasswordDialogAlert.create();
        EditPasswordAlert.show();
        //---------------
        btn_change_password.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                LoadingDialog.show();
                if (HelperModule.IsEditTextEmpty(edt_new_password, edt_new_password_rpt))
                {
                    LoadingDialog.dismiss();
                    Toast.makeText(ProfileActivity.this, "لطفا مقادیر خواسته شده را وارد کنید.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!(edt_new_password.getText().toString().equals(edt_new_password_rpt.getText().toString())))
                {
                    LoadingDialog.dismiss();
                    Toast.makeText(ProfileActivity.this, "مقادیر گذرواژه و تکرار گذرواژه همخوانی ندارد", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (edt_new_password.getText().toString().length() < 6 && edt_new_password_rpt.getText().toString().length() < 6)
                {
                    LoadingDialog.dismiss();
                    Toast.makeText(ProfileActivity.this, "حداقل میزان پسورد 6 حرف است", Toast.LENGTH_SHORT).show();
                    return;
                }
                profileManagementModule.EditAccountPassword(MobileNumber, OldPassword, edt_new_password.getText().toString());
                profileManagementModule.setOnPasswordChangedListener(new ProfileManagementModule.OnPasswordChangedListener()
                {
                    @Override
                    public void OnPasswordChanged(Boolean PasswordChangedState)
                    {
                        if (PasswordChangedState)
                        {
                            Toast.makeText(ProfileActivity.this, "گذرواژه شما تغییر کرد.", Toast.LENGTH_SHORT).show();
                            SharedPreferencesHelper.WriteString("CurrentPassword", edt_new_password.getText().toString());
                        }
                        else
                        {
                            Toast.makeText(ProfileActivity.this, "مشکلی در ویرایش گذرواژه رخ داده است.", Toast.LENGTH_SHORT).show();
                        }
                        LoadingDialog.dismiss();
                        EditPasswordAlert.dismiss();
                    }
                });

            }
        });
    }


    private void UserResidenceStatus()
    {
        LoadingDialog.show();
        profileManagementModule.UserResidenceStatus(UserId);
        profileManagementModule.setOnUserResidenceStatusListener(new ProfileManagementModule.OnUserResidenceStatusListener()
        {
            @Override
            public void OnUserResidenceStatus(Integer UserState)
            {
                ProfileActivity.this.UserState = UserState;
                if (ProfileActivity.this.UserState == -4)
                {
                    user_profile_name.setText("نام و فامیلی شما ثبت نشده");
                    user_profile_subscription_code.setText("اشتراکی در سیستم ندارید");
                    txt_UserProfile_txtEmail.setText("ایمیل ثبت نشده");
                    txt_UserProfile_address.setText("آدرس شما ثبت نشده");
                    Glide.with(ProfileActivity.this).load(R.drawable.profile_bg).apply(bitmapTransform(new BlurTransformation(15, 3))).into(img_bg_pro);
                    Glide.with(ProfileActivity.this).load(R.drawable.user_profile).into(profile_person_image);
                    LoadingDialog.dismiss();
                }
                else
                {
                    LoadProfileInfo();
                }
            }
        });
    }

    private void LoadProfileInfo()
    {
        final RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.user_profile)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);
        profileManagementModule.GetProfileInfo(UserId);
        profileManagementModule.setOnGetProfileInfoListener(new ProfileManagementModule.OnGetProfileInfoListener()
        {
            @Override
            public void OnGetProfileInfo(UserInfoApiModel ProfileUserInfo)
            {
                UserInfo = new UserInfoApiModel();
                UserInfo = ProfileUserInfo;
                //No Address No SubscriptionCode
                user_profile_name.setText(ProfileUserInfo.Fname.equals("") ? "نام شما ثبت نشده" : ProfileUserInfo.Fname + " " + (ProfileUserInfo.Lname.equals("") ? "نام خانوادگی شما ثبت نشده" : ProfileUserInfo.Lname));
                txt_UserProfile_txtEmail.setText(ProfileUserInfo.Email == null ? "ایمیل ثبت نشده" : ProfileUserInfo.Email);
                user_profile_subscription_code.setText(ProfileUserInfo.SubscriptionCode.equals("") ? "اشتراکی در سیستم ندارید" : "اشتراک شما   " + ProfileUserInfo.SubscriptionCode);
                txt_UserProfile_address.setText(ProfileUserInfo.UserAddress.equals("") ? "آدرس شما ثبت نشده" : ProfileUserInfo.UserAddress);
                Glide.with(ProfileActivity.this).load(getResources().getString(R.string.UsersImageFolder) + ProfileUserInfo.ImageName).apply(options).into(profile_person_image);
                Glide.with(ProfileActivity.this).load(getResources().getString(R.string.UsersImageFolder) + ProfileUserInfo.ImageName).apply(options).apply(bitmapTransform(new BlurTransformation(10, 3))).into(img_bg_pro);
                LoadingDialog.dismiss();
            }
        });
    }


    @Override
    public void OnFailureLoadData(Throwable t)
    {
        if (EditPasswordAlert != null && EditPasswordAlert.isShowing())
        {
            EditPasswordAlert.dismiss();
        }
        LoadingDialog.dismiss();
    }
}
