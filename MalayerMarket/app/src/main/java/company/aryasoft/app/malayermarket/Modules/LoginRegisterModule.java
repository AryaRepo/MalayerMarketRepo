package company.aryasoft.app.malayermarket.Modules;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;
import company.aryasoft.app.malayermarket.Activities.LandActivity;
import company.aryasoft.app.malayermarket.ApiModels.ActiveAccountApiModel;
import company.aryasoft.app.malayermarket.ApiModels.RecoverPasswordApiModel;
import company.aryasoft.app.malayermarket.ApiModels.RegisterAccountApiModel;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ApiCallBack;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ApiServiceGenerator;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ServiceRequestApi;
import company.aryasoft.app.malayermarket.R;
import company.aryasoft.app.malayermarket.UtilityAndHelper.HelperModule;
import company.aryasoft.app.malayermarket.UtilityAndHelper.SharedPreferencesHelper;
import retrofit2.Call;
import retrofit2.Response;

public class LoginRegisterModule
{
    public interface OnActiveUserListener
    {
        void OnActivated();
    }

    private Context context;
    private SweetAlertDialog LoadingDialog;
    private String MobileNumber, Password;
    private ServiceRequestApi requestApi;
    private Timer SchedulerTask;
    private String StartMin;
    private String StartSec;
    private String ResendTimeText = "ارسال دوباره تا ";
    private OnActiveUserListener onActiveUserListener;
    private Dialog ValidationDialog;

    public void setOnActiveUserListener(OnActiveUserListener onActiveUserListener)
    {
        this.onActiveUserListener = onActiveUserListener;
    }

    public LoginRegisterModule(Context context)
    {
        LoadingDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        LoadingDialog.setTitleText("لطفا کمی صبر کنید...");
        LoadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        LoadingDialog.setCancelable(false);
        requestApi = ApiServiceGenerator.GetApiService();
        this.context = context;
    }

    public void LoginUser(String UserName, String UserPassword)
    {
        if (!LoadingDialog.isShowing())
        {
            LoadingDialog.show();
        }
        this.MobileNumber = HelperModule.arabicToDecimal(UserName);
        this.Password = HelperModule.arabicToDecimal(UserPassword);
        Call<Integer> LoginCall = requestApi.Login(this.MobileNumber, this.Password);
        LoginCall.enqueue(new ApiCallBack<Integer>(context, LoginCall)
        {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response)
            {
                LoadingDialog.dismiss();
                //-----------------------
                if (response.body() != -1 && response.body() != -2)
                {
                    SharedPreferencesHelper.WriteInt("UserState", 3);
                    SharedPreferencesHelper.WriteInt("UserId", response.body());
                    SharedPreferencesHelper.WriteString("MobileNumber", MobileNumber);
                    SharedPreferencesHelper.WriteString("CurrentPassword", Password);
                    //---------------------
                    ShowWelcomeDialog();
                }
                else if (response.body() == -1)
                {
                    SharedPreferencesHelper.WriteInt("UserState", 1);
                    SharedPreferencesHelper.WriteString("MobileNumber", MobileNumber);
                    SharedPreferencesHelper.WriteString("CurrentPassword", Password);
                    ShowActiveAccountDialog();
                    //--------------------
                }
                else if (response.body() == -2)
                {
                    SharedPreferencesHelper.WriteInt("UserState", -1);
                    HelperModule.OpenCommonDialog(context, "کاربر یافت نشد", "کاربر گرامی چنین کاربری در سیستم موجود نیست.(شما باید در سیستم ثبت نام شوید)", "باشه");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t)
            {
                LoadingDialog.dismiss();
                super.onFailure(call, t);
            }
        });
    }

