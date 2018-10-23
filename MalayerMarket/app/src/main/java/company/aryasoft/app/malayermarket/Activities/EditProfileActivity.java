package company.aryasoft.app.malayermarket.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import ir.hamsaa.persiandatepicker.Listener;
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.util.PersianCalendar;
import company.aryasoft.app.malayermarket.ApiModels.AddUserInfoApiModel;
import company.aryasoft.app.malayermarket.ApiModels.UserInfoApiModel;
import company.aryasoft.app.malayermarket.ApiModels.ZonesApiModel;
import company.aryasoft.app.malayermarket.Modules.OnFailureDataLoadListener;
import company.aryasoft.app.malayermarket.Modules.ProfileManagementModule;
import company.aryasoft.app.malayermarket.R;
import company.aryasoft.app.malayermarket.UtilityAndHelper.HelperModule;
import company.aryasoft.app.malayermarket.UtilityAndHelper.MiladiToShamsi;
import company.aryasoft.app.malayermarket.UtilityAndHelper.SharedPreferencesHelper;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class EditProfileActivity extends AppCompatActivity implements OnFailureDataLoadListener
{
    private static final int READ_EXTERNAL_STORAGE_CODE = 100;
    private CircleImageView img_edit_profile;
    private SweetAlertDialog LoadingDialog;
    private TextView txt_birthday;
    private Button btn_choose_profile_photo;
    private EditText edt_user_name_edit;
    private EditText edt_user_last_name_edit;
    private EditText edt_user_email;
    private EditText edt_phone_edit;
    private EditText edt_address_edit;
    private Button btn_save_changes;
    private Button btn_choose_birthday;
    private ir.hamsaa.RtlMaterialSpinner sp_zone_lists;
    private UserInfoApiModel UserInfo;
    private ArrayList<ZonesApiModel> ZoneData;
    private ArrayAdapter<String> ZoneAdapter;
    private String BirthDay = "";
    private int UserId = -1;
    private int UserState = 0;
    private byte[] Picture;

    private ProfileManagementModule profileManagementModule;

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == READ_EXTERNAL_STORAGE_CODE)
        {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                OpenGallery();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                if (data != null)
                {
                    try
                    {
                        new LoadImageDataTask(data).execute();
                    } catch (Exception ignored)
                    {
                    }
                }
            }
        }
    }

    @Override
    public void OnFailureLoadData(Throwable t)
    {
        if (LoadingDialog.isShowing())
        {
            LoadingDialog.dismiss();
        }
        Toast.makeText(this, "مشکل در ارتباط با سمت سرور پیش آمده است.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        edt_user_name_edit.requestFocus();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        profileManagementModule = new ProfileManagementModule(this);
        profileManagementModule.setOnFailureDataLoadListener(this);
        initPreferenceData();
        initViews();
        initEvents();
        LoadZone();
    }

    private void initViews()
    {
        img_edit_profile = findViewById(R.id.img_edit_profile);
        edt_user_email = findViewById(R.id.edt_user_email);
        btn_choose_birthday = findViewById(R.id.btn_choose_birthday);
        btn_choose_profile_photo = findViewById(R.id.btn_choose_profile_photo);
        edt_user_name_edit = findViewById(R.id.edt_user_name_edit);
        txt_birthday = findViewById(R.id.txt_birthday);
        edt_user_last_name_edit = findViewById(R.id.edt_user_last_name_edit);
        edt_phone_edit = findViewById(R.id.edt_phone_edit);
        edt_address_edit = findViewById(R.id.edt_address_edit);
        btn_save_changes = findViewById(R.id.btn_save_changes);
        sp_zone_lists = findViewById(R.id.sp_zone_lists);
        LoadingDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        img_edit_profile.requestFocus();
        LoadingDialog.setTitleText(getString(R.string.loading_title));
        LoadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        LoadingDialog.setCancelable(false);
        SetUserProfile();
    }

    private void ChooseBirthDate()
    {
        PersianDatePickerDialog picker = new PersianDatePickerDialog(EditProfileActivity.this)
                .setPositiveButtonString("باشه")
                .setNegativeButton("بیخیال")
                .setMinYear(1320)
                .setMaxYear(PersianDatePickerDialog.THIS_YEAR)
                .setActionTextColor(Color.GRAY)
                .setListener(new Listener()
                {
                    @Override
                    public void onDateSelected(PersianCalendar persianCalendar)
                    {
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        BirthDay = dateFormat.format(persianCalendar.getTime()) + "";
                        try
                        {
                            if (!BirthDay.isEmpty())
                            {
                                Date UserBirth = dateFormat.parse(BirthDay);
                                txt_birthday.setText(new MiladiToShamsi().getPersianDate(UserBirth) + "");
                            }

                        } catch (ParseException e)
                        {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onDismissed()
                    {
                        BirthDay = "";
                    }
                });

        picker.show();
    }

    private void initEvents()
    {
        btn_choose_birthday.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ChooseBirthDate();
            }
        });
        btn_choose_profile_photo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!(ActivityCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED))
                {
                    RequestGalleryPermission();
                }
                else
                {
                    OpenGallery();
                }
            }
        });
        btn_save_changes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditProfile();
            }
        });
    }

    private void OpenGallery()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "انتخاب عکس"), 1);
    }

    private void initPreferenceData()
    {
        UserId = getIntent().getIntExtra("UserId", 0);
        UserState = getIntent().getIntExtra("UserState", -6);
        UserInfo = new Gson().fromJson(getIntent().getStringExtra("UserInfo"), new TypeToken<UserInfoApiModel>()
        {
        }.getType());
        if (UserInfo == null)
        {
            UserInfo = new UserInfoApiModel();
        }
    }


    private void LoadZone()
    {
        profileManagementModule.GetCityZones();
        profileManagementModule.setOnGetCityZonesListener(new ProfileManagementModule.OnGetCityZonesListener()
        {
            @Override
            public void OnGetCityZones(ArrayList<ZonesApiModel> ZoneData, String[] ZoneTitle)
            {
                EditProfileActivity.this.ZoneData = ZoneData;
                ZoneAdapter = new ArrayAdapter<>(EditProfileActivity.this, R.layout.my_spinner_item, ZoneTitle);
                ZoneAdapter.setDropDownViewResource(R.layout.my_spinner_item);
                sp_zone_lists.setAdapter(ZoneAdapter);
                try
                {
                    for (int i = 0; i < ZoneData.size(); ++i)
                    {
                        if (ZoneData.get(i).zoneid == UserInfo.ZoneCode)
                        {
                            sp_zone_lists.setSelection(i);
                            break;
                        }
                    }
                } catch (Exception exp)
                {
                    sp_zone_lists.setSelection(0);
                }
            }
        });
    }

    private void EditProfile()
    {
        if (HelperModule.IsEditTextEmpty(edt_user_name_edit, edt_user_last_name_edit, edt_phone_edit, edt_address_edit))
        {
            Toast.makeText(this, "کاربر گرامی لطفا تمامی موارد خواسته شده را پر کنید.", Toast.LENGTH_LONG).show();
            return;
        }
        if (HelperModule.IsEditTextEmpty(edt_user_name_edit))
        {
            Toast.makeText(this, "کاربر گرامی لطفا نام خود را وارد کنید.", Toast.LENGTH_LONG).show();
            return;
        }
        if (HelperModule.IsEditTextEmpty(edt_user_last_name_edit))
        {
            Toast.makeText(this, "کاربر گرامی لطفا نام خانوادگی خود را وارد کنید.", Toast.LENGTH_LONG).show();
            return;
        }
        if (HelperModule.IsEditTextEmpty(edt_phone_edit))
        {
            Toast.makeText(this, "کاربر گرامی لطفا شماره تلفن خود را وارد کنید.", Toast.LENGTH_LONG).show();
            return;
        }
        if (HelperModule.IsEditTextEmpty(edt_address_edit))
        {
            Toast.makeText(this, "کاربر گرامی لطفا آدرس خود را وارد کنید.", Toast.LENGTH_LONG).show();
            return;
        }
        if (BirthDay.equals(""))
        {
            Toast.makeText(this, "کاربر گرامی لطفا تاریخ تولدتان را انتخاب نمایید.", Toast.LENGTH_LONG).show();
            return;
        }
        if (sp_zone_lists.getSelectedItemPosition() == 0)
        {
            Toast.makeText(this, "کاربر گرامی لطفا منطقه خود را انتخاب کنید.", Toast.LENGTH_LONG).show();
            return;
        }
        if (!HelperModule.IsEditTextEmpty(edt_user_email))
        {
            if (!HelperModule.EmailValidation(edt_user_email.getText().toString()))
            {
                Toast.makeText(EditProfileActivity.this, "کاربر گرامی ایمیل وارد شده در قالب صحیح ایمیل وارد نشده.", Toast.LENGTH_SHORT).show();
                return;
            }
        }


        AddUserInfoApiModel ObjAddInfo = new AddUserInfoApiModel();
        //---------------------------------------------------------------
        ObjAddInfo.Fname = edt_user_name_edit.getText().toString();
        ObjAddInfo.Lname = edt_user_last_name_edit.getText().toString();
        if (UserInfo.ImageName.isEmpty())
        {
            if (Picture == null)
            {
                ObjAddInfo.ImageName = "nophoto.png";
            }
            else
            {
                ObjAddInfo.ImageName = Base64.encodeToString(Picture, Base64.DEFAULT);
            }
        }
        else
        {
            if (Picture == null)
            {
                ObjAddInfo.ImageName = "old-photo";
            }
            else
            {
                ObjAddInfo.ImageName = Base64.encodeToString(Picture, Base64.DEFAULT);
            }
        }
        ///-------------------------------------------------------------------------
        ObjAddInfo.PhoneNumber = HelperModule.arabicToDecimal(edt_phone_edit.getText().toString());
        ObjAddInfo.UserID = UserId;
        ObjAddInfo.StateCode = 30;
        ObjAddInfo.CityCode = 419;
        ObjAddInfo.Email = edt_user_email.getText().toString();
        ObjAddInfo.ZoneCode = ZoneData.get(sp_zone_lists.getSelectedItemPosition()).zoneid;
        ObjAddInfo.Latitude = "0";
        ObjAddInfo.Longitude = "0";
        ObjAddInfo.BirthDate = HelperModule.arabicToDecimal(BirthDay);
        ObjAddInfo.UserAddress = edt_address_edit.getText().toString();
        LoadingDialog.show();
        profileManagementModule.EditProfile(ObjAddInfo);

        profileManagementModule.setOnEditProfileListener(new ProfileManagementModule.OnEditProfileListener()
        {
            @Override
            public void OnEditProfile(Boolean OnEditProfileState)
            {
                if (OnEditProfileState)
                {
                    if (UserState != 0)
                    {
                        CreateSubscriptionCode(SharedPreferencesHelper.ReadInt("UserId"), ZoneData.get(sp_zone_lists.getSelectedItemPosition()).zoneid);
                    }
                    else
                    {
                        Toast.makeText(EditProfileActivity.this, "ویرایش پروفایل با موفقیت انجام شد.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
                else
                {
                    Toast.makeText(EditProfileActivity.this, "ویرایش اطلاعات با مشکل مواجه شد", Toast.LENGTH_LONG).show();
                }
                if (LoadingDialog.isShowing())
                {
                    LoadingDialog.dismiss();
                }
            }
        });
    }

    private void SetUserProfile()
    {
        if (UserInfo == null)
        {
            UserInfo = new UserInfoApiModel();
        }
        if (UserInfo.ImageName.isEmpty())
        {
            Glide.with(this).load(R.drawable.user_profile).into(img_edit_profile);
        }
        else if (UserInfo.ImageName.equals("nophoto.png"))
        {
            Picture = null;
        }
        else
        {
            Glide.with(this).load(getResources().getString(R.string.UsersImageFolder) + UserInfo.ImageName).into(img_edit_profile);
        }
        edt_user_name_edit.setText(UserInfo.Fname);
        edt_user_last_name_edit.setText(UserInfo.Lname);
        edt_phone_edit.setText(UserInfo.PhoneNumber);
        if (UserInfo.Email.equals("noemail@noemail.com"))
        {
            edt_user_email.setHint("ایمیلتون رو ثبت کنید");
            edt_user_email.setHintTextColor(Color.RED);
        }
        else
        {
            edt_user_email.setText(UserInfo.Email);
        }
        edt_address_edit.setText(UserInfo.UserAddress);
        BirthDay = UserInfo.BirthDate;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try
        {
            if (!BirthDay.isEmpty())
            {
                Date UserBirth = dateFormat.parse(BirthDay);
                txt_birthday.setText(new MiladiToShamsi().getPersianDate(UserBirth) + "");
            }

        } catch (ParseException e)
        {
            e.printStackTrace();
        }
    }

    private void CreateSubscriptionCode(int UserId, int ZoneCode)
    {
        profileManagementModule.CreateSubscriptionForUser(UserId, ZoneCode);
        profileManagementModule.setOnSubscriptionCodeCreatedListener(new ProfileManagementModule.OnSubscriptionCodeCreatedListener()
        {
            @Override
            public void OnSubscriptionCodeCreated(Boolean SubscriptionCodeCreatedState)
            {
                if (SubscriptionCodeCreatedState)
                {
                    //SubscriptionCodeCreated Successfully
                    Toast.makeText(EditProfileActivity.this, "ویرایش اطلاعات با موفقیت انجام شد", Toast.LENGTH_LONG).show();
                    LoadingDialog.dismiss();
                    finish();
                }
            }
        });
    }

    private void RequestGalleryPermission()
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE))
        {
            ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_CODE);
        }
        else
        {
            ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_CODE);
        }
    }


    private class LoadImageDataTask extends AsyncTask<Void, Void, Void>
    {
        private Intent imageIntentData;

        LoadImageDataTask(Intent imageIntentData)
        {
            this.imageIntentData = imageIntentData;
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            Picture = HelperModule.ConvertImageToByteArray(imageIntentData, EditProfileActivity.this);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            Glide.with(EditProfileActivity.this).load(imageIntentData.getData()).into(img_edit_profile);
        }
    }
}
