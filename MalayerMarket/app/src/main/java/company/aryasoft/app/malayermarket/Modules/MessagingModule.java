package company.aryasoft.app.malayermarket.Modules;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import company.aryasoft.app.malayermarket.ApiModels.GetMessagesApiModel;
import company.aryasoft.app.malayermarket.ApiModels.MessageListApiModel;
import company.aryasoft.app.malayermarket.ApiModels.StoreOwnerApiModel;
import company.aryasoft.app.malayermarket.ApiModels.ZonesApiModel;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ApiCallBack;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ApiServiceGenerator;
import company.aryasoft.app.malayermarket.NetworkApiCenter.ServiceRequestApi;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class MessagingModule
{
    private Context context;
    private ServiceRequestApi requestApi;
    private OnFailureDataLoadListener onFailureLoadDataListener;
    private OnGetStoreOwnerUserIdListener onGetStoreOwnerUserIdListener;
    private OnLoadTicketHistoryListener onLoadTicketHistoryListener;
    private OnCreateNewTicketMessageListener onCreateNewTicketMessageListener;
    private OnGetMessagesOfTicketListener onGetMessagesOfTicketListener;
    private OnRefreshMessagesOfTicketListener onRefreshMessagesOfTicketListener;
    private OnSendNewMessageListener onSendNewMessageListener;

    public void setOnSendNewMessageListener(OnSendNewMessageListener onSendNewMessageListener)
    {
        this.onSendNewMessageListener = onSendNewMessageListener;
    }

    public void setOnRefreshMessagesOfTicketListener(OnRefreshMessagesOfTicketListener onRefreshMessagesOfTicketListener)
    {
        this.onRefreshMessagesOfTicketListener = onRefreshMessagesOfTicketListener;
    }

    public void setOnCreateNewTicketMessageListener(OnCreateNewTicketMessageListener onCreateNewTicketMessageListener)
    {
        this.onCreateNewTicketMessageListener = onCreateNewTicketMessageListener;
    }

    public void setOnLoadTicketHistoryListener(OnLoadTicketHistoryListener onLoadTicketHistoryListener)
    {
        this.onLoadTicketHistoryListener = onLoadTicketHistoryListener;
    }

    public void setOnGetStoreOwnerUserIdListener(OnGetStoreOwnerUserIdListener onGetStoreOwnerUserIdListener)
    {
        this.onGetStoreOwnerUserIdListener = onGetStoreOwnerUserIdListener;
    }

    public void setOnGetMessagesOfTicketListener(OnGetMessagesOfTicketListener onGetMessagesOfTicketListener)
    {
        this.onGetMessagesOfTicketListener = onGetMessagesOfTicketListener;
    }

    public MessagingModule(Context context)
    {
        this.context = context;
        this.requestApi = ApiServiceGenerator.GetApiService();
    }

    public void SetOnFailureLoadDataListener(OnFailureDataLoadListener onFailureLoadDataListener)
    {
        this.onFailureLoadDataListener = onFailureLoadDataListener;
    }

    public void GetStoreOwnerUserId()
    {
        Call<List<StoreOwnerApiModel>> GetStoreOwnerID = requestApi.GetStoreOwnerID();
        GetStoreOwnerID.enqueue(new ApiCallBack<List<StoreOwnerApiModel>>(context, GetStoreOwnerID)
        {
            @Override
            public void onResponse(Call<List<StoreOwnerApiModel>> call, Response<List<StoreOwnerApiModel>> response)
            {
                onGetStoreOwnerUserIdListener.OnGetStoreOwnerUserId(response.body().get(0).UserID);
            }

            @Override
            public void onFailure(@NonNull Call<List<StoreOwnerApiModel>> call, @NonNull Throwable t)
            {
                onFailureLoadDataListener.OnFailureLoadData(t);
                super.onFailure(call, t);
            }
        });
    }

    public void GetTicketsHistory(int UserId, int MessageOffsetNumber)
    {

        Call<List<GetMessagesApiModel>> GetMessages = requestApi.GetMessages(UserId, MessageOffsetNumber);
        GetMessages.enqueue(new ApiCallBack<List<GetMessagesApiModel>>(context, GetMessages)
        {
            @Override
            public void onResponse(Call<List<GetMessagesApiModel>> call, Response<List<GetMessagesApiModel>> response)
            {
                ArrayList<GetMessagesApiModel> DataList = new ArrayList<>(response.body()!=null ? response.body():new ArrayList<GetMessagesApiModel>());
                onLoadTicketHistoryListener.OnLoadTicketHistory(DataList);
            }

            @Override
            public void onFailure(@NonNull Call<List<GetMessagesApiModel>> call, @NonNull Throwable t)
            {
                onFailureLoadDataListener.OnFailureLoadData(t);
                super.onFailure(call, t);
            }
        });

    }

    public void CreateNewTicketMessage(String MessageTitle, String MessageText, int AdminId, int UserId)
    {
        Call<Boolean> CreateNewMessageTicket = requestApi.CreateNewMessageTicket(MessageTitle, MessageText, AdminId, UserId);
        CreateNewMessageTicket.enqueue(new ApiCallBack<Boolean>(context, CreateNewMessageTicket)
        {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response)
            {
                onCreateNewTicketMessageListener.OnCreateNewTicketMessage(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t)
            {
                onFailureLoadDataListener.OnFailureLoadData(t);
                super.onFailure(call, t);
            }
        });
    }

    public void GetMessagesOfTicket(int MessageID, int MessageOffsetNumber)
    {
        Call<List<MessageListApiModel>> GetMessageList = requestApi.GetMessageListByMessageId(MessageID, MessageOffsetNumber);
        GetMessageList.enqueue(new ApiCallBack<List<MessageListApiModel>>(context, GetMessageList)
        {
            @Override
            public void onResponse(Call<List<MessageListApiModel>> call, Response<List<MessageListApiModel>> response)
            {
                ArrayList<MessageListApiModel> DataList = new ArrayList<>(response.body()!=null ? response.body():new ArrayList<MessageListApiModel>());
                onGetMessagesOfTicketListener.GetMessagesOfTicket(DataList);
            }

            @Override
            public void onFailure(@NonNull Call<List<MessageListApiModel>> call, @NonNull Throwable t)
            {
                onFailureLoadDataListener.OnFailureLoadData(t);
                super.onFailure(call, t);
            }
        });
    }

    public void SendNewMessage(int MessageID, String RespondText, int UserId, int RespondState)
    {
        Call<Integer> RespondToMessage = requestApi.ResponseToTicketMessage(MessageID, RespondText, UserId, RespondState);
        RespondToMessage.enqueue(new ApiCallBack<Integer>(context, RespondToMessage)
        {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response)
            {
                onSendNewMessageListener.OnSendNewMessage(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t)
            {
                onFailureLoadDataListener.OnFailureLoadData(t);
                super.onFailure(call, t);
            }
        });
    }

    public void RefreshMessagesOfTicket(int MessageID)
    {
        Call<List<MessageListApiModel>> GetMessageList = requestApi.GetMessageListByMessageId(MessageID, 0);
        GetMessageList.enqueue(new ApiCallBack<List<MessageListApiModel>>(context, GetMessageList)
        {
            @Override
            public void onResponse(Call<List<MessageListApiModel>> call, Response<List<MessageListApiModel>> response)
            {
                ArrayList<MessageListApiModel> DataList = new ArrayList<>(response.body()!=null ? response.body():new ArrayList<MessageListApiModel>());
                onRefreshMessagesOfTicketListener.RefreshMessagesOfTicket(DataList);
            }

            @Override
            public void onFailure(@NonNull Call<List<MessageListApiModel>> call, @NonNull Throwable t)
            {
                onFailureLoadDataListener.OnFailureLoadData(t);
                super.onFailure(call, t);
            }
        });
    }

    //------------------------------------------------------------------------Interfaces
    public interface OnGetStoreOwnerUserIdListener
    {
        void OnGetStoreOwnerUserId(int StoreOwnerUserId);
    }

    public interface OnLoadTicketHistoryListener
    {
        void OnLoadTicketHistory(ArrayList<GetMessagesApiModel> TicketHistoryData);
    }

    public interface OnCreateNewTicketMessageListener
    {
        void OnCreateNewTicketMessage(Boolean MessageTicketSendState);
    }

    public interface OnGetMessagesOfTicketListener
    {
        void GetMessagesOfTicket(ArrayList<MessageListApiModel> MessagesOfTicketData);
    }

    public interface OnRefreshMessagesOfTicketListener
    {
        void RefreshMessagesOfTicket(ArrayList<MessageListApiModel> RefreshedMessagesOfTicketData);
    }

    public interface OnSendNewMessageListener
    {
        void OnSendNewMessage(int SendState);
    }
}
