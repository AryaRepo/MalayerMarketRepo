package company.aryasoft.app.malayermarket.Activities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.util.ArrayList;
import cn.pedant.SweetAlert.SweetAlertDialog;
import company.aryasoft.app.malayermarket.Adapters.AllMessageAdapter;
import company.aryasoft.app.malayermarket.ApiModels.GetMessagesApiModel;
import company.aryasoft.app.malayermarket.Modules.MessagingModule;
import company.aryasoft.app.malayermarket.Modules.OnFailureDataLoadListener;
import company.aryasoft.app.malayermarket.R;
import company.aryasoft.app.malayermarket.UtilityAndHelper.SharedPreferencesHelper;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TicketsMessageActivity extends AppCompatActivity implements OnFailureDataLoadListener
{
    private RecyclerView RecyclerMessages = null;
    private AllMessageAdapter allMessageAdapter = null;
    private FloatingActionButton fab_new_message = null;
    private int AdminId = -1;
    private int UserId = -1;
    private Dialog MessagingDialogHolder;
    private boolean IsLoading = false;
    private LinearLayoutManager TicketLayoutManager = null;
    private int MessageOffsetNumber = 0;
    private boolean IsMessageEnd = false;
    private SweetAlertDialog LoadingDialog = null;
    private MessagingModule messagingModule;
    private LinearLayout lin_no_messages;

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_message);
        messagingModule = new MessagingModule(this);
        initViews();
        initEvents();
        GetStoreOwnerID();
    }


    private void initViews()
    {
        RecyclerMessages = findViewById(R.id.list_all_message);
        UserId = SharedPreferencesHelper.ReadInt("UserId");
        CardView card_titles = findViewById(R.id.card_titles);
        card_titles.setMaxCardElevation(0);
        fab_new_message = findViewById(R.id.fab_new_message);
        lin_no_messages = findViewById(R.id.lin_no_messages);
        allMessageAdapter = new AllMessageAdapter(TicketsMessageActivity.this);
        TicketLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        RecyclerMessages.setLayoutManager(TicketLayoutManager);
        DividerItemDecoration itemDecorator = new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL);
        itemDecorator.setDrawable(getResources().getDrawable(R.drawable.line_divider));
        RecyclerMessages.setAdapter(allMessageAdapter);
        RecyclerMessages.addItemDecoration(itemDecorator);
        LoadingDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        LoadingDialog.setTitleText(getString(R.string.loading_title));
        LoadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        LoadingDialog.setCancelable(false);
    }

    private void initEvents()
    {
        messagingModule.SetOnFailureLoadDataListener(this);
        fab_new_message.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CreateNewTicketMessageDialog();
            }
        });
        //------------------
        RecyclerMessages.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy)
            {
                if (!IsMessageEnd)
                {
                    if (allMessageAdapter.getItemCount() >= 15)
                    {
                        if (IsLoading)
                        {
                            return;
                        }
                        LoadMoreMessages();
                    }
                    super.onScrolled(recyclerView, dx, dy);
                }
            }
        });
        //------------------------
    }

    private void CreateNewTicketMessageDialog()
    {
        android.support.v7.app.AlertDialog.Builder MessageDialogAlert = new android.support.v7.app.AlertDialog.Builder(TicketsMessageActivity.this);
        View AlertView = View.inflate(TicketsMessageActivity.this, R.layout.create_message_dlg_layout, null);
        final EditText edt_msg_title = AlertView.findViewById(R.id.edt_msg_title);
        final EditText edt_msg_text = AlertView.findViewById(R.id.edt_msg_text);
        final Button btn_send_msg = AlertView.findViewById(R.id.btn_send_msg);
        btn_send_msg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!(edt_msg_title.getText().toString().isEmpty() || edt_msg_text.getText().toString().isEmpty()))
                {
                    LoadingDialog.show();
                    messagingModule.CreateNewTicketMessage(edt_msg_title.getText().toString(), edt_msg_text.getText().toString(), AdminId, UserId);
                    messagingModule.setOnCreateNewTicketMessageListener(new MessagingModule.OnCreateNewTicketMessageListener()
                    {
                        @Override
                        public void OnCreateNewTicketMessage(Boolean MessageSendState)
                        {
                            if (MessageSendState == null)
                            {
                                Toast.makeText(TicketsMessageActivity.this, "مشکل در ارسال پیام برای پشتیبانی", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (MessageSendState)
                            {
                                MessagingDialogHolder.dismiss();
                                allMessageAdapter.GetMessageData().clear();
                                allMessageAdapter.notifyDataSetChanged();
                                MessageOffsetNumber = 0;
                                GetTickets();
                            }
                            else
                            {
                                Toast.makeText(TicketsMessageActivity.this, "مشکل در ارسال پیام برای پشتیبانی", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(TicketsMessageActivity.this, "لطفا موضوع پیام یا متن پیام را وارد کنید.", Toast.LENGTH_LONG).show();
                }
            }
        });
        MessageDialogAlert.setView(AlertView);
        MessagingDialogHolder = MessageDialogAlert.show();
    }

    private void LoadMoreMessages()
    {
        int VisibleItemCount = TicketLayoutManager.getChildCount();
        int TotalItemCount = TicketLayoutManager.getItemCount();
        int PastVisibleItem = TicketLayoutManager.findFirstVisibleItemPosition();
        if ((VisibleItemCount + PastVisibleItem) >= TotalItemCount)
        {
            if (!LoadingDialog.isShowing())
            {
                LoadingDialog.show();
            }
            IsLoading = true;
            MessageOffsetNumber += 15;
            GetTickets();
        }
    }

    private void GetStoreOwnerID()
    {
        LoadingDialog.show();
        messagingModule.GetStoreOwnerUserId();
        messagingModule.setOnGetStoreOwnerUserIdListener(new MessagingModule.OnGetStoreOwnerUserIdListener()
        {
            @Override
            public void OnGetStoreOwnerUserId(int StoreOwnerUserId)
            {
                AdminId = StoreOwnerUserId;
                GetTickets();
            }
        });
    }

    private void GetTickets()
    {
        messagingModule.GetTicketsHistory(UserId, MessageOffsetNumber);
        messagingModule.setOnLoadTicketHistoryListener(new MessagingModule.OnLoadTicketHistoryListener()
        {
            @Override
            public void OnLoadTicketHistory(ArrayList<GetMessagesApiModel> TicketHistoryData)
            {
                if (TicketHistoryData != null)
                {
                    if (TicketHistoryData.size() > 0)
                    {
                        allMessageAdapter.AddMessageData(TicketHistoryData);
                        RecyclerMessages.setVisibility(View.VISIBLE);
                        lin_no_messages.setVisibility(View.GONE);
                    }
                    else
                    {
                        IsMessageEnd = true;
                        RecyclerMessages.setVisibility(View.GONE);
                        lin_no_messages.setVisibility(View.VISIBLE);
                    }
                }
                IsLoading = false;
                LoadingDialog.dismiss();
            }
        });
    }


    @Override
    public void OnFailureLoadData(Throwable t)
    {
        LoadingDialog.dismiss();
        IsLoading = false;
        IsMessageEnd = false;
    }
}
