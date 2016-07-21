package com.mrpeng.chatdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 1、解析asset文件，得到一个内容为asset文件一行文字的String集合list，集合实体emoji_1.png,[可爱]
 * 2、封装一个ChatEmoji的bean，内含文件名，描述，对应drawId变量（根据文件名如emoji_1.png），遍历list，填充数据。得到表情集合，集合实体为ChatEmoji
 * 3、将集合实体分页，每一页也是一个集合List<List<ChatEmoji>> emjisLists
 *
 * @see [相关类/方法]
 * @since [产品/模板版本]
 * @deprecated
 */
public class FaceConversionUtils
{
    private static FaceConversionUtils mFaceConversionUtils;

    /**
     * 每一页表情的个数
     */
    private int pagerSize = 20;
    /**
     * 保存于内存中的表情HashMap
     */
    private HashMap<String,String> emojiMap = new HashMap<String,String>();
    /**
     * 保存于内存中的表情集合
     */
    private List<ChatEmoji> emojis = new ArrayList<ChatEmoji>();

    /**
     * 表情分页的结果集合
     */
    public List<List<ChatEmoji>> emjisLists = new ArrayList<List<ChatEmoji>>();

    private ChatEmoji mEmojEntry;
    private Bitmap mBitmap;

    public static FaceConversionUtils getIntance()
    {
        if(mFaceConversionUtils == null)
        {
            mFaceConversionUtils = new FaceConversionUtils();
        }
        return mFaceConversionUtils;


    }

    public void getFileText(Context context)
    {
        ArrayList<String> list = new ArrayList<>();
        try
        {
            InputStream emojiInput = context.getResources().getAssets().open("emoji");
            BufferedReader br = new BufferedReader(new InputStreamReader(emojiInput, "UTF-8"));
            String str = null;
            while(( str = br.readLine() ) != null)
            {
                list.add(str);
            }
        }catch(IOException e)
        {


            e.printStackTrace();
        }
        for(String str : list)
        {
            //emoji_1.png,[可爱]
            String[] text = str.split(",");
            String fileName = text[0].substring(0, text[0].lastIndexOf("."));
            emojiMap.put(text[1], fileName);
            //这行代码是重点！！！
            int drawId = context.getResources().getIdentifier(fileName, "drawable", context.getPackageName());
            if(drawId != 0)
            {
                mEmojEntry = new ChatEmoji();
                mEmojEntry.id = drawId;
                mEmojEntry.character = text[1];
                mEmojEntry.faceName = fileName;
                emojis.add(mEmojEntry);
            }
        }

        int pageCount = (int)Math.ceil(emojis.size()/20+0.1);
        for(int i = 0; i<pageCount; i++)
        {
            emjisLists.add(getData(i));
        }
    }

    private List<ChatEmoji> getData(int pageCount)
    {
        int startIndex = pageCount*pagerSize;
        int endIndex = startIndex+pagerSize;
        //最后一页
        if(endIndex>emojis.size())
        {
            endIndex = emojis.size();
        }
        //        List<ChatEmoji> chatEmojis = emojis.subList(startIndex, endIndex);
        //        return chatEmojis;


        List<ChatEmoji> list = new ArrayList<ChatEmoji>();
        list.addAll(emojis.subList(startIndex, endIndex));
        if(list.size()<pagerSize)
        {
            for(int i = list.size(); i<pagerSize; i++)
            {
                ChatEmoji object = new ChatEmoji();
                list.add(object);
            }
        }
        if(list.size() == pagerSize)
        {
            ChatEmoji object = new ChatEmoji();
            object.id = ( R.drawable.face_del_icon );
            list.add(object);
        }
        return list;
    }


    /**
     * 添加表情
     *
     * @param context
     * @param imgId
     * @param spannableString
     * @return
     */
    public SpannableString addFace(Context context, int imgId,
                                   String spannableString) {
        if (TextUtils.isEmpty(spannableString)) {
            return null;
        }
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                imgId);
        float density = context.getResources().getDisplayMetrics().density;
        bitmap = Bitmap.createScaledBitmap(bitmap, (int)(20*density), (int)(20*(density)), true);
        ImageSpan imageSpan = new ImageSpan(context, bitmap);
        SpannableString spannable = new SpannableString(spannableString);
        spannable.setSpan(imageSpan, 0, spannableString.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    public SpannableString getExpressionString(Context context, String str)
    {
        SpannableString spannableString = new SpannableString(str);
        // 正则表达式比配字符串里是否含有表情，如： 我好[开心]啊
        String zhengze = "\\[[^\\]]+\\]";
        // 通过传入的正则表达式来生成一个pattern
        Pattern sinaPatten = Pattern.compile(zhengze, Pattern.CASE_INSENSITIVE);
        try {
            dealExpression(context, spannableString, sinaPatten, 0);
        } catch (Exception e) {
            Log.e("dealExpression", e.getMessage());
        }
        return spannableString;



    }

    private void dealExpression(Context context, SpannableString spannableString, Pattern patten,int start)
    {
        Matcher matcher = patten.matcher(spannableString);
        while (matcher.find()) {
            String key = matcher.group();
            // 返回第一个字符的索引的文本匹配整个正则表达式,ture 则继续递归
            if (matcher.start() < start) {
                continue;
            }
            String value = emojiMap.get(key);
            if (TextUtils.isEmpty(value)) {
                continue;
            }
            int resId = context.getResources().getIdentifier(value, "drawable",
                    context.getPackageName());
            // 通过上面匹配得到的字符串来生成图片资源id
            // Field field=R.drawable.class.getDeclaredField(value);
            // int resId=Integer.parseInt(field.get(null).toString());
            if (resId != 0) {
                Bitmap bitmap = BitmapFactory.decodeResource(
                        context.getResources(), resId);
                float density = context.getResources().getDisplayMetrics().density;
                bitmap = Bitmap.createScaledBitmap(bitmap, (int)(40*density), (int)(40*density), true);
                // 通过图片资源id来得到bitmap，用一个ImageSpan来包装
                ImageSpan imageSpan = new ImageSpan(bitmap);
                // 计算该图片名字的长度，也就是要替换的字符串的长度
                int end = matcher.start() + key.length();
                // 将该图片替换字符串中规定的位置中
                spannableString.setSpan(imageSpan, matcher.start(), end,
                        Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                if (end < spannableString.length()) {
                    // 如果整个字符串还未验证完，则继续。。
                    dealExpression(context, spannableString, patten, end);
                }
                break;
            }
        }
    }
}
