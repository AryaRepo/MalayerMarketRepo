package company.aryasoft.app.malayermarket.Fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.hsalf.smilerating.BaseRating;
import java.util.ArrayList;
import cn.pedant.SweetAlert.SweetAlertDialog;
import company.aryasoft.app.malayermarket.Adapters.CommentsAdapter;
import company.aryasoft.app.malayermarket.ApiModels.ProductCommentApiModel;
import company.aryasoft.app.malayermarket.Models.CommentModel;
import company.aryasoft.app.malayermarket.Modules.DetailProductModule;
import company.aryasoft.app.malayermarket.Modules.OnFailureDataLoadListener;
import company.aryasoft.app.malayermarket.R;
import company.aryasoft.app.malayermarket.UtilityAndHelper.HelperModule;
import company.aryasoft.app.malayermarket.UtilityAndHelper.SharedPreferencesHelper;
import company.aryasoft.app.malayermarket.UtilityAndHelper.VectorConfig;
import company.aryasoft.app.malayermarket.UtilityAndHelper.VectorsModel;


public class CommentsFragment extends Fragment implements OnFailureDataLoadListener
{
    private RecyclerView rvComments;
    private FloatingActionButton fabWriteComment;
    private ArrayList<ProductCommentApiModel> CommentList = null;
    private int ProductId = -1;
    private int UserId = -1;
    private CommentsAdapter commentsAdapter = null;
    private LinearLayoutManager CommentLayoutManager = null;
    private boolean IsLoading = false;
    private int CommentOffsetNumber = 0;
    private boolean IsCommentEnded = false;
    private SweetAlertDialog LoadingDialog = null;
    private DetailProductModule detailProductModule;
    private Dialog CommentHolderDialog = null;
    public static CommentsFragment newInstance(ArrayList<ProductCommentApiModel> CommentList, int ProductId)
    {
        CommentsFragment CommentsFragmentInstance = new CommentsFragment();
        CommentsFragmentInstance.CommentList = CommentList;
        CommentsFragmentInstance.ProductId = ProductId;
        return CommentsFragmentInstance;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_comments, container, false);
        SharedPreferencesHelper.InitPreferences(view.getContext());
        UserId = SharedPreferencesHelper.ReadInt("UserId");
        LoadingDialog = new SweetAlertDialog(view.getContext(), SweetAlertDialog.PROGRESS_TYPE);
        LoadingDialog.setTitleText(getString(R.string.loading_title));
        LoadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        LoadingDialog.setCancelable(false);
        detailProductModule = new DetailProductModule(view.getContext());
        detailProductModule.setOnFailureDataLoadListener(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        initViews(view);
        setupRecyclerView(rvComments);
        fabWriteComment.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (UserId != -1)
                {
                    ShowCommentDialog();
                }
                else
                {
                    HelperModule.ShowSignInAlert(v.getContext());
                }
            }
        });
    }


    private void initViews(View v)
    {
        rvComments = getActivity().findViewById(R.id.rec_comments);
        fabWriteComment = getActivity().findViewById(R.id.fab_write_comment);
    }
    private void ShowCommentDialog()
    {
        android.support.v7.app.AlertDialog.Builder CommentAlert = new android.support.v7.app.AlertDialog.Builder(getContext());
        CommentHolderDialog = CommentAlert.create();
        CommentAlert.setCancelable(true);
        View CommentAlertView = View.inflate(getContext(), R.layout.dialog_comment_layout, null);
        CommentAlert.setView(CommentAlertView);
        final Button btnSubmitComment = CommentAlertView.findViewById(R.id.btn_submit_comment);
        final EditText edt_comment_title = CommentAlertView.findViewById(R.id.edt_comment_title);
        final EditText edtComment = CommentAlertView.findViewById(R.id.edt_comment);
        ArrayList<VectorsModel> vectorsList = new ArrayList<>();
        vectorsList.add(new VectorsModel(R.drawable.send, btnSubmitComment, VectorConfig.MyDirType.end));
        VectorConfig.SetVectors(getContext().getResources(), vectorsList);
        final com.hsalf.smilerating.SmileRating ratingBarSetScore = CommentAlertView.findViewById(R.id.rating_set_score);
        ratingBarSetScore.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/iransans.ttf"));
        //==========================================================================
        ratingBarSetScore.setNameForSmile(BaseRating.TERRIBLE, "افتضاح");
        ratingBarSetScore.setNameForSmile(BaseRating.BAD, "بد");
        ratingBarSetScore.setNameForSmile(BaseRating.OKAY, "متوسط");
        ratingBarSetScore.setNameForSmile(BaseRating.GOOD, "خوب");
        ratingBarSetScore.setNameForSmile(BaseRating.GREAT, "عالی");
        //-----------------------------------
        btnSubmitComment.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (UserId != -1)
                {
                    if (HelperModule.IsEditTextEmpty(edt_comment_title))
                    {
                        YoYo.with(Techniques.Shake).duration(400).interpolate(new DecelerateInterpolator()).playOn(edt_comment_title);
                        Toast.makeText(getContext(), "عنوان نظر وارد نشده", Toast.LENGTH_SHORT).show();
                    }
                    else if (HelperModule.IsEditTextEmpty(edtComment))
                    {
                        YoYo.with(Techniques.Shake).duration(400).interpolate(new DecelerateInterpolator()).playOn(edtComment);
                        Toast.makeText(getContext(), "متن نظر وارد نشده", Toast.LENGTH_SHORT).show();
                    }
                    else if (ratingBarSetScore.getRating() == 0)
                    {
                        YoYo.with(Techniques.Shake).duration(400).interpolate(new DecelerateInterpolator()).playOn(ratingBarSetScore);
                        Toast.makeText(getContext(), "میزان امتیاز انتخاب نشده.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        LoadingDialog.show();
                        CommentModel commentModel=new CommentModel();
                        commentModel.UserId=UserId;
                        commentModel.CommentTitle=edt_comment_title.getText().toString();
                        commentModel.CommentText=edtComment.getText().toString();
                        commentModel.PointRate=ratingBarSetScore.getRating();
                        commentModel.ProductId=ProductId;
                        detailProductModule.RegisterProductComment(commentModel);
                        detailProductModule.setOnRegisterProductCommentListener(new DetailProductModule.OnRegisterProductCommentListener()
                        {
                            @Override
                            public void OnRegisterProductComment(Integer RegisterProductCommentState)
                            {
                                LoadingDialog.dismiss();
                                CommentHolderDialog.dismiss();
                                if (RegisterProductCommentState >= 0)
                                {
                                    Toast.makeText(getContext(), "کاربر گرامی نظر شما در سیستم ثبت شد.\n و پس از تایید نمایش داده خواهد شد.", Toast.LENGTH_LONG).show();
                                }
                                else
                                {
                                    Toast.makeText(getContext(), "کاربر گرامی ثبت نظر شما در سیستم با مشکل مواجه شد.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
                else
                {
                    HelperModule.ShowSignInAlert(v.getContext());
                }

            }
        });
        //--------------------
        CommentHolderDialog = CommentAlert.show();
    }

    private void setupRecyclerView(RecyclerView recyclerView)
    {
        CommentLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        commentsAdapter = new CommentsAdapter(getActivity(), CommentList);
        recyclerView.setLayoutManager(CommentLayoutManager);
        recyclerView.setAdapter(commentsAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (!IsCommentEnded)
                {
                    if (commentsAdapter.getItemCount() >= 15)
                    {
                        if (IsLoading)
                        {
                            return;
                        }
                        int VisibleItemCount = CommentLayoutManager.getChildCount();
                        int TotalItemCount = CommentLayoutManager.getItemCount();
                        int PastVisibleItem = CommentLayoutManager.findFirstVisibleItemPosition();
                        if ((VisibleItemCount + PastVisibleItem) >= TotalItemCount)
                        {
                            if (!LoadingDialog.isShowing())
                            {
                                LoadingDialog.show();
                            }
                            IsLoading = true;
                            CommentOffsetNumber += 15;
                            LoadMoreComments();
                        }
                        super.onScrolled(recyclerView, dx, dy);
                    }
                }
            }
        });
    }

    private void LoadMoreComments()
    {
        detailProductModule.GetProductComment(ProductId, CommentOffsetNumber);
        detailProductModule.setOnGetProductCommentListener(new DetailProductModule.OnGetProductCommentListener()
        {
            @Override
            public void OnGetProductComment(ArrayList<ProductCommentApiModel> GetProductCommentData)
            {
                if (GetProductCommentData.size() > 0)
                {
                    commentsAdapter.AddMoreComment(GetProductCommentData);
                    IsCommentEnded = false;
                }
                else
                {
                    IsCommentEnded = true;
                }
                IsLoading = false;
                LoadingDialog.dismiss();
            }
        });
        //----------------------------------------------------
    }
    @Override
    public void OnFailureLoadData(Throwable t)
    {
        if (LoadingDialog.isShowing())
        {
            LoadingDialog.dismiss();
        }
    }
}
