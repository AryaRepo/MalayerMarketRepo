package company.aryasoft.app.malayermarket.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import company.aryasoft.app.malayermarket.Activities.MessageActivity;
import company.aryasoft.app.malayermarket.ApiModels.GetMessagesApiModel;
import company.aryasoft.app.malayermarket.Modules.MessagingModule;
import company.aryasoft.app.malayermarket.Modules.OnFailureDataLoadListener;
import company.aryasoft.app.malayermarket.R;
import company.aryasoft.app.malayermarket.UtilityAndHelper.HelperModule;
import company.aryasoft.app.malayermarket.UtilityAndHelper.SharedPreferencesHelper;
import retrofit2.http.HEAD;


public class AllMessageAdapter extends RecyclerView.Adapter<AllMessageAdapter.MsgRecyclerViewHolder> implements OnFailureDataLoadListener
{
    private ArrayList<GetMessagesApiModel> _List;
    private Context ViewContext;
    private MessagingModule messagingModule;
    private SweetAlertDialog LoadingDialog;

    public AllMessageAdapter(Context _Context)
    {
        this.ViewContext = _Context;
        this._List = new ArrayList<>();
        this.messagingModule = new MessagingModule(_Context);
        this.messagingModule.SetOnFailureLoadDataListener(this);
        LoadingDialog = new SweetAlertDialog(_Context, SweetAlertDialog.PROGRESS_TYPE);
        LoadingDialog.setTitleText(_Context.getString(R.string.loading_title));
        LoadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        LoadingDialog.setCancelable(false);
    }

    @NonNull
    @Override
    public MsgRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View RecyclerItemView = LayoutInflater.from(ViewContext).inflate(R.layout.tickets_item_view, parent, false);
        return new AllMessageAdapter.MsgRecyclerViewHolder(RecyclerItemView);
    }

    public void AddMessageData(ArrayList<GetMessagesApiModel> _List)
    {
        this._List.addAll(_List);
        this.notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull final MsgRecyclerViewHolder holder, int position)
    {
        holder.txtTicketTitle.setText("عنوان پیام : " + _List.get(position).MessageTitle);
        holder.txtTicketSentDate.setText(_List.get(position).LastSendDate);
        holder.txtTicketState.setText("وضعیت : " + _List.get(position).MessageStateTitle);
        switch (_List.get(position).MessageStateID)
        {
            case 1:
                holder.txtTicketState.setTextColor(Color.parseColor("#FF9800"));
                break;
            case 2:
                holder.txtTicketState.setTextColor(Color.parseColor("#388E3C"));
                break;
            case 3:
                holder.txtTicketState.setTextColor(Color.parseColor("#FE1743"));
                break;
        }
        holder.btnViewTicket.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ViewContext.startActivity(new Intent(ViewContext, MessageActivity.class).putExtra("MessageStateID", _List.get(holder.getAdapterPosition()).MessageStateID).putExtra("MessageID", _List.get(holder.getAdapterPosition()).MessageID));
            }
        });
        //---------------------------------
        holder.btnCloseTicket.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(_List.get(holder.getAdapterPosition()).MessageStateID==3)
                {
                    Toast.makeText(ViewContext, "این گفتگو قبلا بسته شده است.", Toast.LENGTH_SHORT).show();
                    return;
                }
                AlertDialog.Builder closeTicketDialog = new AlertDialog.Builder(v.getContext());
                closeTicketDialog.setCancelable(false);
                closeTicketDialog.setTitle("بستن تیکت ");
                closeTicketDialog.setMessage("کاربر گرامی آیا قصد دارید این گفتگو را ادامه ندهید؟ \n\n با تایید شما این گفتگو برای همیشه بسته خواهد شد!");
                closeTicketDialog.setPositiveButton("بله موافقم", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        CloseTicket(SharedPreferencesHelper.ReadInt("UserId"), holder.getAdapterPosition());
                        dialog.dismiss();
                    }
                });
                closeTicketDialog.setNegativeButton("خیر ", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
                closeTicketDialog.show();
            }
        });
    }

    private void CloseTicket(int userId, final int position)
    {
        LoadingDialog.show();
        messagingModule.SendNewMessage(_List.get(position).MessageID, "این پیام بسته شده.", userId, 3);
        messagingModule.setOnSendNewMessageListener(new MessagingModule.OnSendNewMessageListener()
        {
            @Override
            public void OnSendNewMessage(int SendState)
            {
                if (SendState >= 0)
                {
                    Toast.makeText(ViewContext, "گفتگو شما با مدیر پشتیبانی بسته شد.", Toast.LENGTH_SHORT).show();
                    GetMessagesApiModel currentTicket = _List.get(position);
                    currentTicket.MessageStateTitle = "بسته شده";
                    _List.set(position, currentTicket);
                    notifyDataSetChanged();
                }
                else
                {
                    Toast.makeText(ViewContext, "خطا در ارسال پیام", Toast.LENGTH_SHORT).show();
                }
                LoadingDialog.dismiss();
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return _List.size();
    }

    public ArrayList<GetMessagesApiModel> GetMessageData()
    {
        return _List;
    }

    @Override
    public void OnFailureLoadData(Throwable t)
    {
        LoadingDialog.dismiss();
        Toast.makeText(ViewContext, "خطا در بستن تیکت !", Toast.LENGTH_SHORT).show();
    }

    class MsgRecyclerViewHolder extends RecyclerView.ViewHolder
    {
        TextView txtTicketSentDate;
        TextView txtTicketTitle;
        TextView txtTicketState;
        Button btnViewTicket;
        Button btnCloseTicket;

        MsgRecyclerViewHolder(View itemView)
        {
            super(itemView);
            txtTicketTitle = itemView.findViewById(R.id.txt_ticket_title);
            txtTicketSentDate = itemView.findViewById(R.id.txt_ticket_sent_date);
            txtTicketState = itemView.findViewById(R.id.txt_ticket_state);
            btnViewTicket = itemView.findViewById(R.id.btn_view_ticket);
            btnCloseTicket = itemView.findViewById(R.id.btn_close_ticket);
        }

    }
}
