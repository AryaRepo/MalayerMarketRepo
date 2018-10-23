package company.aryasoft.app.malayermarket.NetworkApiCenter;

import java.util.ArrayList;
import java.util.List;

import company.aryasoft.app.malayermarket.ApiModels.ActiveAccountApiModel;
import company.aryasoft.app.malayermarket.ApiModels.AddUserInfoApiModel;
import company.aryasoft.app.malayermarket.ApiModels.ApplicationUpdateModel;
import company.aryasoft.app.malayermarket.ApiModels.BasicProductInfoApiModel;
import company.aryasoft.app.malayermarket.ApiModels.DataCollectionsApiModel;
import company.aryasoft.app.malayermarket.ApiModels.GetMessagesApiModel;
import company.aryasoft.app.malayermarket.ApiModels.MessageListApiModel;
import company.aryasoft.app.malayermarket.ApiModels.ProfileInfoApiModel;
import company.aryasoft.app.malayermarket.ApiModels.RecoverPasswordApiModel;
import company.aryasoft.app.malayermarket.ApiModels.RegisterAccountApiModel;
import company.aryasoft.app.malayermarket.ApiModels.RegisterProductOrderApiModel;
import company.aryasoft.app.malayermarket.ApiModels.ShoppingCartInfoApiModel;
import company.aryasoft.app.malayermarket.ApiModels.SpecialProductApiModel;
import company.aryasoft.app.malayermarket.ApiModels.ProductCommentApiModel;
import company.aryasoft.app.malayermarket.ApiModels.ProductGroupsApiModel;
import company.aryasoft.app.malayermarket.ApiModels.ProductPropertyApiModel;
import company.aryasoft.app.malayermarket.ApiModels.SimilarProductApiModel;
import company.aryasoft.app.malayermarket.ApiModels.SimpleProductApiModel;
import company.aryasoft.app.malayermarket.ApiModels.SliderModelApi;
import company.aryasoft.app.malayermarket.ApiModels.StoreOwnerApiModel;
import company.aryasoft.app.malayermarket.ApiModels.UserInfoApiModel;
import company.aryasoft.app.malayermarket.ApiModels.UserOrderDetailApiModel;
import company.aryasoft.app.malayermarket.ApiModels.UserOrdersListApiModel;
import company.aryasoft.app.malayermarket.ApiModels.ZonesApiModel;
import company.aryasoft.app.malayermarket.Models.OrderProduct;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ServiceRequestApi
{
    //region--------------------------Land Activity
    @Headers("Connection:close")
    @GET("api/Testapi/GetSliders/")
    Call<List<SliderModelApi>> GetSlider();

    @Headers("Connection:close")
    @GET("api/Testapi/GetCollections/")
    Call<List<DataCollectionsApiModel>> GetCollectionData(@Query("ListOffsetNumber") int ListOffsetNumber, @Query("ItemOffsetNumber") int ItemOffsetNumber);

    @Headers("Connection:close")
    @GET("api/Testapi/GetCollectionByTypeID/")
    Call<List<DataCollectionsApiModel>> GetCollectionByTypeID(@Query("ListOffsetNumber") int ListOffsetNumber, @Query("ListFetchNumber") int ItemOffsetNumber, @Query("CollectionTypeID") int CollectionTypeID);

    @Headers("Connection:close")
    @GET("api/Testapi/GetNewlyProducts/")
    Call<List<SpecialProductApiModel>> GetNewlyProducts(@Query("OffsetNumber") int OffsetNumber, @Query("FetchNumber") int FetchNumber);

    @Headers("Connection:close")
    @GET("api/Testapi/GetBestSellingProducts/")
    Call<List<SpecialProductApiModel>> GetBestSellingProducts(@Query("OffsetNumber") int OffsetNumber, @Query("FetchNumber") int FetchNumber);

    @Headers("Connection:close")
    @GET("api/Testapi/GetProductGroups/")
    Call<List<ProductGroupsApiModel>> GetProductGroups(@Query("GroupId") int GroupId);

    @Headers("Connection:close")
    @GET("api/Testapi/Search/")
    Call<List<SimpleProductApiModel>> Search(@Query("GroupIds") String GroupIds, @Query("SearchText") String SearchText, @Query("OffsetNumber") int OffsetNumber);

    @Headers("Connection:close")
    @GET("api/Testapi/GetProductByGroupId/")
    Call<List<SimpleProductApiModel>> GetProductByGroupId(@Query("GroupId") int GroupId, @Query("OffsetNumber") int OffsetNumber);

    @Headers("Connection:close")
    @GET("api/Testapi/GetProductProperty/")
    Call<List<ProductPropertyApiModel>> GetProductProperty(@Query("ProductId") int ProductId);
    //endregion----------------------------------===========Land Activity

    //region---------------------Product detail
    @Headers("Connection:close")
    @GET("api/Testapi/GetProductComments/")
    Call<List<ProductCommentApiModel>> GetProductComments(@Query("ProductId") int ProductId, @Query("OffsetNumber") int OffsetNumber);

    @Headers("Connection:close")
    @GET("api/Testapi/GetProductInfo/")
    Call<BasicProductInfoApiModel> GetProductInfo(@Query("ProductId") int ProductId);

    @Headers("Connection:close")
    @GET("api/Testapi/GetSimilarProducts/")
    Call<List<SimilarProductApiModel>> GetSimilarProducts(@Query("ProductId") int ProductId);

    @Headers("Connection:close")
    @POST("api/Testapi/RegisterCommentWithPointForProduct/")
    Call<Integer> RegisterCommentWithPoint(@Query("UserId") int UserId, @Query("CommentTitle") String CommentTitle, @Query("CommentText") String CommentText, @Query("PointValue") int PointValue, @Query("ProductId") int ProductId);
    //endregion-------------------------------------------------------

    //region--------------Cart and Orders
    @Headers("Connection:close")
    @GET("api/Testapi/GetUserOrdersList/")
    Call<List<UserOrdersListApiModel>> GetUserOrdersList(@Query("UserId") int UserId, @Query("OffsetNumber") int OffsetNumber);

    @Headers("Connection:close")
    @GET("api/Testapi/GetUserOrderDetail/")
    Call<List<UserOrderDetailApiModel>> GetUserOrdersList(@Query("OrderId") int OrderId);

    @Headers("Connection:close")
    @GET("api/Testapi/ShoppingCartInfo/")
    Call<List<ShoppingCartInfoApiModel>> ShoppingCartInfo(@Query("ProductIds") String ProductIds);
//endregion-------------------------------------

    //region---------------------Accounting Api
    @Headers("Connection:close")
    @POST("api/AccountAPI/Register/")
    Call<Integer> Register(@Body RegisterAccountApiModel register);

    @Headers("Connection:close")
    @POST("api/AccountAPI/ActiveUser/")
    Call<Boolean> ActiveUser(@Body ActiveAccountApiModel active);

    @Headers("Connection:close")
    @GET("api/AccountAPI/Login/")
    Call<Integer> Login(@Query("MobileNumber") String MobileNumber, @Query("Password") String Password);

    @Headers("Connection:close")
    @POST("api/AccountAPI/RecoverPassword/")
    Call<Integer> RecoverPassword(@Body RecoverPasswordApiModel recover);

    @Headers("Connection:close")
    @POST("api/AccountAPI/newpassword/")
    Call<Boolean> NewPassword(@Query("MobileNumber") String MobileNumber, @Query("OldPassword") String OldPassword, @Query("NewPassword") String NewPassword);

    @Headers("Connection:close")
    @POST("api/AccountAPI/AddUserInfo/")
    Call<Boolean> AddUserInfo(@Body AddUserInfoApiModel t_userinfo);

    @Headers("Connection:close")
    @GET("api/AccountAPI/GetUserInfo/")
    Call<UserInfoApiModel> GetUserInfo(@Query("id") int id);

    @Headers("Connection:close")
    @GET("api/AccountAPI/GetUserInfo/")
    Call<ProfileInfoApiModel> GetUserProfileImage(@Query("id") int id);

    @Headers("Connection:close")
    @GET("api/AccountAPI/RenewActiveCode/")
    Call<Integer> RenewActiveCode(@Query("MobileNumber") String MobileNumber);

    @Headers("Connection:close")
    @GET("api/AccountAPI/GetZone/")
    Call<List<ZonesApiModel>> GetZones();

    @Headers("Connection:close")
    @POST("api/AccountAPI/CreateSubscriptionCode/")
    Call<Boolean> CreateSubscriptionCode(@Query("UserId") int UserId, @Query("ZoneCode") int ZoneCode);

    @Headers("Connection:close")
    @GET("api/AccountAPI/UserResidenceStatus/")
    Call<Integer> UserResidenceStatus(@Query("UserId") int UserId);


    //endregion--------------------------------------------------------------------------------------------------------

    //region----------------Messaging Api
    @Headers("Connection:close")
    @GET("api/Testapi/GetStoreOwnerID/")
    Call<List<StoreOwnerApiModel>> GetStoreOwnerID();

    @Headers("Connection:close")
    @POST("api/Testapi/CreateNewMessageTicket/")
    Call<Boolean> CreateNewMessageTicket(@Query("MessageTitle") String MessageTitle, @Query("MessageText") String MessageText, @Query("AdminId") int AdminId, @Query("UserId") int UserId);

    @Headers("Connection:close")
    @GET("api/Testapi/GetMessages/")
    Call<List<GetMessagesApiModel>> GetMessages(@Query("UserId") int UserId, @Query("OffsetNumber") int OffsetNumber);

    @Headers("Connection:close")
    @GET("api/Testapi/GetMessageListByMessageId/")
    Call<List<MessageListApiModel>> GetMessageListByMessageId(@Query("MessageId") int MessageId, @Query("OffsetNumber") int OffsetNumber);

    @Headers("Connection:close")
    @POST("api/Testapi/ResponseToTiketMessage/")
    Call<Integer> ResponseToTicketMessage(@Query("MessageId") int MessageId, @Query("MessageText") String MessageText, @Query("UserId") int UserId, @Query("MessageStateId") int MessageStateId);

    //endregion----------------Messaging Api

    //region--------------------ShoppingCart Orders
    @Headers("Connection:close")
    @POST("api/Testapi/ChangeOrderPaymentStatus/")
    Call<Boolean> ChangeOrderPaymentStatus(@Query("OrderId") int OrderId);

    @Headers("Connection:close")
    @POST("api/Testapi/RegisterOrderWithProducts/")
    Call<Integer> RegisterOrderWithProducts(@Query("UserId") int UserId, @Query("CustomerDeliveryTime") String CustomerDeliveryTime, @Query("PaymentTypeId") int PaymentTypeId, @Query("TotalPrice") int TotalPrice, @Query("PaymentStatus") boolean PaymentStatus, @Body List<RegisterProductOrderApiModel> listOrder);


    //endregion---------------------

    @Headers("Connection:close")
    @GET("/android-app/VersionUpdate.json")
    Call<ApplicationUpdateModel> CheckUpdates();
}