package company.aryasoft.app.malayermarket.Models;

/**
 * Created by MohamadAmin on 2/10/2018.
 */

public class SimilarProductModel
{
    private String productName;
    private int productPhoto;

    public SimilarProductModel(String productName, int productPhoto)
    {
        this.productName = productName;
        this.productPhoto = productPhoto;
    }

    public String getProductName()
    {
        return productName;
    }

    public int getProductPhoto()
    {
        return productPhoto;
    }

    public void setProductName(String productName) {this.productName = productName;}

    public void setProductPhoto(int productPhoto)
    {
        this.productPhoto = productPhoto;
    }
}
