package company.aryasoft.app.malayermarket.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import company.aryasoft.app.malayermarket.ApiModels.ProductCommentApiModel;
import company.aryasoft.app.malayermarket.R;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder>
{
    private ArrayList<ProductCommentApiModel> CommentList = null;
    private Context _Context;

    public CommentsAdapter(Context context, ArrayList<ProductCommentApiModel> CommentList)
    {
        this.CommentList = CommentList;
        this._Context = context;
    }

    @Override
    public CommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item_layout, parent, false);
        return new CommentsViewHolder(item);
    }

    @Override
    public void onBindViewHolder(CommentsViewHolder holder, int position)
    {
        holder.txtUserName.setText("نظر دوستمون " + CommentList.get(position).NameFamily);
        holder.txtCommentText.setText("عنوان : " + CommentList.get(position).CommentsTitle + "\n\n" + CommentList.get(position).ProductCommentsText);
        holder.txt_comment_date.setText(CommentList.get(position).ProductCommentsDate );
        Glide.with(_Context).load(_Context.getResources().getString(R.string.UsersImageFolder) + CommentList.get(position).ImageName).into(holder.img_user_pic).onLoadFailed(_Context.getResources().getDrawable(R.drawable.user_profile));
    }

    @Override
    public int getItemCount()
    {
        return CommentList.size();
    }

    public void AddMoreComment(ArrayList<ProductCommentApiModel> CommentList)
    {
        this.CommentList.addAll(CommentList);
        notifyDataSetChanged();
    }

    //-------------------------VIEW HOLDER--------------------------

    class CommentsViewHolder extends RecyclerView.ViewHolder
    {
        private TextView txtUserName;
        private TextView txtCommentText;
        // private RatingBar rateScore;
        private CircleImageView img_user_pic = null;
        private TextView txt_comment_date= null;
        public CommentsViewHolder(View itemView)
        {
            super(itemView);
            //rateScore=(RatingBar) itemView.findViewById(R.id.rating_score);
            txtUserName = (TextView) itemView.findViewById(R.id.txt_user_name_comment);
            txtCommentText = (TextView) itemView.findViewById(R.id.txt_comment_text);
            img_user_pic = (CircleImageView) itemView.findViewById(R.id.img_user_pic);
            txt_comment_date= (TextView) itemView.findViewById(R.id.txt_comment_date);
        }
    }
//---------------------------------------------------------VIEW HOLDER------------------------------
}
