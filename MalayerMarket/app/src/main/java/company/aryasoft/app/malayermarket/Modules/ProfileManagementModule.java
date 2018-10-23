package company.aryasoft.app.malayermarket.Modules;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import company.aryasoft.app.malayermarket.ApiModels.AddUserInfoApiModel;
import company.aryasoft.app.malayermarket.ApiModels.UserInfoApiModel;
import company.aryasoft.app.malayermarket.ApiModels.UserOrderDetailApiModel;
import company.aryasoft.app.malayermarket.ApiModels.ZonesApiModel;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ApiCallBack;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ApiServiceGenerator;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ServiceRequestApi;
import company.aryasoft.app.malayermarket.UtilityAndHelper.HelperModule;
import retrofit2.Call;
import retrofit2.Response;

public class ProfileManagementModule
{
    private Context context;
    private ServiceRequestApi requestApi;
    private OnFailureDataLoadListener onFailureDataLoadListener;
    private OnPasswordChangedListener onPasswordChangedListener;
    private OnUserResidenceStatusListener onUserResidenceStatusListener;
    private OnGetProfileInfoListener onGetProfileInfoListener;
    private OnSubscriptionCodeCreatedListener onSubscriptionCodeCreatedListener;
    private OnEditProfileListener onEditProfileListener;
    private OnGetCityZonesListener onGetCityZonesListener;

    public void setOnGetCityZonesListener(OnGetCityZonesListener onGetCityZonesListener)
    {
        this.onGetCityZonesListener = onGetCityZonesListener;
    }

    public void setOnEditProfileListener(OnEditProfileListener onEditProfileListener)
    {
        this.onEditProfileListener = onEditProfileListener;
    }

    public void setOnSubscriptionCodeCreatedListener(OnSubscriptionCodeCreatedListener onSubscriptionCodeCreatedListener)
    {
        this.onSubscriptionCodeCreatedListener = onSubscriptionCodeCreatedListener;
    }

    public void setOnGetProfileInfoListener(OnGetProfileInfoListener onGetProfileInfoListener)
    {
        this.onGetProfileInfoListener = onGetProfileInfoListener;
    }

    public void setOnUserResidenceStatusListener(OnUserResidenceStatusListener onUserResidenceStatusListener)
    {
        this.onUserResidenceStatusListener = onUserResidenceStatusListener;
    }

    public void setOnPasswordChangedListener(OnPasswordChangedListener onPasswordChangedListener)
    {
        this.onPasswordChangedListener = onPasswordChangedListener;
    }

    public void setOnFailureDataLoadListener(OnFailureDataLoadListener onFailureDataLoadListener)
    {
        this.onFailureDataLoadListener = onFailureDataLoadListener;
    }

    public ProfileManagementModule(Context context)
    {
        this.context = context;
        this.requestApi = ApiServiceGenerator.GetApiService();
    }

    public void EditAccountPassword(String MobileNumber, String OldPassword, final String NewPassword)
    {
        Call<Boolean> ChangePassword = requestApi.NewPassword(HelperModule.arabicToDecimal(MobileNumber), HelperModule.arabicToDecimal(OldPassword), HelperModule.arabicToDecimal(NewPassword));
        ChangePassword.enqueue(new ApiCallBack<Boolean>(context, ChangePassword)
        {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response)
            {
                onPasswordChangedListener.OnPasswordChanged(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t)
            {
                onFailureDataLoadListener.OnFailureLoadData(t);
                super.onFailure(call, t);
            }
        });
    }

    public void UserResidenceStatus(int UserId)
    {
        Call<Integer> UserResidenceStatus = requestApi.UserResidenceStatus(UserId);
        UserResidenceStatus.enqueue(new ApiCallBack<Integer>(context, UserResidenceStatus)
        {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response)
            {
                onUserResidenceStatusListener.OnUserResidenceStatus(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t)
            {
                onFailureDataLoadListener.OnFailureLoadData(t);
                super.onFailure(call, t);
            }
        });
    }

    public void GetProfileInfo(int UserId)
    {
        Call<UserInfoApiModel> GetUserInfo = requestApi.GetUserInfo(UserId);
        GetUserInfo.enqueue(new ApiCallBack<UserInfoApiModel>(context, GetUserInfo)
        {
            @Override
            public void onResponse(Call<UserInfoApiModel> call, final Response<UserInfoApiModel> response)
            {
                onGetProfileInfoListener.OnGetProfileInfo(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<UserInfoApiModel> call, @NonNull Throwable t)
            {
                onFailureDataLoadListener.OnFailureLoadData(t);
                super.onFailure(call, t);
            }
        });
    }

    public void EditProfile(AddUserInfoApiModel ObjAddInfo)
    {
        Call<Boolean> AddUserInfo = requestApi.AddUserInfo(ObjAddInfo);
        AddUserInfo.enqueue(new ApiCallBack<Boolean>(context, AddUserInfo)
        {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response)
            {
                onEditProfileListener.OnEditProfile(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t)
            {
                onFailureDataLoadListener.OnFailureLoadData(t);
                super.onFailure(call, t);
            }
        });
    }

    public void GetCityZones()
    {
        Call<List<ZonesApiModel>> GetZones = requestApi.GetZones();
        GetZones.enqueue(new ApiCallBack<List<ZonesApiModel>>(context, GetZones)
        {
            @Override
            public void onResponse(Call<List<ZonesApiModel>> call, Response<List<ZonesApiModel>> response)
            {
                String[] ZoneTitle;
                if (response.body() != null)
                {
                    ZoneTitle = new String[response.body().size()];
                    for (int i = 0; i < response.body().size(); ++i)
                    {
                        ZoneTitle[i] = response.body().get(i).zonename;
                    }
                    ArrayList<ZonesApiModel> DataList = new ArrayList<>(response.body()!=null ? response.body():new ArrayList<ZonesApiModel>());
                    onGetCityZonesListener.OnGetCityZones(DataList,ZoneTitle);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ZonesApiModel>> call, @NonNull Throwable t)
            {
                onFailureDataLoadListener.OnFailureLoadData(t);
                super.onFailure(call, t);
            }
        });

    }

    public void CreateSubscriptionForUser(int UserId, int ZoneCode)
    {
        Call<Boolean> CreateSubscriptionCode = requestApi.CreateSubscriptionCode(UserId, ZoneCode);
        CreateSubscriptionCode.enqueue(new ApiCallBack<Boolean>(context, CreateSubscriptionCode)
        {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response)
            {
                onSubscriptionCodeCreatedListener.OnSubscriptionCodeCreated(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t)
            {
                onFailureDataLoadListener.OnFailureLoadData(t);
                super.onFailure(call, t);
            }
        });
    }

    //-----------------------------------------Interfaces

    public interface OnPasswordChangedListener
    {
        void OnPasswordChanged(Boolean PasswordChangedState);
    }

    public interface OnUserResidenceStatusListener
    {
        void OnUserResidenceStatus(Integer UserState);
    }

    public interface OnGetProfileInfoListener
    {
        void OnGetProfileInfo(UserInfoApiModel ProfileUserInfo);
    }

    public interface OnSubscriptionCodeCreatedListener
    {
        void OnSubscriptionCodeCreated(Boolean SubscriptionCodeCreatedState);
    }

    public interface OnEditProfileListener
    {
        void OnEditProfile(Boolean OnEditProfileState);
    }

    public interface OnGetCityZonesListener
    {
        void OnGetCityZones( ArrayList<ZonesApiModel> ZoneData,String[] ZoneTitle);
    }
}
