package company.aryasoft.app.malayermarket.UtilityAndHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import company.aryasoft.app.malayermarket.Models.SavedShoppingCartModel;


public class ShoppingCartManger
{
    public static int CartManagerDecrease(int productId)
    {
        ArrayList<SavedShoppingCartModel> MyCart = GetShoppingCart();
        int count = 0;
        int FoundedProductCartIndex = -1;
        for (int i = 0; i < MyCart.size(); ++i)
        {
            if (MyCart.get(i).ProductId == productId)
            {
                FoundedProductCartIndex = i;
                break;
            }
        }
        //-----------------
        if (FoundedProductCartIndex != -1)
        {
            if (MyCart.get(FoundedProductCartIndex).ProductCount - 1 == 0)
            {
                MyCart.remove(FoundedProductCartIndex);
                count = -1;
                RemoveProductOfCart(FoundedProductCartIndex);
            }
            else
            {
                MyCart = GetShoppingCart();
                --MyCart.get(FoundedProductCartIndex).ProductCount;
                UpdateShoppingCart(MyCart.get(FoundedProductCartIndex), FoundedProductCartIndex);
                //RefreshCart();
                MyCart = GetShoppingCart();
                count = MyCart.get(FoundedProductCartIndex).ProductCount;

            }
        }
        return count;
    }

    public static boolean IsProductExist(int ProductId)
    {
        boolean IsExist = false;
        ArrayList<SavedShoppingCartModel> MyCart = GetShoppingCart();
        for (int i = 0; i < MyCart.size(); ++i)
        {
            if (MyCart.get(i).ProductId == ProductId)
            {
                IsExist = true;
                break;
            }
        }
        return IsExist;
    }

    public static boolean AddProductToCart(int productId)
    {
        ArrayList<SavedShoppingCartModel> MyCart = GetShoppingCart();
        boolean ProductExist = false;
        int FoundedProductCartIndex = -1;
        for (int i = 0; i < MyCart.size(); ++i)
        {
            if (MyCart.get(i).ProductId == productId)
            {
                ProductExist = true;
                FoundedProductCartIndex = i;
                break;
            }
        }
        if (!ProductExist)
        {
            SavedShoppingCartModel NewCart = new SavedShoppingCartModel();
            NewCart.ProductId = productId;
            ++NewCart.ProductCount;
            AddToShoppingCartSingleItem(NewCart);
            //RefreshCart();
        }
        return ProductExist;
    }

    public static boolean CartManagerIncrease(int productId)
    {
        ArrayList<SavedShoppingCartModel> MyCart = GetShoppingCart();
        boolean ProductExist = false;
        int FoundedProductCartIndex = -1;

        for (int i = 0; i < MyCart.size(); ++i)
        {
            if (MyCart.get(i).ProductId == productId)
            {
                ProductExist = true;
                FoundedProductCartIndex = i;
                break;
            }
        }
        if (!ProductExist)
        {
            SavedShoppingCartModel NewCart = new SavedShoppingCartModel();
            NewCart.ProductId = productId;
            ++NewCart.ProductCount;
            AddToShoppingCartSingleItem(NewCart);
            //RefreshCart();
        }
        else
        {
            MyCart.get(FoundedProductCartIndex).ProductCount++;
            UpdateShoppingCart(MyCart.get(FoundedProductCartIndex), FoundedProductCartIndex);
            //RefreshCart();
        }
        return ProductExist;
    }
    //-----------------------

    public static int FindProductCount(int ProductId)
    {
        ArrayList<SavedShoppingCartModel> MyCart = GetShoppingCart();
        int count = 0;
        for (int i = 0; i < MyCart.size(); ++i)
        {
            if (MyCart.get(i).ProductId == ProductId)
            {
                count = MyCart.get(i).ProductCount;
                break;
            }
        }
        return count;
    }

