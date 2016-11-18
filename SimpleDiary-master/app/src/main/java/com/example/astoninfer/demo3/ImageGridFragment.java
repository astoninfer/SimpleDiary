package com.example.astoninfer.demo3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;


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
            /*
            R.drawable.img001, R.drawable.img002, R.drawable.img003, R.drawable.img004,
            R.drawable.img005, R.drawable.img006, R.drawable.img007, R.drawable.img008,
            R.drawable.img009, R.drawable.img010, R.drawable.img011, R.drawable.img012,
            R.drawable.img013, R.drawable.img014, R.drawable.img015, R.drawable.img016,
            R.drawable.img017, R.drawable.img018, R.drawable.img019, R.drawable.img020,
            R.drawable.img021, R.drawable.img022, R.drawable.img023, R.drawable.img024,
            R.drawable.img025, R.drawable.img026, R.drawable.img027, R.drawable.img028,
            R.drawable.img029, R.drawable.img030, R.drawable.img031, R.drawable.img032,
            R.drawable.img033, R.drawable.img034, R.drawable.img035, R.drawable.img036,
            R.drawable.img037, R.drawable.img038, R.drawable.img039, R.drawable.img040,
            R.drawable.img041, R.drawable.img042, R.drawable.img043, R.drawable.img044,
            R.drawable.img045, R.drawable.img046, R.drawable.img047, R.drawable.img048,
            R.drawable.img049, R.drawable.img050, R.drawable.img051, R.drawable.img052,
            R.drawable.img053, R.drawable.img054, R.drawable.img055, R.drawable.img056,
            R.drawable.img057, R.drawable.img058, R.drawable.img059, R.drawable.img060,
            R.drawable.img061, R.drawable.img062, R.drawable.img063, R.drawable.img064,
            R.drawable.img065, R.drawable.img066, R.drawable.img067, R.drawable.img068,
            R.drawable.img069, R.drawable.img070, R.drawable.img071, R.drawable.img072,
            R.drawable.img073, R.drawable.img074, R.drawable.img075, R.drawable.img076,
            R.drawable.img077, R.drawable.img078, R.drawable.img079, R.drawable.img080,
            R.drawable.img081, R.drawable.img082, R.drawable.img083, R.drawable.img084,
            R.drawable.img085, R.drawable.img086, R.drawable.img087, R.drawable.img088,
            R.drawable.img089, R.drawable.img090, R.drawable.img091, R.drawable.img092,
            R.drawable.img093, R.drawable.img094, R.drawable.img095, R.drawable.img096,
            R.drawable.img097, R.drawable.img098, R.drawable.img099, R.drawable.img100,
            R.drawable.img101, R.drawable.img102, R.drawable.img103, R.drawable.img104,
            R.drawable.img105, R.drawable.img106, R.drawable.img107, R.drawable.img108,
            R.drawable.img109, R.drawable.img110, R.drawable.img111, R.drawable.img112,
            */
    };



    public ImageGridFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new ImageAdapter(getActivity());
        width = AuxUtil.getScreenWidth(getActivity()) / 3f;
        height = AuxUtil.dip2px(getActivity(), 90);
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

    private class ImageAdapter extends BaseAdapter{
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
