package company.aryasoft.app.malayermarket.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import company.aryasoft.app.malayermarket.ApiModels.MessageListApiModel;
import company.aryasoft.app.malayermarket.Modules.OnFailureDataLoadListener;
import company.aryasoft.app.malayermarket.R;
import company.aryasoft.app.malayermarket.UtilityAndHelper.SharedPreferencesHelper;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MsgRecyclerViewHolder>
{
    private ArrayList<MessageListApiModel> _List;
    private int UserId = -1;
    private Context ViewContext ;

    public MessageAdapter(Context _Context)
    {
        this.ViewContext = _Context;
        this._List = new ArrayList<>();
        UserId = SharedPreferencesHelper.ReadInt("UserId");
    }

    @NonNull
    @Override
    public MsgRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View RecyclerItemView = LayoutInflater.from(ViewContext).inflate(R.layout.message_item_layout, parent, false);
        return new MessageAdapter.MsgRecyclerViewHolder(RecyclerItemView);
    }

    public void AddMessageListData(ArrayList<MessageListApiModel> _List)
    {
        this._List.addAll(_List);
        this.notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(MsgRecyclerViewHolder holder, int position)
    {
        //incoming message
        if (_List.get(position).UserIDSender != UserId)
        {
            holder.txtMessageText.setBackgroundResource(R.drawable.bubble_chat_style2);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT, android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.txtMessageText.setLayoutParams(layoutParams);
            holder.txtMessageText.setTextColor(Color.BLACK);
            holder.txtMessageText.setText(_List.get(position).MessageText);
            holder.txtMessageDate.setText( _List.get(position).SendDate);
        }
        //outgoing message
        else if (_List.get(position).UserIDSender == UserId)
        {
            holder.txtMessageText.setBackgroundResource(R.drawable.bubble_chat_style);
            //change view gravity
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT, android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.txtMessageText.setLayoutParams(layoutParams);
            holder.txtMessageText.setTextColor(Color.BLACK);
            holder.txtMessageText.setText(_List.get(position).MessageText);
            holder.txtMessageDate.setText( _List.get(position).SendDate);
        }
    }

    public void ClearAllMessages()
    {
        this._List.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount()
    {
        return _List.size();
    }



    class MsgRecyclerViewHolder extends RecyclerView.ViewHolder
    {
        TextView txtMessageText;
        TextView txtMessageDate;
        RelativeLayout relMessageContainer;
         MsgRecyclerViewHolder(View itemView)
        {
            super(itemView);
            txtMessageText = itemView.findViewById(R.id.txt_message_text);
            txtMessageDate = itemView.findViewById(R.id.txt_message_date);
            relMessageContainer = itemView.findViewById(R.id.rel_message_container);
        }
    }
}
