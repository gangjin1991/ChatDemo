package com.mrpeng.chatdemo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * <一句话功能描述>
 * <功能详细描述>
 *
 * @see [相关类/方法]
 * @since [产品/模板版本]
 * @deprecated
 */
public class ChatActivity extends Activity implements View.OnClickListener,View.OnLayoutChangeListener
{

    private Button mBtnSend;

    private EditText mEditTextContent;

    private ListView mListView;


    private String[] msgArray = new String[]{
            "[媚眼]666[媚眼]", "啦啦啦", "测试啦", "测试啦", "测试啦", "你妹[苦逼]", "测[惊讶]你妹", "测你妹[胜利]", "测试啦"
    };

    private String[] dataArray = new String[]{
            "2012-12-12 12:00", "2012-12-12 12:10", "2012-12-12 12:11", "2012-12-12 12:20", "2012-12-12 12:30", "2012-12-12 12:35",
            "2012-12-12 12:40", "2016-66-66 66:66", "2016-66-66 66:66"
    };
    private ChatMsgAdapter mMsgAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //用户选择activity时，软键盘总是被隐藏
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        WindowManager windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        initView();

        initData();

    }
    private final static int COUNT = 8;
    ArrayList<ChatMsgEntity> mChatMsgEntities=new ArrayList<ChatMsgEntity>();

    private void initData()
    {
        for(int i=0;i<COUNT;i++){

            ChatMsgEntity chatMsgEntity = new ChatMsgEntity();
            chatMsgEntity.date=dataArray[i];
            chatMsgEntity.text=msgArray[i];
            if(i%2==0){
                chatMsgEntity.namme="来者";
                chatMsgEntity.isComing=true;
            }else {
                chatMsgEntity.namme="你自己";
                chatMsgEntity.isComing=false;
            }
            mChatMsgEntities.add(chatMsgEntity);
        }

        mMsgAdapter = new ChatMsgAdapter(this, mChatMsgEntities);
        mListView.setAdapter(mMsgAdapter);

    }

    private void initView()
    {
        mListView = (ListView)findViewById(R.id.listview);
        mBtnSend = (Button)findViewById(R.id.btn_send);
        mBtnSend.setOnClickListener(this);
        mEditTextContent = (EditText)findViewById(R.id.et_sendmessage);


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode==KeyEvent.KEYCODE_BACK&&((FaceRelativeLayout)findViewById(R.id.FaceRelativeLayout)).hideFaceView()){
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.btn_send:
                send();
                break;
            case R.id.et_sendmessage:
                caculateScroll(mEditTextContent);
                break;

        }

    }


    public void setLayoutListener(OnLayoutChangeAfterListener listner)
    {
        mLayoutListener = listner;
    }

    private OnLayoutChangeAfterListener mLayoutListener;
    interface OnLayoutChangeAfterListener {
        void onLayoutChangeAfter(int y );
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom)
    {
        Log.e("mrpeng","布局改变");
        if(oldBottom != 0 && bottom != 0 && ( oldBottom-bottom>100 )){
            int y=oldBottom-bottom;
            if(mLayoutListener!=null){
                //执行接口监听器的方法
                Log.e("mrpeng","调用方法");
                mLayoutListener.onLayoutChangeAfter(y);
            }
        }

    }
    private void caculateScroll(View view)
    {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int screenY = location[1];
        setLayoutListener(new OnLayoutChangeAfterListener() {
            //复写结构方法
            @Override
            public void onLayoutChangeAfter(int y)
            {
                Log.e("mrpeng","复写方法");

            }
        });



    }

    private void send()
    {

        String contString = mEditTextContent.getText().toString();
        if (contString.length() > 0) {
            ChatMsgEntity entity = new ChatMsgEntity();
            entity.date=getDate();
            entity.isComing=false;
            entity.text=contString;

            mChatMsgEntities.add(entity);
            mMsgAdapter.notifyDataSetChanged();
            mEditTextContent.setText("");
            mListView.setSelection(mListView.getCount() - 1);
        }


    }

    public String getDate()
    {
        Calendar c = Calendar.getInstance();
        String year = String.valueOf(c.get(Calendar.YEAR));
        String month = String.valueOf(c.get(Calendar.MONTH));
        String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH) + 1);
        String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
        String mins = String.valueOf(c.get(Calendar.MINUTE));

        StringBuffer sbBuffer = new StringBuffer();
        sbBuffer.append(year + "-" + month + "-" + day + " " + hour + ":"
                + mins);

        return sbBuffer.toString();


    }
}
