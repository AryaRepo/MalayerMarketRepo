package company.aryasoft.app.malayermarket.Models;

/**
 * Created by MrBinary on 2/12/2018.
 */

public class ShoppingCartModel
{
    public int ProductId;
    public String ProductTitle;
    public int ProductPrice;
    public String ProductImage;
    public int ProductCount;
    public int CoverPrice ;
    public ShoppingCartModel(int ProductId,String ProductTitle,int ProductPrice,String ProductImage, int ProductCount,int CoverPrice )
    {
        this.ProductId=ProductId;
        this.ProductTitle=ProductTitle;
        this.ProductPrice=ProductPrice;
        this.ProductImage=ProductImage;
        this.ProductCount=ProductCount;
        this.CoverPrice=CoverPrice;
    }

}
