package com.jullae.ui.home.profile.bookmarkTab;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.jullae.R;
import com.jullae.data.db.model.FeedModel;
import com.jullae.data.db.model.PictureModel;
import com.jullae.data.db.model.StoryModel;
import com.jullae.ui.home.profile.draftTab.DraftTabAdapter;
import com.jullae.utils.AppUtils;
import com.jullae.utils.Constants;
import com.jullae.utils.GlideUtils;
import com.jullae.utils.MyProgressDialog;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by master on 1/5/17.
 */

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder> {

    private static final String TAG = BookmarkAdapter.class.getName();
    private final Activity mContext;
    private final RequestOptions picOptions;
    private final BookmarkTabPresentor mPresentor;

    List<FeedModel> messagelist = new ArrayList<FeedModel>();

    public BookmarkAdapter(Activity activity, BookmarkTabPresentor mPresentor) {
        this.mContext = activity;
        this.mPresentor = mPresentor;

        picOptions = new RequestOptions();
        picOptions.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
    }

    @NonNull
    @Override
    public BookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BookmarkViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bookmark, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarkViewHolder viewHolder, int position) {
        PictureModel pictureModel = messagelist.get(position).getPictureModel();
        StoryModel storyModel = messagelist.get(position).getStoryModel();

        GlideUtils.loadImagefromUrl(mContext, pictureModel.getPicture_url_small(), viewHolder.image);
        //  private Stringth(mContext).load(pictureModel.getPicture_url_small()).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(viewHolder.image);
        viewHolder.story_like_count.setText(storyModel.getLike_count() + " likes");
        viewHolder.story_comment_count.setText(storyModel.getComment_count() + " comments");
        viewHolder.story_title.setText(storyModel.getStory_title());
        viewHolder.story_text.setText(storyModel.getStory_text());

    }


    @Override
    public int getItemCount() {
        return messagelist.size();
    }

    public void add(List<FeedModel> list) {
        messagelist.clear();
        messagelist.addAll(list);
        Log.d(TAG, "add: list size: " + list.size());
        notifyDataSetChanged();
    }

    public void addMore(List<FeedModel> feedModel) {
        int initialSize = messagelist.size();
        messagelist.addAll(feedModel);
        int finalSize = messagelist.size();
        notifyItemRangeInserted(initialSize, finalSize - initialSize);
    }

    private void showDeleteDraftWarningDialog(final int adapterPosition, final String story_id) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Remove bookmark?");
        alertDialog.setMessage("Are you sure you want to remove this bookmark?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                MyProgressDialog.showProgressDialog(mContext, "Please Wait!");
                mPresentor.sendDeleteBookmarkReq(story_id, new DraftTabAdapter.DeleteListener() {
                    @Override
                    public void onSuccess() {
                        MyProgressDialog.dismissProgressDialog();
                        Toast.makeText(mContext.getApplicationContext(), "Bookmark removed successfully", Toast.LENGTH_SHORT).show();
                        messagelist.remove(adapterPosition);
                        notifyItemRemoved(adapterPosition);
                    }

                    @Override
                    public void onFail() {
                        MyProgressDialog.dismissProgressDialog();

                        Toast.makeText(mContext.getApplicationContext(), "Something went wrong! Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();

    }

    interface DeleteListener {
        void onSuccess();

        void onFail();
    }

    public class BookmarkViewHolder extends RecyclerView.ViewHolder {


        private View rootview;
        private ImageView image;
        private TextView story_title, story_text;
        private TextView story_like_count;
        private TextView story_comment_count;

        public BookmarkViewHolder(View inflate) {
            super(inflate);

            image = inflate.findViewById(R.id.image);
            story_title = inflate.findViewById(R.id.story_title);
            story_text = inflate.findViewById(R.id.story_text);
            story_like_count = inflate.findViewById(R.id.story_like_count);
            story_comment_count = inflate.findViewById(R.id.story_comment_count);

            inflate.findViewById(R.id.text_delete_bookmark).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteDraftWarningDialog(getAdapterPosition(), messagelist.get(getAdapterPosition()).getStoryModel().getStory_id());
                }
            });
            inflate.findViewById(R.id.rootview).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtils.showStoryDetailActivity(mContext, messagelist.get(getAdapterPosition()).getStoryModel().getStory_id());

                }
            });
            inflate.findViewById(R.id.story_like_count).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (messagelist.get(getAdapterPosition()).getStoryModel().getLike_count() != 0)
                        AppUtils.showLikesDialog(mContext, messagelist.get(getAdapterPosition()).getStoryModel().getStory_id(), Constants.LIKE_TYPE_STORY);

                }
            });


            inflate.findViewById(R.id.image).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtils.showPictureDetailActivity(mContext, messagelist.get(getAdapterPosition()).getPictureModel().getPicture_id());
                }
            });

           /* image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtils.showFullPictureDialog(mContext, messagelist.get(getAdapterPosition()).getPictureModel(), new AppUtils.LikeClickListener() {
                        @Override
                        public void onLikeClick() {
                            Log.d(TAG, "onLikeClick: " + messagelist.get(getAdapterPosition()).getPictureModel().getIs_liked());
                            if (messagelist.get(getAdapterPosition()).getPictureModel().getIs_liked()) {
                                messagelist.get(getAdapterPosition()).getPictureModel().setIs_liked(false);
                                messagelist.get(getAdapterPosition()).getPictureModel().setDecrementLikeCount();
                            } else {
                                messagelist.get(getAdapterPosition()).getPictureModel().setIs_liked(true);
                                messagelist.get(getAdapterPosition()).getPictureModel().setIncrementLikeCount();
                            }
                            mPresentor.setLike(messagelist.get(getAdapterPosition()).getPictureModel().getPicture_id(), new HomeFeedAdapter.ReqListener() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onFail() {
                                    Toast.makeText(mContext.getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                                    if (messagelist.get(getAdapterPosition()).getPictureModel().getIs_liked()) {
                                        messagelist.get(getAdapterPosition()).getPictureModel().setIs_liked(false);
                                        messagelist.get(getAdapterPosition()).getPictureModel().setDecrementLikeCount();
                                    } else {
                                        messagelist.get(getAdapterPosition()).getPictureModel().setIs_liked(true);
                                        messagelist.get(getAdapterPosition()).getPictureModel().setIncrementLikeCount();
                                    }
                                }
                            }, !messagelist.get(getAdapterPosition()).getPictureModel().getIs_liked());


                        }
                    });
                }
            });*/
        }
    }
}
