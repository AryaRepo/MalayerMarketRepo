package company.aryasoft.app.malayermarket.ZarinPalPort;

public class PaymentModel
{
    private String merchantId;
    private long amount;
    private String paymentDescription;

    public PaymentModel(String merchantId, long amount, String paymentDescription)
    {
        this.merchantId = merchantId;
        this.amount = amount;
        this.paymentDescription = paymentDescription;
    }

    public String getMerchantId()
    {
        return merchantId;
    }

    public long getAmount()
    {
        return amount;
    }

    public String getPaymentDescription()
    {
        return paymentDescription;
    }
}
