package com.jullae.ui.home.homeFeed.freshfeed;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jullae.ApplicationClass;
import com.jullae.R;
import com.jullae.databinding.ItemHomeFeedBinding;
import com.jullae.ui.custom.ItemOffLRsetDecoration;
import com.jullae.ui.home.homeFeed.HomeFeedModel;
import com.jullae.ui.home.homeFeed.HomeFeedPresentor;
import com.jullae.ui.home.homeFeed.StoryAdapter;
import com.jullae.utils.AppUtils;
import com.jullae.utils.Constants;
import com.jullae.utils.DialogUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by master on 1/5/17.
 */

public class HomeFeedAdapter extends RecyclerView.Adapter<HomeFeedAdapter.HomeFeedViewHolder> {

    private static final String TAG = HomeFeedAdapter.class.getName();
    private final Activity mContext;
    private final HomeFeedPresentor mPresentor;
    private final float calculateoffset;

    List<HomeFeedModel.Feed> messagelist = new ArrayList<HomeFeedModel.Feed>();

    public HomeFeedAdapter(Activity activity, HomeFeedPresentor homeFeedPresentor) {
        this.mContext = activity;

        this.mPresentor = homeFeedPresentor;

        calculateoffset = AppUtils.convertdpTopx((((int) ((ApplicationClass) activity.getApplication()).getDpWidth()) / 2) - 132);
    }


    @NonNull
    @Override
    public HomeFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHomeFeedBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_home_feed, parent, false);
        return new HomeFeedViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeFeedViewHolder viewHolder, int position) {

        ((StoryAdapter) viewHolder.binding.recyclerViewStory.getAdapter()).setPictureId(messagelist.get(position).getPicture_id());
        ((StoryAdapter) viewHolder.binding.recyclerViewStory.getAdapter()).setPictureUrl(messagelist.get(position).getPicture_url());

        if (messagelist.get(position).getStories().size() != 0) {
            ((StoryAdapter) viewHolder.binding.recyclerViewStory.getAdapter()).add(messagelist.get(position).getStories());
            ((LinearLayoutManager) viewHolder.binding.recyclerViewStory.getLayoutManager()).scrollToPositionWithOffset(messagelist.get(position).getHighlightStoryIndex(), (int) calculateoffset);
        } else
            ((StoryAdapter) viewHolder.binding.recyclerViewStory.getAdapter()).addEmptyMessage(messagelist.get(position).getPicture_id(), messagelist.get(position).getPicture_url());

        viewHolder.binding.setFeed(messagelist.get(position));
        viewHolder.binding.executePendingBindings();


    }

    @Override
    public void onBindViewHolder(@NonNull HomeFeedViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty())
            holder.binding.executePendingBindings();
        else
            super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public int getItemCount() {
        return messagelist.size();
    }

    public void add(List<HomeFeedModel.Feed> list) {
        messagelist.clear();
        messagelist.addAll(list);
        Log.d(TAG, "add: list size: " + list.size());
        notifyDataSetChanged();
    }

    private void showMenuOptions(final int adapterPosition) {


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        View view;
        if (messagelist.get(adapterPosition).getIs_self() && messagelist.get(adapterPosition).getStory_count() == 0)
            view = mContext.getLayoutInflater().inflate(R.layout.picture_options_self, null);
        else view = mContext.getLayoutInflater().inflate(R.layout.picture_options, null);


        dialogBuilder.setView(view);

        final AlertDialog dialog = dialogBuilder.create();
        view.findViewById(R.id.menu1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteStoryDialog(adapterPosition);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();

                    }
                }, 100);


            }
        });
        view.findViewById(R.id.menu2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        view.findViewById(R.id.menu3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        dialog.show();

    }

    private void showDeleteStoryDialog(final int adapterPosition) {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
        builder1.setTitle("Delete Picture!");
        builder1.setMessage("Are you sure you want to delete this picture?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mPresentor.sendPictureDeleteReq(messagelist.get(adapterPosition).getPicture_id());
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();

    }

    private void changeLike(int adapterPosition) {
        if (messagelist.get(adapterPosition).getIs_liked()) {
            messagelist.get(adapterPosition).setIs_liked(false);
            messagelist.get(adapterPosition).setDecrementLikeCount();
        } else {
            messagelist.get(adapterPosition).setIs_liked(true);
            messagelist.get(adapterPosition).setIncrementLikeCount();
        }
        notifyItemChanged(adapterPosition, "like");

    }

    public void addMore(List<HomeFeedModel.Feed> feedList) {
        int intialSize = messagelist.size();
        messagelist.addAll(feedList);
        int finalSize = messagelist.size();

        notifyItemRangeInserted(intialSize, finalSize - intialSize);
    }

    public interface ReqListener {
        void onSuccess();

        void onFail();
    }

    class HomeFeedViewHolder extends RecyclerView.ViewHolder {


        private final ItemHomeFeedBinding binding;

        public HomeFeedViewHolder(ItemHomeFeedBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.ivMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //   showMenuOptions(getAdapterPosition());
                    DialogUtils.showPictureMoreOptions(mContext, mPresentor, messagelist.get(getAdapterPosition()));
                }
            });
            binding.textWriteStory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtils.showWriteStoryDialog(mContext, messagelist.get(getAdapterPosition()).getPicture_url(), messagelist.get(getAdapterPosition()).getPicture_id(), null, null);
                }
            });
            binding.recyclerViewStory.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));

            binding.recyclerViewStory.addItemDecoration(new ItemOffLRsetDecoration(mContext, R.dimen.item_offset_4dp));

            binding.recyclerViewStory.setAdapter(new StoryAdapter(mContext, null));

            binding.textPenname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtils.showVisitorProfile(mContext, messagelist.get(getAdapterPosition()).getPhotographer_penname());
                }
            });
            binding.userAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtils.showVisitorProfile(mContext, messagelist.get(getAdapterPosition()).getPhotographer_penname());
                }
            });


            binding.likeCount.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (messagelist.get(getAdapterPosition()).getLike_count() != 0)
                        AppUtils.showLikesDialog(mContext, messagelist.get(getAdapterPosition()).getPicture_id(), Constants.LIKE_TYPE_PICTURE);

                }
            });

            binding.buttonLike.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    changeLike(getAdapterPosition());
                    mPresentor.setLike(messagelist.get(getAdapterPosition()).getPicture_id(), new ReqListener() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onFail() {
                            Toast.makeText(mContext.getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                            changeLike(getAdapterPosition());
                        }

                    }, !messagelist.get(getAdapterPosition()).getIs_liked());


                }
            });
        }
    }


}
