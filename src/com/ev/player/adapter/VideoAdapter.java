package com.ev.player.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ev.player.model.VodVideo;
import com.aplayer.vod.plus.R;

public class VideoAdapter extends BaseAdapter{

	private LayoutInflater mInfalter;
	private List<VodVideo> mListVodVideo;
	private Context mContext;
	private int nowplaypos=-1;
	public VideoAdapter(Context context,List<VodVideo> list){
		mInfalter = LayoutInflater.from(context);
		mListVodVideo = list;
		mContext = context;
	}
	public void changvod(int pos){
		this.nowplaypos=pos;
		this.notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		return mListVodVideo.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mListVodVideo.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		Holder holder = null;
		if(null == convertView){
			holder = new Holder();
			convertView = mInfalter.inflate(R.layout.vod_item, null);
			holder.name = (TextView) convertView.findViewById(R.id.catgory_item_text);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		VodVideo program = mListVodVideo.get(position);
		holder.name.setText(program.getName());
//		holder.name.setBackgroundColor(mContext.getResources().getColor(R.color.test));
		if(nowplaypos==position){
			convertView.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.item_vod_now));
		}else{
			convertView.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.item_vod_nornal));
		}
	
//		R.drawable.bg_vod
//		R.drawable.i
//		convertView.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.vod_bg));
		return convertView;
	}
	
	class Holder{
		TextView name;
	}

}
