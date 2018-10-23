package company.aryasoft.app.malayermarket.Models;

import java.util.ArrayList;

import company.aryasoft.app.malayermarket.ApiModels.ProductCommentApiModel;
import company.aryasoft.app.malayermarket.ApiModels.ProductPropertyApiModel;
import company.aryasoft.app.malayermarket.ApiModels.SimilarProductApiModel;

public class DetailModel
{
    public int ProductID=-1;
    public String ProductTitle;
    public int CoverPrice;
    public int SalesPrice;
    public String ImageName;
    public String Description ;
    public ArrayList<String> Gallary ;
    public Integer  point ;
    public ArrayList<SimilarProductApiModel>SimilarProductList;
    public ArrayList<ProductPropertyApiModel>PropertyList;
    public ArrayList<ProductCommentApiModel> CommentsList;
}