    private static void AddToShoppingCartSingleItem(SavedShoppingCartModel Cart)
    {
        ArrayList<SavedShoppingCartModel> ShoppingCartData;
        ShoppingCartData = GetShoppingCart();
        ShoppingCartData.add(Cart);
        SharedPreferencesHelper.WriteString("AppShoppingCart", new Gson().toJson(ShoppingCartData));
    }

    private static void UpdateShoppingCart(SavedShoppingCartModel Cart, int index)
    {
        SavedShoppingCartModel cart = GetShoppingCart().get(index);
        cart.ProductId = Cart.ProductId;
        cart.ProductCount = Cart.ProductCount;

        ArrayList<SavedShoppingCartModel> carts = GetShoppingCart();
        carts.set(index, cart);
        SharedPreferencesHelper.WriteString("AppShoppingCart", new Gson().toJson(carts));
        //Log.i("ProductId", GetShoppingCart().get(index).ProductId + "  " + GetShoppingCart().get(index).ProductCount + "size :" + GetShoppingCart().size() + "");
    }

    private static void RemoveProductOfCart(int index)
    {
        ArrayList<SavedShoppingCartModel> carts = GetShoppingCart();
        carts.remove(index);
        SharedPreferencesHelper.WriteString("AppShoppingCart", new Gson().toJson(carts));
    }

    public static void RemoveProductById(int ProductId)
    {
        int FoundedProductCartIndex = 0;
        ArrayList<SavedShoppingCartModel> TempCart = GetShoppingCart();
        for (int i = 0; i < TempCart.size(); ++i)
        {
            if (TempCart.get(i).ProductId == ProductId)
            {
                FoundedProductCartIndex = i;
                break;
            }
        }
        ArrayList<SavedShoppingCartModel> carts = GetShoppingCart();
        carts.remove(FoundedProductCartIndex);
        SharedPreferencesHelper.WriteString("AppShoppingCart", new Gson().toJson(carts));
    }

    public static void AddToShoppingCartCollectionItem(ArrayList<SavedShoppingCartModel> SavedCartList)
    {
        boolean IsExist = false;
        ArrayList<SavedShoppingCartModel> ShoppingCartData;
        ShoppingCartData = GetShoppingCart();
        //---------------
        if (ShoppingCartData.size() > 0)
        {
            for (int i = 0; i < SavedCartList.size(); ++i)
            {
                for (int j = 0; j < ShoppingCartData.size(); ++j)
                {
                    if (SavedCartList.get(i).ProductId == ShoppingCartData.get(j).ProductId)
                    {
                        IsExist = true;
                        break;
                    }
                    else
                    {
                        IsExist = false;
                    }

                }
                if (!IsExist)
                {

                    ShoppingCartData.add(SavedCartList.get(i));
                }
            }
        }
        else
        {
            for (int i = 0; i < SavedCartList.size(); ++i)
            {
                ShoppingCartData.add(SavedCartList.get(i));
            }
        }
        SharedPreferencesHelper.WriteString("AppShoppingCart", new Gson().toJson(ShoppingCartData));
    }

    public static ArrayList<SavedShoppingCartModel> GetShoppingCart()
    {
        ArrayList<SavedShoppingCartModel> Result = new ArrayList<>();

        if (!(SharedPreferencesHelper.ReadString("AppShoppingCart").equals("def")))
        {
            Result = new Gson().fromJson(SharedPreferencesHelper.ReadString("AppShoppingCart"), new TypeToken<ArrayList<SavedShoppingCartModel>>()
            {
            }.getType());
        }
        return Result;
    }

    public static void ClearCart()
    {
        SharedPreferencesHelper.ClearPreferenceByKeyName("AppShoppingCart");
    }
}

//    private void RefreshCart(ArrayList<SavedShoppingCartModel> MyCart)
//    {
//        MyCart = HelperModule.GetShoppingCart();
//        //  Toast.makeText(ViewContext, MyCart.get(0).ProductCount+"", Toast.LENGTH_SHORT).show();
//    }