    private void ShowActiveAccountDialog()
    {
        android.support.v7.app.AlertDialog.Builder NotifyDialogAlert = new android.support.v7.app.AlertDialog.Builder(context);
        NotifyDialogAlert.setCancelable(false);
        View AlertView = View.inflate(context, R.layout.dialog_notify, null);
        TextView NotifyDialogTitle = AlertView.findViewById(R.id.txt_dialog_title);
        TextView NotifyDialogMessage = AlertView.findViewById(R.id.txt_dialog_message);
        NotifyDialogTitle.setText("فعال سازی حساب");
        NotifyDialogMessage.setText("کاربر عزیز حساب کاربری تون هنوز فعال نشده.یک پیامک حاوی کدفعال سازی ارسال میشه.");
        NotifyDialogAlert.setView(AlertView);
        NotifyDialogAlert.setPositiveButton("ممنون،فهمیدم", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                //call API
                SendActiveCode();
            }
        });
        //--------------------
        NotifyDialogAlert.show();
    }

    private void SendActiveCode()
    {
        LoadingDialog.show();
        Call<Integer> RenewActiveCode = requestApi.RenewActiveCode(SharedPreferencesHelper.ReadString("MobileNumber"));
        RenewActiveCode.enqueue(
                new ApiCallBack<Integer>(context, RenewActiveCode)
                {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response)
                    {
                        SharedPreferencesHelper.WriteInt("ActiveCode", response.body());
                        LoadingDialog.dismiss();
                        ShowValidationDialog();

//                        if (ValidationDialog != null)
//                        {
//                            if (!ValidationDialog.isShowing())
//                            {
//                                ShowValidationDialog();
//                            }
//                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t)
                    {
                        LoadingDialog.dismiss();
                        super.onFailure(call, t);
                    }
                }
        );
    }

    public void RetrievePassword(RecoverPasswordApiModel RecoverPasswordObject)
    {
        if (!LoadingDialog.isShowing())
        {
            LoadingDialog.show();
        }
        Call<Integer> RecoveryPassword = requestApi.RecoverPassword(RecoverPasswordObject);
        RecoveryPassword.enqueue(new ApiCallBack<Integer>(context, RecoveryPassword)
        {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response)
            {
                if (LoadingDialog.isShowing())
                {
                    LoadingDialog.dismiss();
                }
                if (response.body() == 2)
                {
                    //user is not active
                    HelperModule.OpenCommonDialog(context, "فعال نبودن حساب", "کاربر عزیز حساب کاربری شما فعال نیست.", "باشه");
                }
                else if (response.body() == 3)
                {
                    //no user
                    HelperModule.OpenCommonDialog(context, "کاربری یافت نشد", "چنین کاربری با این شماره موبایل یافت نشد", "باشه");
                }
                else
                {
                    HelperModule.OpenCommonDialog(context, "بازیابی رمز عبور", "کاربر عزیز رمز عبور جدیدی در سیستم برای شما ایجاد شد.که از طریق پیامک برای شما ارسال می شود.", "باشه");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t)
            {
                LoadingDialog.dismiss();
                super.onFailure(call, t);
            }
        });
    }

    public void RegisterUser(RegisterAccountApiModel UserRegister)
    {
        LoadingDialog.show();
        this.MobileNumber = UserRegister.MobileNumber;
        Call<Integer> Register = requestApi.Register(UserRegister);
        Register.enqueue(new ApiCallBack<Integer>(context, Register)
        {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response)
            {
                LoadingDialog.dismiss();
                if (response.body() == 0)
                {
                    //he is active
                    SharedPreferencesHelper.WriteInt("UserState", 2);
                    HelperModule.OpenCommonDialog(context, "ثبت نام", "کاربر گرامی شما کاربرفعال این برنامه هستید. و نیاز به ثبت نام ندارید\n از قسمت ورود میتوانید وارد حساب کاربری تان شوید.", "باشه،فهمیدم");

                }
                else if (response.body() == 1)
                {
                    //he is not active
                    SharedPreferencesHelper.WriteInt("UserState", 1);
                    SharedPreferencesHelper.WriteString("MobileNumber", MobileNumber);
                    ShowActiveAccountDialog();
                    //--------------------
                }
                else if (response.body() == -1)
                {
                    //exception
                    HelperModule.OpenCommonDialog(context, "خطای سیستم", "کاربر عزیز در هنگام ثبت نام شما خطایی رخ داده است.", "باشه،فهمیدم");
                }
                else if (response.body() != 0 && response.body() != 1 && response.body() != -1)
                {
                    SharedPreferencesHelper.WriteInt("UserState", 1);
                    //succecfull sign up
                    //lock sigup
                    //get active code
                    android.support.v7.app.AlertDialog.Builder NotifyDialogAlert = new android.support.v7.app.AlertDialog.Builder(context);
                    NotifyDialogAlert.setCancelable(false);
                    View AlertView = View.inflate(context, R.layout.dialog_notify, null);
                    TextView NotifyDialogTitle = AlertView.findViewById(R.id.txt_dialog_title);
                    TextView NotifyDialogMessage = AlertView.findViewById(R.id.txt_dialog_message);
                    NotifyDialogTitle.setText("خوش آمدید");
                    NotifyDialogMessage.setText("کاربر عزیز ثبت نام شما با موفقیت انجام گردید.\n تا چند لحظه ی دیگر کد فعال سازی برای شما پیامک خواهد شد.\n با استفاده از آن کدمیتوانید حساب کاربری تان را فعال کنید");
                    NotifyDialogAlert.setView(AlertView);
                    NotifyDialogAlert.setPositiveButton("باشه،فهمیدم", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                            ShowValidationDialog();
                        }
                    });
                    //--------------------
                    NotifyDialogAlert.show();
                    SharedPreferencesHelper.WriteInt("ActiveCode", response.body());

                }
            }

            @Override
            public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t)
            {
                LoadingDialog.dismiss();
                super.onFailure(call, t);
            }
        });
    }

    private void ShowValidationDialog()
    {
        android.support.v7.app.AlertDialog.Builder ValidationAlert = new android.support.v7.app.AlertDialog.Builder(context);
        ValidationAlert.setCancelable(false);
        View AlertView = View.inflate(context, R.layout.dialog_validation, null);
        final EditText edt_validationCode = AlertView.findViewById(R.id.edt_validationcode_validation);
        ImageButton btn_checkValid = AlertView.findViewById(R.id.btn_checkvalid_validation);
        final ImageButton btn_send_again = AlertView.findViewById(R.id.btn_send_again_validation);
        final TextView txt_validation_timer = AlertView.findViewById(R.id.txt_validation_timer);
        ValidationAlert.setView(AlertView);
        ValidationDialog = ValidationAlert.create();
        btn_send_again.setEnabled(false);
        btn_send_again.setBackgroundColor(Color.parseColor("#E57373"));
        RefreshTimeCounter(txt_validation_timer, btn_send_again);
        btn_checkValid.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!(HelperModule.IsEditTextEmpty(edt_validationCode)))
                {
                    ActiveUser(HelperModule.arabicToDecimal(edt_validationCode.getText().toString()));
                }
                else
                {
                    Toast.makeText(context, "کد را وارد کنید.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_send_again.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (btn_send_again.isEnabled())
                {
                    SendActiveCode();
                    txt_validation_timer.setVisibility(View.VISIBLE);
                    btn_send_again.setBackgroundColor(Color.parseColor("#E57373"));
                    RefreshTimeCounter(txt_validation_timer, btn_send_again);
                    btn_send_again.setEnabled(false);
                }
            }
        });
        ValidationDialog = ValidationAlert.show();
    }

    private void RefreshTimeCounter(final TextView txt_validation_timer, final ImageButton btn_send_again)
    {
        if (SchedulerTask != null)
        {
            SchedulerTask.cancel();
            SchedulerTask = null;
        }
        StartMin = "01";
        StartSec = "59";
        txt_validation_timer.setText(ResendTimeText + StartSec + " : " + StartMin);
        SchedulerTask = new Timer();
        SchedulerTask.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                ((AppCompatActivity) context).runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (Integer.parseInt(StartSec) - 1 >= 0)
                        {
                            int s = Integer.parseInt(StartSec);
                            --s;
                            StartSec = s + "";
                            txt_validation_timer.setText(ResendTimeText + StartSec + " : " + StartMin);
                            if (s == 0)
                            {
                                if (Integer.parseInt(StartMin) - 1 >= 0)
                                {
                                    int m = Integer.parseInt(StartMin);
                                    --m;
                                    StartMin = m + "";
                                    txt_validation_timer.setText(ResendTimeText + StartSec + " : " + StartMin);
                                    StartSec = "59";
                                }
                            }
                        }
                        if (StartSec.equals("0") && StartMin.equals("0"))
                        {
                            btn_send_again.setEnabled(true);
                            btn_send_again.setBackgroundColor(Color.parseColor("#FE1743"));
                            txt_validation_timer.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        }, 0, 1000);
    }

    private void ShowWelcomeDialog()
    {
        android.support.v7.app.AlertDialog.Builder NotifyDialogAlert = new android.support.v7.app.AlertDialog.Builder(context);
        NotifyDialogAlert.setCancelable(false);
        View AlertView = View.inflate(context, R.layout.dialog_notify, null);
        TextView NotifyDialogTitle = AlertView.findViewById(R.id.txt_dialog_title);
        TextView NotifyDialogMessage = AlertView.findViewById(R.id.txt_dialog_message);
        NotifyDialogTitle.setText("به ملایر مارکت خوش اومدی ☺");
        NotifyDialogMessage.setText("کاربر عزیز به فروشگاه ملایر مارکت خوش اومدی.\n از حالا به بعد میتونی خرید کنی.");
        NotifyDialogAlert.setView(AlertView);
        NotifyDialogAlert.setPositiveButton("ممنون،فهمیدم", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                //----------
                ((AppCompatActivity) context).finish();
                context.startActivity(new Intent(context, LandActivity.class));
            }
        });
        //--------------------
        NotifyDialogAlert.show();
    }

    private void ActiveUser(String ActiveCode)
    {
        if (SharedPreferencesHelper.ReadInt("ActiveCode") == Integer.parseInt(ActiveCode))
        {
            //  Toast.makeText(context, MobileNumber+"\n"+ActiveCode+"\n"+SharedPreferencesHelper.ReadInt("ActiveCode"), Toast.LENGTH_SHORT).show();
            ActiveAccountApiModel activeAccount = new ActiveAccountApiModel();
            activeAccount.ActiveCode = ActiveCode;
            activeAccount.MobileNumber = MobileNumber;
            Call<Boolean> ActiveUser = requestApi.ActiveUser(activeAccount);
            LoadingDialog.show();
            ActiveUser.enqueue(new ApiCallBack<Boolean>(context, ActiveUser)
            {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response)
                {
                    if (response.body())
                    {
                        ValidationDialog.dismiss();
                        onActiveUserListener.OnActivated();
                    }
                    else if (!response.body())
                    {
                        Toast.makeText(context, "خطا در فعال سازی", Toast.LENGTH_SHORT).show();
                    }
                    LoadingDialog.dismiss();
                }

                @Override
                public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t)
                {
                    ValidationDialog.dismiss();
                    LoadingDialog.dismiss();
                    super.onFailure(call, t);
                }
            });
        }
        else
        {
            Toast.makeText(context, "کد وارد شده صحیح نیست", Toast.LENGTH_LONG).show();
        }
    }
}
