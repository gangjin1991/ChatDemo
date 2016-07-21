package com.mrpeng.chatdemo;

import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * <一句话功能描述>
 * <功能详细描述>
 *
 * @see [相关类/方法]
 * @since [产品/模板版本]
 * @deprecated
 */
public class ChatMsgAdapter extends BaseAdapter {

    public static interface IMsgViewType {
        int IMVT_COM_MSG = 0;
        int IMVT_TO_MSG = 1;
    }

    private List<ChatMsgEntity> coll;
    private LayoutInflater mInflater;
    private Context context;
    public ChatMsgAdapter(Context context, List<ChatMsgEntity> coll) {
        this.coll = coll;
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    public int getCount() {
        return coll.size();
    }

    public Object getItem(int position) {
        return coll.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

//    public int getItemViewType(int position) {
//        ChatMsgEntity entity = coll.get(position);
//
//        if (entity.isComing) {
//            return IMsgViewType.IMVT_COM_MSG;
//        } else {
//            return IMsgViewType.IMVT_TO_MSG;
//        }
//
//    }

//    public int getViewTypeCount() {
//        return 2;
//    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ChatMsgEntity entity = coll.get(position);
        boolean isComMsg = entity.isComing;

        ViewHolder viewHolder = null;
        if (convertView == null) {
            if (isComMsg) {
                convertView = mInflater.inflate(
                        R.layout.item_chat_left, null);
            } else {
                convertView = mInflater.inflate(
                        R.layout.item_chat_right, null);
            }

            viewHolder = new ViewHolder();
            viewHolder.tvSendTime = (TextView) convertView
                    .findViewById(R.id.tv_sendtime);
            viewHolder.tvContent = (TextView) convertView
                    .findViewById(R.id.tv_chatcontent);
            viewHolder.isComMsg = isComMsg;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvSendTime.setText(entity.date);
        SpannableString spannableString =
                FaceConversionUtils.getIntance().getExpressionString(context, entity.text);
        viewHolder.tvContent.setText(spannableString);

        return convertView;
    }

    class ViewHolder {
        public TextView tvSendTime;
        public TextView tvContent;
        public boolean isComMsg = true;
    }

}