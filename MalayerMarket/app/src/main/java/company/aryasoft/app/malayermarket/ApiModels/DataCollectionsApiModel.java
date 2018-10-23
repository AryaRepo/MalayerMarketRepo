package company.aryasoft.app.malayermarket.ApiModels;

import java.util.ArrayList;

import company.aryasoft.app.malayermarket.Models.ProductCollectionModel;
import okhttp3.ResponseBody;

public class DataCollectionsApiModel
{
    public int CollectionTypeID;
    public String CollectionTypeTitle;
    public ArrayList<ProductCollectionModel> ProductInfoList;
}
