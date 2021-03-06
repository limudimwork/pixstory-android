package com.jullae.ui.home.profile.profileVisitor;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.jullae.R;
import com.jullae.data.AppDataManager;
import com.jullae.data.db.model.ProfileModel;
import com.jullae.data.db.model.UserPrefsModel;
import com.jullae.databinding.FragmentProfileBinding;
import com.jullae.ui.base.BaseFragment;
import com.jullae.ui.base.BaseResponseModel;
import com.jullae.ui.home.profile.pictureTab.PictureTabFragment;
import com.jullae.ui.home.profile.storyTab.StoryTabFragment;
import com.jullae.utils.DialogUtils;
import com.jullae.utils.NetworkUtils;
import com.jullae.utils.ToastUtils;

/**
 * Created by Rahul Abrol on 12/26/17.
 * <p>
 * Profile fragment.
 */

public class ProfileVisitorFragment extends BaseFragment implements ProfileVisitorView {


    private static final String TAG = ProfileVisitorFragment.class.getName();
    private View view;
    private ImageView user_image;
    private TextView user_name, user_penname, user_bio, user_followers, user_following, user_stories, user_pictures;
    private ProfileVisitorPresentor mPresentor;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private TabLayout tabLayout;
    private UserPrefsModel userPrefsModel;
    private String penname;
    private FragmentProfileBinding binding;
    private ProfileModel mProfileModel;
    //private View button_message;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        if (view != null) {
            if (view.getParent() != null)
                ((ViewGroup) view.getParent()).removeView(view);
            return view;
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        view = binding.getRoot();

        penname = getArguments().getString("penname");

        user_image = view.findViewById(R.id.user_avatar);
        user_name = view.findViewById(R.id.user_name);
        user_penname = view.findViewById(R.id.text_penname);
        user_bio = view.findViewById(R.id.user_bio);
        user_followers = view.findViewById(R.id.text_followers);
        user_following = view.findViewById(R.id.text_following);
        user_stories = view.findViewById(R.id.text_stories);
        user_pictures = view.findViewById(R.id.text_pictures);

        view.findViewById(R.id.backcontainer).setVisibility(View.VISIBLE);
        view.findViewById(R.id.close1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getmContext().finish();
            }
        });

        view.findViewById(R.id.button_edit_profile).setVisibility(View.GONE);
        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        pagerAdapter = new PagerAdapter(getChildFragmentManager());
        viewPager.setOffscreenPageLimit(3);

        view.findViewById(R.id.swiperefresh).setEnabled(false);
        viewPager.setAdapter(pagerAdapter);

        mPresentor = new ProfileVisitorPresentor();

        //button_message.setVisibility(View.INVISIBLE);

        LinearLayout close_container = (LinearLayout) inflater.inflate(R.layout.close_button, (CoordinatorLayout) view.findViewById(R.id.rootview), false);
        close_container.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getmContext().finish();
            }
        });
        setupTabIcons();

        view.findViewById(R.id.ivMore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.showProfileMoreOptions(getmContext(), mProfileModel, mPresentor);
            }
        });


        ((CoordinatorLayout) view.findViewById(R.id.rootview)).addView(close_container);

        binding.containerFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.showFollowersDialog(getmContext(), mProfileModel.getId());
            }
        });
        binding.containerFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.showFollowingDialog(getmContext(), mProfileModel.getId());
            }
        });

        binding.userFollowed.setVisibility(View.VISIBLE);
        binding.userFollowed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mProfileModel.getIs_followed()) {
                    mProfileModel.setIs_followed(true);
                } else {
                    mProfileModel.setIs_followed(false);
                }

                AppDataManager.getInstance().getmApiHelper().makeFollowReq(mProfileModel.getId(), !mProfileModel.getIs_followed()).getAsObject(BaseResponseModel.class, new ParsedRequestListener<BaseResponseModel>() {

                    @Override
                    public void onResponse(BaseResponseModel response) {
                        NetworkUtils.parseResponse(TAG, response);
                        if (mProfileModel.getIs_followed())
                            AppDataManager.getInstance().getmUserProfile().getValue().data.incrementFollowingCount();
                        else
                            AppDataManager.getInstance().getmUserProfile().getValue().data.decrementFollowingCount();

                    }

                    @Override
                    public void onError(ANError anError) {
                        NetworkUtils.parseError(TAG, anError);
                        ToastUtils.showNoInternetToast(getmContext());
                        if (!mProfileModel.getIs_followed()) {
                            mProfileModel.setIs_followed(true);
                        } else {
                            mProfileModel.setIs_followed(false);
                        }
                    }
                });
            }
        });
        return view;
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_outline_photo_24px_white);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_edit_white);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresentor.attachView(this);
        mPresentor.loadProfile(penname);
    }


    @Override
    public void onDestroyView() {
        mPresentor.detachView();
        super.onDestroyView();

    }

    @Override
    public void onProfileFetchFail() {

    }

    @Override
    public void onProfileFetchSuccess(final ProfileModel profileModel) {
        this.mProfileModel = profileModel;
        try {
          /*  GlideUtils.loadImagefromUrl(getmContext(), profileModel.getUser_avatar(), user_image);
            user_name.setText(profileModel.getName());
            user_penname.setText(profileModel.getPenname());
            user_bio.setText(profileModel.getBio());
            user_followers.setText(profileModel.getFollower_count());
            user_following.setText(profileModel.getFollowing_count());
            user_stories.setText(profileModel.getStories_count());
            user_pictures.setText(profileModel.getPictures_count());*/
            binding.setProfileModel(profileModel);

            //button_message.setVisibility(View.VISIBLE);
            /*button_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(getmContext(), MessageActivity.class);
                    i.putExtra("user_id", profileModel.getId());
                    i.putExtra("user_name", profileModel.getName());

                    getmContext().startActivity(i);
                }
            });*/
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private class PagerAdapter extends FragmentStatePagerAdapter {
        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();


        private PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putString("penname", penname);
            switch (position) {
                case 0:
                    PictureTabFragment pictureTabFragment = new PictureTabFragment();
                    pictureTabFragment.setArguments(bundle);
                    return pictureTabFragment;

                case 1:
                    StoryTabFragment storyTabFragment = new StoryTabFragment();
                    storyTabFragment.setArguments(bundle);
                    return storyTabFragment;

                default:
                    return null;
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }


        @Override
        public int getCount() {

            return 2;
        }


    }


}
