package company.aryasoft.app.malayermarket.ApiModels;


public class RegisterCommentPointApiModel
{
    public int UserId  ;
    public String CommentTitle;//max len=50
    public String CommentText; //max len=300
    public int PointValue ;
    public int ProductId ;
    public int CommentParentId;
}
