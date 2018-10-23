package company.aryasoft.app.malayermarket.UtilityAndHelper;

import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.Paint;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import company.aryasoft.app.malayermarket.BuildConfig;
import company.aryasoft.app.malayermarket.R;

public class HelperModule
{
    private static Dialog DCDlg = null;
    public static boolean IsShowDialogLoading = false;

    public static void OpenCommonDialog(Context AppContext, String Title, String Message, String PositiveButtonText)
    {
        android.support.v7.app.AlertDialog.Builder NotifyDialogAlert = new android.support.v7.app.AlertDialog.Builder(AppContext);
        NotifyDialogAlert.setCancelable(false);
        View AlertView = View.inflate(AppContext, R.layout.dialog_notify, null);
        TextView NotifyDialogTitle = AlertView.findViewById(R.id.txt_dialog_title);
        TextView NotifyDialogMessage = AlertView.findViewById(R.id.txt_dialog_message);
        NotifyDialogTitle.setText(Title);
        NotifyDialogMessage.setText(Message);
        NotifyDialogAlert.setView(AlertView);
        NotifyDialogAlert.setPositiveButton(PositiveButtonText, new DialogInterface.OnClickListener()
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

    public static boolean EmailValidation(String EmailText)
    {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";//REGEX PATTERN FOR EMAIL
        return EmailText.matches(emailPattern);
    }

    public static void NoDataReceivedDialog(Context AppContext)
    {
        android.support.v7.app.AlertDialog.Builder NotifyDialogAlert = new android.support.v7.app.AlertDialog.Builder(AppContext);
        NotifyDialogAlert.setCancelable(false);
        View AlertView = View.inflate(AppContext, R.layout.dialog_notify, null);
        TextView NotifyDialogTitle = AlertView.findViewById(R.id.txt_dialog_title);
        TextView NotifyDialogMessage = AlertView.findViewById(R.id.txt_dialog_message);
        NotifyDialogTitle.setText("خطا در دریافت اطلاعات");
        NotifyDialogMessage.setText("کاربرگرامی باوجود اینکه دستگاه شما به اینترنت وصل است اما داده ای دریافت نشد.\n\nممکن است مشکل از ارتباط اینترنتی یا شبکه ای باشد.");
        NotifyDialogAlert.setView(AlertView);
        NotifyDialogAlert.setNegativeButton("باشه،فهمیدم!", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                System.exit(1);
            }
        });
        //--------------------
        NotifyDialogAlert.show();
    }

