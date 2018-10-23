package company.aryasoft.app.malayermarket.Activities;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import java.util.ArrayList;
import cn.pedant.SweetAlert.SweetAlertDialog;
import company.aryasoft.app.malayermarket.Adapters.MessageAdapter;
import company.aryasoft.app.malayermarket.ApiModels.MessageListApiModel;
import company.aryasoft.app.malayermarket.ModifiedInterfaceAndAbstracts.ModifiedTextWatcher;
import company.aryasoft.app.malayermarket.Modules.MessagingModule;
import company.aryasoft.app.malayermarket.Modules.OnFailureDataLoadListener;
import company.aryasoft.app.malayermarket.R;
import company.aryasoft.app.malayermarket.UtilityAndHelper.SharedPreferencesHelper;
import retrofit2.Call;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MessageActivity extends AppCompatActivity implements OnFailureDataLoadListener
{
    private RecyclerView ChatListRecycler = null;
    private int MessageStateID;
    private int MessageID;
    private int UserId = -1;
    private ImageButton btn_send_respond = null;
    private EditText edt_respond_text = null;
    private int RespondState = 1;
    private MessageAdapter messageAdapter = null;
    private LinearLayoutManager layoutManager = null;
    private boolean IsLoading = false;
    private int MessageOffsetNumber = 0;
    private boolean IsMessageEnded = false;
    private SweetAlertDialog LoadingDialog = null;
    private MessagingModule messagingModule = null;

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        messagingModule = new MessagingModule(this);
        initStoredPreferences();
        initViews();
        initEvents();
        ///------------------
        LoadingDialog.show();
        GetMessagesOfTicket();
    }

    private void initStoredPreferences()
    {
        UserId = SharedPreferencesHelper.ReadInt("UserId");
        MessageStateID = getIntent().getIntExtra("MessageStateID", -1);
        MessageID = getIntent().getIntExtra("MessageID", -1);
    }

    private void initViews()
    {
        ChatListRecycler = findViewById(R.id.list_message);
        CardView rel_send_message = findViewById(R.id.rel_send_message);
        btn_send_respond = findViewById(R.id.btn_send_respond);
        edt_respond_text = findViewById(R.id.edt_respond_text);
        layoutManager = new LinearLayoutManager(MessageActivity.this, LinearLayoutManager.VERTICAL, false);
        messageAdapter = new MessageAdapter(MessageActivity.this);
        ChatListRecycler.setLayoutManager(layoutManager);
        ChatListRecycler.setAdapter(messageAdapter);
        LoadingDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        LoadingDialog.setTitleText(getString(R.string.loading_title));
        LoadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        LoadingDialog.setCancelable(false);
        rel_send_message.setMaxCardElevation(0);
        if (MessageStateID == 3)
        {

            edt_respond_text.setEnabled(false);
            btn_send_respond.setEnabled(false);
            edt_respond_text.setHint("این گفتگو بسته شده است.");
            edt_respond_text.setHintTextColor(Color.RED);
        }
        //----------------------------
    }

    private void initEvents()
    {
        messagingModule.SetOnFailureLoadDataListener(this);
        edt_respond_text.addTextChangedListener(new ModifiedTextWatcher()
        {
            @Override
            public void afterTextChanged(Editable s)
            {
                if (TextUtils.isEmpty(s.toString().trim()))
                {
                    btn_send_respond.setColorFilter(Color.parseColor("#757575"));
                }
                else
                {
                    btn_send_respond.setColorFilter(Color.parseColor("#2196F3"));
                }
            }
        });
        btn_send_respond.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!(TextUtils.isEmpty(edt_respond_text.getText().toString().trim())))
                {
                    LoadingDialog.show();
                    messagingModule.SendNewMessage(MessageID,edt_respond_text.getText().toString(),UserId,RespondState);
                    messagingModule.setOnSendNewMessageListener(new MessagingModule.OnSendNewMessageListener()
                    {
                        @Override
                        public void OnSendNewMessage(int SendState)
                        {
                            LoadingDialog.dismiss();
                            if (SendState >= 0)
                            {
                                Toast.makeText(MessageActivity.this, "پیام شما به مدیر ارسال شد.", Toast.LENGTH_SHORT).show();
                                edt_respond_text.setText("");
                                RefreshMessages();
                            }
                            else
                            {
                                Toast.makeText(MessageActivity.this, "خطا در ارسال پیام", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(MessageActivity.this, "لطفا پیام خود را وارد کنید.", Toast.LENGTH_LONG).show();
                }
            }
        });
        //-----------------
        ChatListRecycler.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (!IsMessageEnded)
                {
                    if (messageAdapter.getItemCount() >= 15)
                    {
                        if (IsLoading)
                        {
                            return;
                        }
                        LoadMoreMessageTicket();
                    }
                }
                if (messageAdapter.getItemCount() >= 15)
                {
                    if (IsLoading)
                    {
                        return;
                    }
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        //------------------------------
    }

    private void RefreshMessages()
    {
        messagingModule.RefreshMessagesOfTicket(MessageID);
        messagingModule.setOnRefreshMessagesOfTicketListener(new MessagingModule.OnRefreshMessagesOfTicketListener()
        {
            @Override
            public void RefreshMessagesOfTicket(ArrayList<MessageListApiModel> RefreshedMessagesOfTicketData)
            {
                if (RefreshedMessagesOfTicketData != null)
                {
                    messageAdapter.ClearAllMessages();
                    messageAdapter.AddMessageListData(RefreshedMessagesOfTicketData);
                    ChatListRecycler.smoothScrollToPosition(ChatListRecycler.getAdapter().getItemCount() - 1);
                }
                LoadingDialog.dismiss();
            }
        });
    }

    private void GetMessagesOfTicket()
    {
        messagingModule.GetMessagesOfTicket(MessageID, MessageOffsetNumber);
        messagingModule.setOnGetMessagesOfTicketListener(new MessagingModule.OnGetMessagesOfTicketListener()
        {
            @Override
            public void GetMessagesOfTicket(ArrayList<MessageListApiModel> MessagesOfTicketData)
            {
                if (MessagesOfTicketData != null)
                {
                    if (MessagesOfTicketData.size() > 0)
                    {
                        messageAdapter.AddMessageListData(MessagesOfTicketData);
                        IsMessageEnded = false;
                    }
                    else
                    {
                        IsMessageEnded = true;
                    }
                    IsLoading = false;
                }
                LoadingDialog.dismiss();
            }
        });
    }

    private void LoadMoreMessageTicket()
    {
        int VisibleItemCount = layoutManager.getChildCount();
        int TotalItemCount = layoutManager.getItemCount();
        int PastVisibleItem = layoutManager.findFirstVisibleItemPosition();
        if ((VisibleItemCount + PastVisibleItem) >= TotalItemCount)
        {
            if (!LoadingDialog.isShowing())
            {
                LoadingDialog.show();
            }
            IsLoading = true;
            MessageOffsetNumber += 15;
            GetMessagesOfTicket();
        }
    }

    @Override
    public void OnFailureLoadData(Throwable t)
    {
        LoadingDialog.dismiss();
        IsLoading = false;
        IsMessageEnded = false;
    }
}
