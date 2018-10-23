package company.aryasoft.app.malayermarket.DBLayer;

import com.google.gson.annotations.Expose;
import com.orm.SugarApp;
import com.orm.SugarRecord;
import com.orm.dsl.Column;

public class ProductFavouriteCart extends SugarRecord
{

    public int ProductId;
    public String ProductTitle;
    public int ProductCount;
    public long BasketId;
    public ProductFavouriteCart()
    {
        //default const
    }
}
