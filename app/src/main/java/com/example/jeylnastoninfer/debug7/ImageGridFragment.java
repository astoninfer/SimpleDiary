package com.example.jeylnastoninfer.debug7;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.jeylnastoninfer.debug7.auxUtils.graphicsUtils.NumericHelper;


public class ImageGridFragment extends Fragment implements AdapterView.OnItemClickListener{

    private float width;
    private float height;

    private int mFirstVisibleItem;
    private int mVisibleItemCount;
    private int totItemCount;

    private boolean isFirstEnter = true;
    private boolean called = false;

    private ImageAdapter mAdapter;
    private ImageLoader mImageLoader;

    GridView mGridView;

    private LayoutInflater mInflater;


    public final static Integer[] imageResIds = new Integer[]{

    };



    public ImageGridFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new ImageAdapter(getActivity());
        width = NumericHelper.getScreenWidth(getActivity()) / 3f;
        height = NumericHelper.dip2px(getActivity(), 90);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.main_display_layout, container, false);
        mGridView = (GridView) v.findViewById(R.id.main_display);
        //mGridView.setOnScrollListener(mAdapter.listener);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);
        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

        //TODO: register events here

    }

    private class ImageAdapter extends BaseAdapter {
        private final Context mContext;

        private AbsListView.OnScrollListener listener;

        public ImageAdapter(Context context) {
            super();
            mContext = context;
            mImageLoader = ImageLoader.getInstance(20, ImageLoader.Type.LIFO);
            mInflater = LayoutInflater.from(mContext);
            listener = new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    int p = mFirstVisibleItem + mVisibleItemCount;
                    int q = Math.min(p + 10, totItemCount);
                    for(int i = p; i < q; i++){
                        String tag = getImageTag(imageResIds[i], 1);
                        ImageView imageView = (ImageView)mGridView.getChildAt(i - p);
                        mImageLoader.loadImage(imageView, tag, imageResIds[i]);
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    mFirstVisibleItem = firstVisibleItem;
                    mVisibleItemCount = visibleItemCount;
                    totItemCount = totalItemCount;
                }
            };
        }

        @Override
        public int getCount() {
            return imageResIds.length;
        }

        @Override
        public Object getItem(int position) {
            return imageResIds[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            ImageView imageView = null;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams((int)width, (int)height);
                imageView.setLayoutParams(lp);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                imageView = (ImageView)convertView;
            }
            imageView.setImageBitmap(null);
            String tag = getImageTag(imageResIds[position], 1);
            mImageLoader.loadImage(imageView, tag, imageResIds[position]);
            return imageView;
        }

    }

    private String getImageTag(int resId, int compressLevel){
        return ((Integer)resId).toString() + "#($91" + ((Integer)compressLevel).toString() + "98741";
    }

    private final class ViewHolder{
        ImageView mImageView;
    }
}
