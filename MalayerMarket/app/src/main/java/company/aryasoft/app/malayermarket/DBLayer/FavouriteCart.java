package company.aryasoft.app.malayermarket.DBLayer;

import com.orm.SugarRecord;

public class FavouriteCart extends SugarRecord
{
    public String CartName;//Basket Name
    //private List<ProductFavouriteCart> DataListFavouriteCart = null;
  //  public ProductFavouriteCart favouriteCart ;
    public FavouriteCart()
    {
        //Default Constructor

    }
    public FavouriteCart(String CartName,ProductFavouriteCart dataListFavouriteCart)
    {
        //Default Constructor
        this.CartName=CartName;
       // this.favouriteCart=dataListFavouriteCart;
       // this.favouriteCart.save();
    }
    public FavouriteCart(String CartName)
    {
        //Default Constructor
        this.CartName=CartName;
    }
//    public List<ProductFavouriteCart> getFavouriteCart(long id)
//    {
//        List<ProductFavouriteCart>FavData= ProductFavouriteCart.find(ProductFavouriteCart.class,"id="+id);
//        return FavData;
//    }

//    public void setFavouriteCart(ProductFavouriteCart favouriteCart)
//    {
//        this.favouriteCart = favouriteCart;
//      ///  this.favouriteCart.save();
//    }

    public String getCartName()
    {
        return CartName;
    }

    public void setCartName(String CartName)
    {
        this.CartName = CartName;
    }
}