    public static void InternetDisconnectedDialog(final Context AppContext)
    {
        if (DCDlg == null)
        {

            android.support.v7.app.AlertDialog.Builder InternetAlert = new android.support.v7.app.AlertDialog.Builder(AppContext);
            DCDlg = InternetAlert.create();
            InternetAlert.setCancelable(false);
            View AlertView = View.inflate(AppContext, R.layout.internet_dialog, null);
            Button Internet_btn_WIFI = AlertView.findViewById(R.id.Internet_btn_WIFI);
            Button Internet_btn_Mobile = AlertView.findViewById(R.id.Internet_btn_Mobile);
            InternetAlert.setView(AlertView);
            Internet_btn_Mobile.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent DATAIntent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
                    AppContext.startActivity(DATAIntent);
                    DCDlg.dismiss();
                    System.exit(1);
                }
            });
            Internet_btn_WIFI.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent WIFIIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    AppContext.startActivity(WIFIIntent);
                    DCDlg.dismiss();
                    System.exit(1);
                }
            });
            InternetAlert.setOnKeyListener(new DialogInterface.OnKeyListener()
            {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
                {
                    if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
                    {
                        dialog.dismiss();
                        ShowExitAppDialog(AppContext);
                    }
                    return false;
                }
            });
            DCDlg = InternetAlert.show();
        }
        //--------------------

    }

    public static void ShowExitAppDialog(final Context AppContext)
    {
        android.support.v7.app.AlertDialog.Builder ExitAccountAlert = new android.support.v7.app.AlertDialog.Builder(AppContext);
        ExitAccountAlert.setCancelable(false);
        View AlertView = View.inflate(AppContext, R.layout.dialog_notify, null);
        TextView NotifyDialogTitle = AlertView.findViewById(R.id.txt_dialog_message);
        TextView txt_dialog_title = AlertView.findViewById(R.id.txt_dialog_title);
        txt_dialog_title.setText("خروج از برنامه");
        NotifyDialogTitle.setText("دوست من  میخوای از برنامه خارج شی؟");
        ExitAccountAlert.setView(AlertView);
        ExitAccountAlert.setPositiveButton("بله", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                System.exit(1);
            }
        });
        ExitAccountAlert.setNegativeButton("خیر", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        ExitAccountAlert.show();
    }

    public static byte[] ConvertImageToByteArray(Intent YourData, Context context)
    {
        final long KiB = 1024;
        String Uri_Path = YourData.getDataString();
        android.database.Cursor cursor = context.getContentResolver().query(android.net.Uri.parse(Uri_Path), null, null, null, null);
        int idx = -1;
        if (cursor != null)
        {
            cursor.moveToFirst();
            idx = cursor.getColumnIndex(android.provider.MediaStore.Images.ImageColumns.DATA);
            String path = cursor.getString(idx);
            File f = new File(path);
            Bitmap bm = BitmapFactory.decodeFile(path);
            long len = (f.length() / KiB);
            cursor.close();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            if (len < 100)
            {
                bm.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            }
            else if (len > 100 && len < 500)
            {
                bm.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
            }
            else if (len > 500)
            {
                bm.compress(Bitmap.CompressFormat.JPEG, 15, byteArrayOutputStream);
            }
            return byteArrayOutputStream.toByteArray();
        }
        return null;
    }

    public static void ShowSignInAlert(final Context AppContext)
    {
        android.support.v7.app.AlertDialog.Builder SignInAlert = new android.support.v7.app.AlertDialog.Builder(AppContext);
        SignInAlert.setCancelable(false);
        View AlertView = View.inflate(AppContext, R.layout.dialog_notify, null);
        TextView NotifyDialogTitle = AlertView.findViewById(R.id.txt_dialog_message);
        NotifyDialogTitle.setText("کاربر گرامی شما کاربر مهمان هستید ، برای استفاده از امکانات برنامه باید ثبت نام کنید یا اینکه ورود به سیستم کنید.");
        SignInAlert.setView(AlertView);
        SignInAlert.setPositiveButton("متوجه شدم", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        SignInAlert.show();
    }

    public static String arabicToDecimal(String number)
    {
        char[] chars = new char[number.length()];
        for (int i = 0; i < number.length(); i++)
        {
            char ch = number.charAt(i);
            if (ch >= 0x0660 && ch <= 0x0669)
            {
                ch -= 0x0660 - '0';
            }
            else if (ch >= 0x06f0 && ch <= 0x06F9)
            {
                ch -= 0x06f0 - '0';
            }
            chars[i] = ch;
        }
        return new String(chars);
    }

    public static boolean IsEditTextEmpty(EditText... Views)
    {
        boolean Result = false;
        int viewCount = 0;
        for (EditText itemView : Views)
        {
            if (TextUtils.isEmpty(itemView.getText().toString()))
            {
                ++viewCount;
            }
        }
        Log.i("Views", Views.length + "");
        Log.i("myviews", viewCount + "");
        if (viewCount == Views.length)
        {
            Result = true;
        }
        return Result;
    }

    public static void LoadFragment(int MainPlaceHolderId, Fragment YourFragment, FragmentManager YourActivityTransaction, int InAnimationId, int OutAnimationId)
    {
        FragmentTransaction FT = YourActivityTransaction.beginTransaction();
        FT.setCustomAnimations(InAnimationId, OutAnimationId);//R.anim.fade_in,R.anim.fade_out
        FT.replace(MainPlaceHolderId, YourFragment);
        FT.commit();
    }

    public static void strikeThroughTextView(TextView view)
    {
        view.setPaintFlags(view.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    public static void hideSoftKey(Context context, View view)
    {
        view.clearFocus();
        InputMethodManager in = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void ShareApplication(Context AppContext)
    {
        ApplicationInfo app = AppContext.getApplicationInfo();
        String filePath = app.sourceDir;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        File originalApk = new File(filePath);
        try
        {
            File tempFile = new File(AppContext.getExternalCacheDir() + "/ExtractedApk");
            if (!tempFile.isDirectory())
            {
                if (!tempFile.mkdirs())
                {
                    return;
                }
            }
            tempFile = new File(tempFile.getPath() + "/" + AppContext.getString(app.labelRes).replace(" ", "").toLowerCase() + ".apk");
            if (!tempFile.exists())
            {
                if (!tempFile.createNewFile())
                {
                    return;
                }
            }
            InputStream in = new FileInputStream(originalApk);
            OutputStream out = new FileOutputStream(tempFile);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0)
            {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            //intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(tempFile));
            intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(AppContext, BuildConfig.APPLICATION_ID + ".provider",tempFile));
            AppContext.startActivity(Intent.createChooser(intent, "اشتراک گذاری ملایرمارکت با"));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void changeTabsFont(Context context, TabLayout tabLayout)
    {
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/iransans.ttf");
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++)
        {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++)
            {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView)
                {
                    ((TextView) tabViewChild).setTypeface(tf, Typeface.NORMAL);
                }
            }
        }
    }
}
