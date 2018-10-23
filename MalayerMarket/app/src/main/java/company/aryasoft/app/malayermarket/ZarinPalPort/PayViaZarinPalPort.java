package company.aryasoft.app.malayermarket.ZarinPalPort;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import com.zarinpal.ewallets.purchase.OnCallbackRequestPaymentListener;
import com.zarinpal.ewallets.purchase.PaymentRequest;
import com.zarinpal.ewallets.purchase.ZarinPal;

import java.util.ArrayList;

import company.aryasoft.app.malayermarket.ApiModels.RegisterProductOrderApiModel;
import company.aryasoft.app.malayermarket.R;

public class PayViaZarinPalPort
{
    public interface OnPaymentReady
    {
        void OnReady();
    }

    private ZarinPal purchase;
    private Context context;
    private static final int SUCCESS_CODE = 100;
    private OnPaymentReady paymentReady;

    public void setPaymentReady(OnPaymentReady paymentReady)
    {
        this.paymentReady = paymentReady;
    }

    public PayViaZarinPalPort(Context yourContext)
    {
        this.context = yourContext;
        this.purchase = ZarinPal.getPurchase(context);
    }

    public void pay(PaymentModel paymentModel)
    {
        PaymentRequest paymentRequest = ZarinPal.getPaymentRequest();
        paymentRequest.setMerchantID(paymentModel.getMerchantId());
        paymentRequest.setAmount(paymentModel.getAmount());
        paymentRequest.setDescription(paymentModel.getPaymentDescription());
        paymentRequest.setCallbackURL(context.getString(R.string.zarinpal_callback_url));
        purchase.startPayment(paymentRequest, new OnCallbackRequestPaymentListener()
        {
            @Override
            public void onCallbackResultPaymentRequest(int status, String authority, Uri paymentGatewayUri, Intent intent)
            {
                if (status == SUCCESS_CODE)
                {
                    context.startActivity(intent);
                    paymentReady.OnReady();
                }
                else
                {
                    Toast.makeText(context, "درخواست پرداخت شما با مشکل مواجه شد.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
