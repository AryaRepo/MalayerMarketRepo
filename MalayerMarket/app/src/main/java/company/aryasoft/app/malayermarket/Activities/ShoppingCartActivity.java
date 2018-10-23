package company.aryasoft.app.malayermarket.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zarinpal.ewallets.purchase.OnCallbackVerificationPaymentListener;
import com.zarinpal.ewallets.purchase.PaymentRequest;
import com.zarinpal.ewallets.purchase.ZarinPal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import cn.pedant.SweetAlert.OptAnimationLoader;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ir.hamsaa.persiandatepicker.Listener;
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.util.PersianCalendar;
import company.aryasoft.app.malayermarket.Adapters.ShoppingCartAdapter;
import company.aryasoft.app.malayermarket.ApiModels.RegisterProductOrderApiModel;
import company.aryasoft.app.malayermarket.Models.SavedShoppingCartModel;
import company.aryasoft.app.malayermarket.Models.ShoppingCartModel;
import company.aryasoft.app.malayermarket.Modules.OnFailureDataLoadListener;
import company.aryasoft.app.malayermarket.Modules.ShoppingCartModule;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ApiCallBack;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ApiServiceGenerator;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ServiceRequestApi;
import company.aryasoft.app.malayermarket.R;
import company.aryasoft.app.malayermarket.UtilityAndHelper.HelperModule;
import company.aryasoft.app.malayermarket.UtilityAndHelper.SharedPreferencesHelper;
import company.aryasoft.app.malayermarket.UtilityAndHelper.ShoppingCartManger;
import company.aryasoft.app.malayermarket.ZarinPalPort.PayViaZarinPalPort;
import company.aryasoft.app.malayermarket.ZarinPalPort.PaymentModel;
import retrofit2.Call;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ShoppingCartActivity extends AppCompatActivity implements View.OnClickListener, OnFailureDataLoadListener
{
    private static final int PriceLimit = 100;
    private ArrayList<RegisterProductOrderApiModel> productOrderApiModel;
    private ServiceRequestApi requestApi;
    private ListView listView_shopping_cart;
    private ShoppingCartAdapter shoppingCartAdapter;
    private TextView total_cart;
    private Button btnPay;
    private ImageButton btn_clear_cart;
    private RelativeLayout rel_empty_cart;
    private TextView txt_profit_value;
    private SweetAlertDialog LoadingDialog;
    private ShoppingCartModule shoppingCartModule;
    private Dialog DL;
    //----------Dialog Views
    private TextView txt_delivery_date;
    private AppCompatRadioButton rd_online_payment;
    private AppCompatRadioButton rd_cash_payment;
    private AppCompatRadioButton rd_choose_today;
    private AppCompatRadioButton rd_choose_different;
    //------------------------------------
    private ArrayList<ShoppingCartModel> ShoppingCartList;
    private int UserId = 0;
    private int TotalPrice = 0;
    private int PaymentTypeId = 0;
    private String CustomerDeliveryTime = "";


    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(ShoppingCartActivity.this, LandActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        Uri data = intent.getData();
        requestApi = ApiServiceGenerator.GetApiService();
        ZarinPal.getPurchase(this).verificationPayment(data, new OnCallbackVerificationPaymentListener()
        {
            @Override
            public void onCallbackResultVerificationPayment(boolean isPaymentSuccess, final String refID, PaymentRequest paymentRequest)
            {
                if (isPaymentSuccess && paymentRequest.getAmount() == TotalPrice)
                {
                    RegisterOrder(true, refID);
                }
                else if (isPaymentSuccess && paymentRequest.getAmount() != TotalPrice)
                {
                    String warnText = "کاربر گرامی میزان مبلغ پرداختی شما با میزان مبلغ سفارش شما خوانایی و مغایرت ندارد.";
                    warnText += "\n\n";
                    warnText += "شماره کد تراکنش مبلغ فعلی شما : " + refID;
                    new AlertDialog.Builder(ShoppingCartActivity.this, R.style.CustomDialogTheme)
                            .setTitle("اخطار در پرداخت")
                            .setMessage(warnText)
                            .setPositiveButton("باشه", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                }
                            }).show();

                }
                else
                {
                    showFailedDialog();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        requestApi = ApiServiceGenerator.GetApiService();
        UserId = SharedPreferencesHelper.ReadInt("UserId");
        shoppingCartModule = new ShoppingCartModule(this);
        shoppingCartModule.setOnFailureDataLoadListener(this);
        initViews();
    }

    private void initViews()
    {
        Toolbar toolbar = findViewById(R.id.include_shopping_cart_toolbar);
        toolbar.setTitle("سبد خرید");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        LoadingDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        LoadingDialog.setTitleText(getString(R.string.loading_title));
        LoadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        LoadingDialog.setCancelable(false);
        btn_clear_cart = findViewById(R.id.btn_clear_cart);
        txt_profit_value = findViewById(R.id.txt_profit_value);
        listView_shopping_cart = findViewById(R.id.listView_shopping_cart);
        btnPay = findViewById(R.id.btn_pay);
        rel_empty_cart = findViewById(R.id.rel_empty_cart);
        total_cart = findViewById(R.id.total_cart);
        shoppingCartAdapter = new ShoppingCartAdapter(this);
        listView_shopping_cart.setAdapter(shoppingCartAdapter);
        initShoppingCart();
        initEvents();
    }

    private void initShoppingCart()
    {
        ShoppingCartList = new ArrayList<>();
        ArrayList<SavedShoppingCartModel> ShoppingCartProductList = ShoppingCartManger.GetShoppingCart();
        if (ShoppingCartProductList.size() != 0)
        {
            LoadingDialog.show();
            String ShoppingCartProductIds;
            if (ShoppingCartProductList.size() == 1)
            {
                ShoppingCartProductIds = ShoppingCartProductList.get(0).ProductId + "";
            }
            else
            {
                StringBuilder ShoppingCartProductIdsBuilder = new StringBuilder();
                for (int i = 0; i < ShoppingCartProductList.size(); ++i)
                {
                    ShoppingCartProductIdsBuilder.append(ShoppingCartProductList.get(i).ProductId);
                    if (i < ShoppingCartProductList.size() - 1)
                    {
                        ShoppingCartProductIdsBuilder.append("-");
                    }
                }
                ShoppingCartProductIds = ShoppingCartProductIdsBuilder.toString();
            }
            shoppingCartModule.GetShoppingCartProductInfo(ShoppingCartProductIds);
            shoppingCartModule.setOnGetShoppingCartProductInfoListener(new ShoppingCartModule.OnGetShoppingCartProductInfoListener()
            {
                @Override
                public void OnGetShoppingCartProductInfo(ArrayList<ShoppingCartModel> ShoppingCartProductInfoData)
                {
                    ShoppingCartList = ShoppingCartProductInfoData;
                    shoppingCartAdapter.AddToListShoppingCart(ShoppingCartProductInfoData);
                    CalculatePrice();
                    CalculateProfit();
                    LoadingDialog.dismiss();
                }
            });
        }
        else
        {
            listView_shopping_cart.setVisibility(View.GONE);
            rel_empty_cart.setVisibility(View.VISIBLE);
        }
    }

    private void PrepareCart()
    {
        productOrderApiModel = new ArrayList<>();
        for (int i = 0; i < ShoppingCartList.size(); ++i)
        {
            RegisterProductOrderApiModel ObjCartOrder = new RegisterProductOrderApiModel();
            ObjCartOrder.ProductId = ShoppingCartList.get(i).ProductId;
            ObjCartOrder.ProductPrice = ShoppingCartList.get(i).ProductPrice;
            ObjCartOrder.DiscountPercent = 0;//TODO FIX Discount
            ObjCartOrder.ProductCount = ShoppingCartList.get(i).ProductCount;
            productOrderApiModel.add(ObjCartOrder);
        }
    }

    private void initEvents()
    {
        shoppingCartAdapter.setOnDataChangeListener(new ShoppingCartAdapter.OnDataChangeListener()
        {
            public void onDataChanged()
            {
                if (ShoppingCartList.size() <= 0)
                {
                    listView_shopping_cart.setVisibility(View.GONE);
                    rel_empty_cart.setVisibility(View.VISIBLE);
                }
                CalculatePrice();
                CalculateProfit();
            }
        });
        btnPay.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CalculatePrice();
                if (ShoppingCartList.size() != 0)
                {
                    if (TotalPrice > PriceLimit)
                    {
                        ShowChoosePaymentDialog();
                        return;
                    }
                    Toast.makeText(ShoppingCartActivity.this, "قیمت نمی تواند کمتر از 100 تومان باشد.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(ShoppingCartActivity.this, R.string.empty_cart_text, Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_clear_cart.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (ShoppingCartList.size() != 0)
                {
                    ClearCartDialog();
                }
                else
                {
                    Toast.makeText(ShoppingCartActivity.this, R.string.empty_cart_text, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void ShowChoosePaymentDialog()
    {
        if (UserId == -1)
        {
            ShowSignUpDialog();
        }
        else
        {
            final AnimationSet mModalInAnim = (AnimationSet) OptAnimationLoader.loadAnimation(this, cn.pedant.SweetAlert.R.anim.modal_in);
            final AnimationSet mModalOutAnim = (AnimationSet) OptAnimationLoader.loadAnimation(this, cn.pedant.SweetAlert.R.anim.modal_out);
            final android.support.v7.app.AlertDialog.Builder RegisterOrderAlert = new android.support.v7.app.AlertDialog.Builder(ShoppingCartActivity.this);
            View AlertView = View.inflate(ShoppingCartActivity.this, R.layout.register_order_payment, null);
            RegisterOrderAlert.setCancelable(false);
            RegisterOrderAlert.setView(AlertView);
            txt_delivery_date = AlertView.findViewById(R.id.txt_delivery_date);
            rd_online_payment = AlertView.findViewById(R.id.rd_online_payment);
            rd_cash_payment = AlertView.findViewById(R.id.rd_cash_payment);
            rd_choose_today = AlertView.findViewById(R.id.rd_choose_today);
            rd_choose_different = AlertView.findViewById(R.id.rd_choose_different);
            Button btn_register_new_order = AlertView.findViewById(R.id.btn_register_new_order);
            Button btn_cancel_new_order = AlertView.findViewById(R.id.btn_cancel_new_order);
            rd_online_payment.setOnClickListener(ShoppingCartActivity.this);
            rd_cash_payment.setOnClickListener(ShoppingCartActivity.this);
            rd_choose_today.setOnClickListener(ShoppingCartActivity.this);
            rd_choose_different.setOnClickListener(ShoppingCartActivity.this);
            DL = RegisterOrderAlert.create();
            DL.setOnDismissListener(new DialogInterface.OnDismissListener()
            {
                @Override
                public void onDismiss(DialogInterface dialog)
                {
                    ((ViewGroup) DL.getWindow().getDecorView()).getChildAt(0).startAnimation(mModalOutAnim);
                }
            });
            ((ViewGroup) DL.getWindow().getDecorView()).getChildAt(0).startAnimation(mModalInAnim);
            DL.getWindow().getDecorView().getBackground().setAlpha(255);
            DL.show();
            //--------------------
            btn_cancel_new_order.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    CustomerDeliveryTime = "";
                    DL.dismiss();

                }
            });
            btn_register_new_order.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(final View v)
                {
                    if (!(CustomerDeliveryTime.equals("")) && (rd_online_payment.isChecked() || rd_cash_payment.isChecked()))
                    {
                        DL.dismiss();
                        LoadingDialog.show();
                        Call<Integer> UserResidenceStatus = requestApi.UserResidenceStatus(UserId);
                        UserResidenceStatus.enqueue(new ApiCallBack<Integer>(v.getContext(), UserResidenceStatus)
                        {
                            @Override
                            public void onResponse(Call<Integer> call, Response<Integer> response)
                            {
                                LoadingDialog.dismiss();
                                if (response.body() == -4)
                                {
                                    ShowCompleteProfileDialog();
                                }
                                else
                                {
                                    PrepareCart();
                                    if (PaymentTypeId == 1)
                                    {
                                        String merchant = getResources().getString(R.string.merchant_id);
                                        PaymentModel paymentModel = new PaymentModel(merchant, TotalPrice, getString(R.string.payment_decription));
                                        paymentRequest(paymentModel);
                                    }
                                    else
                                    {
                                        RegisterOrder(false, "-1");
                                    }
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
                    else
                    {
                        Toast.makeText(ShoppingCartActivity.this, "لطفا نحوه پرداخت را به درستی کامل کنید.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void ShowCompleteProfileDialog()
    {
        android.support.v7.app.AlertDialog.Builder NotifyDialogAlert = new android.support.v7.app.AlertDialog.Builder(ShoppingCartActivity.this);
        NotifyDialogAlert.setCancelable(false);
        View AlertView = View.inflate(ShoppingCartActivity.this, R.layout.dialog_notify, null);
        TextView NotifyDialogTitle = AlertView.findViewById(R.id.txt_dialog_title);
        TextView NotifyDialogMessage = AlertView.findViewById(R.id.txt_dialog_message);
        NotifyDialogTitle.setText("عدم وجود اشتراک");
        NotifyDialogMessage.setText("مشترک گرامی شما در سیستم هیچگونه آدرس یا اشتراکی برای خود ثبت نکردید.\n لطفا از قسمت مدیریت پروفایل نسبت به این عمل اقدام کنید.");
        NotifyDialogAlert.setView(AlertView);
        NotifyDialogAlert.setPositiveButton("آها باشه", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                finish();
                startActivity(new Intent(ShoppingCartActivity.this, ProfileActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        NotifyDialogAlert.show();
    }

    private void ShowSignUpDialog()
    {
        android.support.v7.app.AlertDialog.Builder SignInAlert = new android.support.v7.app.AlertDialog.Builder(ShoppingCartActivity.this);
        SignInAlert.setCancelable(false);
        View AlertView = View.inflate(ShoppingCartActivity.this, R.layout.dialog_notify, null);
        TextView NotifyDialogTitle = AlertView.findViewById(R.id.txt_dialog_message);
        NotifyDialogTitle.setText("کاربر گرامی شما کاربر مهمان هستید ، برای استفاده از امکانات برنامه باید ثبت نام کنید یا اینکه ورود به سیستم کنید.");
        SignInAlert.setView(AlertView);
        SignInAlert.setPositiveButton("بریم ثبت نام/ ورود", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                finish();
                startActivity(new Intent(ShoppingCartActivity.this, RegistrationActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        //--------------------
        SignInAlert.show();
    }


    private void ClearCartDialog()
    {
        new SweetAlertDialog(ShoppingCartActivity.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("خالی کردن سبد")
                .setContentText("کاربرعزیز مایل به پاک کردن سبد خریدتان هستید؟")
                .setCancelText("خیر")
                .setConfirmText("بله")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                {
                    @Override
                    public void onClick(SweetAlertDialog sDialog)
                    {
                        ShoppingCartManger.ClearCart();
                        ShoppingCartList.clear();
                        shoppingCartAdapter.notifyDataSetChanged();
                        CalculatePrice();
                        CalculateProfit();
                        listView_shopping_cart.setVisibility(View.GONE);
                        rel_empty_cart.setVisibility(View.VISIBLE);
                        sDialog.dismiss();
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener()
                {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog)
                    {
                        sweetAlertDialog.dismiss();
                    }
                })
                .show();
    }

    private void CalculatePrice()
    {
        int CalculatedPrice = 0;
        for (int i = 0; i < ShoppingCartList.size(); ++i)
        {
            CalculatedPrice += ShoppingCartList.get(i).ProductCount * ShoppingCartList.get(i).ProductPrice;
        }
        TotalPrice = CalculatedPrice;
        total_cart.setText(TotalPrice + getString(R.string.price_unit));
    }

    private void CalculateProfit()
    {
        int TotalProfit = 0;
        for (int i = 0; i < ShoppingCartList.size(); ++i)
        {
            TotalProfit += (ShoppingCartList.get(i).ProductCount * (ShoppingCartList.get(i).CoverPrice - ShoppingCartList.get(i).ProductPrice));
        }
        txt_profit_value.setText(" میزان سود شما " + Math.abs(TotalProfit) + " تومان شد. ");
    }

    //-------------------------------------------------------------------------
    private void paymentRequest(PaymentModel paymentModel)
    {
        PayViaZarinPalPort zarinPalPort = new PayViaZarinPalPort(ShoppingCartActivity.this);
        zarinPalPort.pay(paymentModel);
        LoadingDialog.show();
        zarinPalPort.setPaymentReady(new PayViaZarinPalPort.OnPaymentReady()
        {
            @Override
            public void OnReady()
            {
                LoadingDialog.dismiss();
            }
        });
    }

    private void ResetPaymentVariables()
    {
        ShoppingCartList.clear();
        UserId = -1;
        TotalPrice = 0;
        productOrderApiModel = null;
        CustomerDeliveryTime = "";
        PaymentTypeId = 0;
    }

    private void FailedRegisterOrder(String refID)
    {
        android.support.v7.app.AlertDialog.Builder NotifyDialogAlert = new android.support.v7.app.AlertDialog.Builder(ShoppingCartActivity.this);
        NotifyDialogAlert.setCancelable(false);
        View AlertView = View.inflate(ShoppingCartActivity.this, R.layout.dialog_notify, null);
        TextView NotifyDialogTitle = AlertView.findViewById(R.id.txt_dialog_title);
        TextView NotifyDialogMessage = AlertView.findViewById(R.id.txt_dialog_message);
        NotifyDialogTitle.setText("خطادر سفارش");
        if (refID.equals("-1"))
        {
            NotifyDialogMessage.setText("کاربر عزیز با عرض معذرت به علت قطعی ارتباط با سرور سفارش شما در سیستم ثبت نشد.");
        }
        else
        {
            NotifyDialogMessage.setText("کاربر عزیز با عرض معذرت به علت قطعی ارتباط با سرور سفارش شما در سیستم ثبت نشد.کد رهگیری پرداخت شما : " + refID + " می باشد.لطفا با مدیریت تماس بگیرید.");
        }
        NotifyDialogAlert.setView(AlertView);
        NotifyDialogAlert.setPositiveButton("باشه،فهمیدم", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        NotifyDialogAlert.show();
    }

    private void RegisterOrder(final boolean IsOnline, final String refID)
    {
        if (LoadingDialog != null)
        {
            if (!LoadingDialog.isShowing())
            {
                LoadingDialog.show();
            }
        }
        Call<Integer> RegisterOrderWithProducts = requestApi.RegisterOrderWithProducts(UserId, HelperModule.arabicToDecimal(CustomerDeliveryTime), PaymentTypeId, TotalPrice, IsOnline, productOrderApiModel);
        RegisterOrderWithProducts.enqueue(new ApiCallBack<Integer>(this, RegisterOrderWithProducts)
        {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response)
            {
                LoadingDialog.dismiss();
                if (IsOnline)
                {
                    if (response.isSuccessful())
                    {
                        if (response.body() != -1)
                        {
                            ShowSuccessfulOnlineOrderDialog(refID);
                        }
                        else
                        {
                            FailedRegisterOrder(refID);
                        }
                    }
                    else
                    {
                        FailedRegisterOrder(refID);
                    }
                }
                else
                {
                    if (response.isSuccessful())
                    {
                        if (response.body() != -1)
                        {
                            ShowSuccessfulOfflineOrderDialog();
                        }
                        else
                        {
                            FailedRegisterOrder(refID);
                        }
                    }
                    else
                    {
                        FailedRegisterOrder(refID);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t)
            {
                LoadingDialog.dismiss();
                FailedRegisterOrder(refID);
                super.onFailure(call, t);
            }
        });
    }

    private void ShowSuccessfulOfflineOrderDialog()
    {
        //--------------------------------------------------------------------
        android.support.v7.app.AlertDialog.Builder NotifyDialogAlert = new android.support.v7.app.AlertDialog.Builder(ShoppingCartActivity.this);
        NotifyDialogAlert.setCancelable(false);
        View AlertView = View.inflate(ShoppingCartActivity.this, R.layout.dialog_notify, null);
        TextView NotifyDialogTitle = AlertView.findViewById(R.id.txt_dialog_title);
        TextView NotifyDialogMessage = AlertView.findViewById(R.id.txt_dialog_message);
        NotifyDialogTitle.setText("ثبت سفارش انجام شد");
        NotifyDialogMessage.setText("کاربر عزیز سفارش شما درسیستم ثبت شد.لطفا در زمان مراجعه پیک هزینه ی سفارش خود را به صورت نقدی پرداخت کنید.");
        NotifyDialogAlert.setView(AlertView);
        NotifyDialogAlert.setPositiveButton("باشه،فهمیدم", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(final DialogInterface dialog, int which)
            {
                dialog.dismiss();
                ShoppingCartList.clear();
                shoppingCartAdapter.notifyDataSetChanged();
                CalculatePrice();
                CalculateProfit();
                listView_shopping_cart.setVisibility(View.GONE);
                rel_empty_cart.setVisibility(View.VISIBLE);
                ShoppingCartManger.ClearCart();
                ResetPaymentVariables();
            }
        });
        NotifyDialogAlert.show();
    }

    private void ShowSuccessfulOnlineOrderDialog(String refID)
    {
        android.support.v7.app.AlertDialog.Builder NotifyDialogAlert = new android.support.v7.app.AlertDialog.Builder(ShoppingCartActivity.this);
        NotifyDialogAlert.setCancelable(false);
        View AlertView = View.inflate(ShoppingCartActivity.this, R.layout.dialog_notify, null);
        TextView NotifyDialogTitle = AlertView.findViewById(R.id.txt_dialog_title);
        TextView NotifyDialogMessage = AlertView.findViewById(R.id.txt_dialog_message);
        NotifyDialogTitle.setText("ثبت سفارش انجام شد");
        NotifyDialogMessage.setText("کاربر عزیز سفارش شما درسیستم ثبت شد.سفارش شما به زودی تحویل شما می گردد. شماره تراکنش پرداخت شما :" + refID);
        NotifyDialogAlert.setView(AlertView);
        NotifyDialogAlert.setPositiveButton("باشه،متشکر", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                ShoppingCartList.clear();
                shoppingCartAdapter.notifyDataSetChanged();
                CalculatePrice();
                CalculateProfit();
                listView_shopping_cart.setVisibility(View.GONE);
                rel_empty_cart.setVisibility(View.VISIBLE);
                ShoppingCartManger.ClearCart();
                ResetPaymentVariables();
                dialog.dismiss();
            }
        });
        NotifyDialogAlert.show();
    }


    private void showFailedDialog()
    {
        new SweetAlertDialog(ShoppingCartActivity.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("خطا در پرداخت")
                .setContentText("کاربر گرامی پرداخت انجام نشد")
                .setCancelText("باشه")
                .setConfirmText("تلاش مجدد")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                {
                    @Override
                    public void onClick(SweetAlertDialog sDialog)
                    {
                        String merchant = getResources().getString(R.string.merchant_id);
                        PaymentModel paymentModel = new PaymentModel(merchant, TotalPrice, "پرداخت هزینه خرید از فروشگاه ملایرمارکت");
                        paymentRequest(paymentModel);
                        sDialog.dismiss();
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener()
                {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog)
                    {
                        sweetAlertDialog.dismiss();
                    }
                }).show();
    }


    //-----------------------------------------------------------------------------
    @Override
    public void onClick(View v)
    {
        if (v.getId() == rd_online_payment.getId())
        {
            PaymentTypeId = 1;
            rd_online_payment.setChecked(true);
            rd_cash_payment.setChecked(false);
        }
        else if (v.getId() == rd_cash_payment.getId())
        {
            PaymentTypeId = 2;
            rd_cash_payment.setChecked(true);
            rd_online_payment.setChecked(false);
        }
        //--------------
        else if (v.getId() == rd_choose_today.getId())
        {
            rd_choose_today.setChecked(true);
            rd_choose_different.setChecked(false);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            CustomerDeliveryTime = dateFormat.format(new PersianCalendar().getTime()) + "";
            txt_delivery_date.setText("سفارش شما کمتر از نیم ساعت دیگر تحویل داده می شود.");
        }
        else if (v.getId() == rd_choose_different.getId())
        {
            rd_choose_different.setChecked(true);
            rd_choose_today.setChecked(false);
            PersianDatePickerDialog picker = new PersianDatePickerDialog(this)
                    .setPositiveButtonString("باشه")
                    .setNegativeButton("بیخیال")
                    .setMinYear(new PersianCalendar().getPersianYear())
                    .setMaxYear(new PersianCalendar().getPersianYear() + 1)
                    .setActionTextColor(Color.GRAY)
                    .setListener(new Listener()
                    {
                        @Override
                        public void onDateSelected(PersianCalendar persianCalendar)
                        {

                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            CustomerDeliveryTime = dateFormat.format(persianCalendar.getTime()) + "";
                            txt_delivery_date.setText(getResources().getString(R.string.order_delivery) + " " + persianCalendar.getPersianLongDate());
                        }

                        @Override
                        public void onDismissed()
                        {
                            CustomerDeliveryTime = "";
                            txt_delivery_date.setText("");
                        }
                    });
            picker.show();
        }
    }

    @Override
    public void OnFailureLoadData(Throwable t)
    {
        if (LoadingDialog.isShowing())
        {
            LoadingDialog.dismiss();
        }
    }
}