package com.mrpeng.chatdemo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * <一句话功能描述>
 * <功能详细描述>
 *
 * @see [相关类/方法]
 * @since [产品/模板版本]
 * @deprecated
 */
public class FaceRelativeLayout extends RelativeLayout implements View.OnClickListener, AdapterView.OnItemClickListener
{


    /** 表情页的监听事件 */
    private OnEmojiSelectedListener mListener;
    /** 表情集合 */
    private List<List<ChatEmoji>> mEmjisLists;
    /** 显示表情页的viewpager */
    private ViewPager vp_face;
    /** 输入框 */
    private EditText mEt_sendmessage;
    /** 游标显示布局 */
    private LinearLayout mLlPointContainer;
    /** 表情区域 */
    private View mLlFacechoose;
    /** 表情页界面集合 */
    private ArrayList<View> pagerViews;
    /** 游标点集合 */
    private ArrayList<ImageView> mPointViews;
    /** 表情数据填充器 */
    private ArrayList<FaceAdapter> mFaceAdapters;


    public FaceRelativeLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.mContext = context;
    }

    private Context mContext;

    public void setListener(OnEmojiSelectedListener listener)
    {
        mListener = listener;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Log.e("mrpeng","方法被执行");
        ChatEmoji emoji = (ChatEmoji)mFaceAdapters.get(current).getItem(position);
        if(emoji.id==R.drawable.face_del_icon){//按下删除键
            int selectionStart = mEt_sendmessage.getSelectionStart();
            String text= mEt_sendmessage.getText().toString();
            if(selectionStart>0){
                String text2 = text.substring(selectionStart-1);
                if("]".equals(text2)){//输入框结尾是表情.开始删除一个表情的逻辑
                    int start = text.lastIndexOf("[");
                    int end=selectionStart;
                    mEt_sendmessage.getText().delete(start,end);
                    return;
                }
                //只删除一个字符
                mEt_sendmessage.getText().delete(selectionStart-1,selectionStart);
            }
        }
        if(!TextUtils.isEmpty(emoji.character)){
            if(mListener!=null)
                mListener.onEmojiSelecter(emoji);
                SpannableString spannableString=FaceConversionUtils.getIntance().addFace(getContext(),emoji.id,emoji.character);
                mEt_sendmessage.append(spannableString);
//            }
        }



    }


    public interface OnEmojiSelectedListener
    {
        void onEmojiSelecter(ChatEmoji emoji);

        void onEmojiDeleted();
    }


    /**
     * 当View中所有的子控件均被映射成xml后触发
     * 这也为处理自定义view子控件提供了一种思路
     */
    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        //mainActivity中已经分页封装完毕，此时已经可以获取表情集合的分页集合
        mEmjisLists = FaceConversionUtils.getIntance().emjisLists;
        onCreate();
    }

    private void onCreate()
    {
        iniView();
        initViewPager();
        initPoint();
        initData();

    }

    private void initPoint()
    {
        mPointViews = new ArrayList<ImageView>();
        ImageView imageView;
        for(int i = 0; i<pagerViews.size(); i++)
        {
            imageView = new ImageView(mContext);
            imageView.setBackgroundResource(R.drawable.d1);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin = 10;
            layoutParams.rightMargin = 10;
            layoutParams.width = 8;
            layoutParams.height = 8;
            mLlPointContainer.addView(imageView, layoutParams);
            if(i == 0 || i == pagerViews.size()-1)
            {
                imageView.setVisibility(GONE);
            }
            if(i == 1)
            {
                imageView.setBackgroundResource(R.drawable.d2);
            }

            mPointViews.add(imageView);
        }
    }

    private int current = 0;

    private void initData()
    {
        vp_face.setAdapter(new ViewPagerAdapter(pagerViews));
        vp_face.setCurrentItem(1);
        vp_face.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {


            }

            @Override
            public void onPageSelected(int position)
            {
                current = position-1;
                drawPoint(position);
                if(position == 0 || ( position == mPointViews.size()-1 ))
                {
                    if(position == 0)
                    {
                        vp_face.setCurrentItem(position+1);
                        mPointViews.get(1).setBackgroundResource(R.drawable.d2);
                    }else
                    {
                        vp_face.setCurrentItem(position-1);
                        mPointViews.get(position-1).setBackgroundResource(R.drawable.d2);
                    }

                }

            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });


    }

    /**
     * 绘制游标背景
     */
    public void drawPoint(int index)
    {
        for(int i = 1; i<mPointViews.size(); i++)
        {
            if(index == i)
            {
                mPointViews.get(i).setBackgroundResource(R.drawable.d2);
            }else
            {
                mPointViews.get(i).setBackgroundResource(R.drawable.d1);
            }
        }
    }


    private void initViewPager()
    {
        pagerViews = new ArrayList<>();
        //左侧添加空白页
        View nullView1 = new View(mContext);
        pagerViews.add(nullView1);

        //中间添加表情页

        mFaceAdapters = new ArrayList<>();
        for(int i = 0; i<mEmjisLists.size(); i++)
        {
            GridView view = new GridView(mContext);
            FaceAdapter faceAdapter = new FaceAdapter(mContext, mEmjisLists.get(i));
            view.setAdapter(faceAdapter);
            mFaceAdapters.add(faceAdapter);
            view.setOnItemClickListener(this);
            view.setNumColumns(7);
            view.setBackgroundColor(Color.TRANSPARENT);
            view.setHorizontalSpacing(1);
            view.setVerticalSpacing(1);
            view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
            view.setCacheColorHint(0);
            view.setPadding(5, 0, 5, 0);
            view.setSelector(new ColorDrawable(Color.TRANSPARENT));
            view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            view.setGravity(Gravity.CENTER);
            pagerViews.add(view);
        }


        //右侧添加空白页
        View nullView2 = new View(mContext);
        pagerViews.add(nullView2);


    }

    private void iniView()
    {
        vp_face = (ViewPager)findViewById(R.id.vp_contains);
        mEt_sendmessage = (EditText)findViewById(R.id.et_sendmessage);
        mLlPointContainer = (LinearLayout)findViewById(R.id.ll_point_container);
        mEt_sendmessage.setOnClickListener(this);
        findViewById(R.id.btn_face).setOnClickListener(this);
        mLlFacechoose = findViewById(R.id.ll_facechoose);
    }


    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.btn_face:
                InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getApplicationWindowToken(), 0);
                shoHidenEmoji();
                break;
            case R.id.et_sendmessage:
                mLlFacechoose.setVisibility(GONE);
                break;
        }

    }

    private void shoHidenEmoji()
    {
        if(mLlFacechoose.getVisibility() == VISIBLE)
        {
            mLlFacechoose.setVisibility(GONE);
        }else
        {
            mLlFacechoose.setVisibility(VISIBLE);
        }
    }


    public void showHidenInput()
    {
        InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);

    }
    /**
     * 隐藏表情选择框
     */
    public boolean hideFaceView() {
        // 隐藏表情选择框
        if (mLlFacechoose.getVisibility() == View.VISIBLE) {
            mLlFacechoose.setVisibility(View.GONE);
            return true;
        }
        return false;
    }
    private class FaceAdapter extends BaseAdapter
    {
        private List<ChatEmoji> mChatEmojis;
        private LayoutInflater mInflater;
        private int size;

        public FaceAdapter(Context context, List<ChatEmoji> chatEmojis)
        {
            this.mChatEmojis = chatEmojis;
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount()
        {
            return mChatEmojis.size();
        }

        @Override
        public Object getItem(int position)
        {
            return mChatEmojis.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ChatEmoji chatEmoji = mChatEmojis.get(position);
            ViewHolder holder = null;
            if(convertView == null)
            {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_face, null);
                holder.iv_face = (ImageView)convertView.findViewById(R.id.item_iv_face);
                convertView.setTag(holder);
            }else
            {
                holder = (ViewHolder)convertView.getTag();
            }
            if(chatEmoji.id == R.drawable.face_del_icon)
            {
                convertView.setBackgroundDrawable(null);
                holder.iv_face.setImageResource(chatEmoji.id);
            }else if(TextUtils.isEmpty(chatEmoji.character))
            {
                convertView.setBackgroundDrawable(null);
                holder.iv_face.setImageDrawable(null);
            }else
            {
                holder.iv_face.setTag(chatEmoji);//此行代码为了什么？
                holder.iv_face.setImageResource(chatEmoji.id);
            }
            return convertView;
        }
    }

    class ViewHolder
    {
        public ImageView iv_face;
    }

    private class ViewPagerAdapter extends PagerAdapter
    {
        private List<View> pageViews;

        public ViewPagerAdapter(ArrayList<View> pagerViews)
        {
            this.pageViews = pagerViews;
        }

        @Override
        public int getCount()
        {
            return pageViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object)
        {
            return view == object;
        }

        @Override
        public int getItemPosition(Object object)
        {
            return super.getItemPosition(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            ( (ViewPager)container ).addView(pageViews.get(position));
            return pageViews.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            ( (ViewPager)container ).removeView(pageViews.get(position));
        }
    }


}
