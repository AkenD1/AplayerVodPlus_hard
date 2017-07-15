package com.ev.player;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ev.player.DisplayMode.DisplayModeEntity;
import com.aplayer.vod.plus.R;

public class DisplayModeAdapter extends BaseAdapter{
	
	private List<DisplayModeEntity> mListPlayMode;
	private Context mContext;
	private LayoutInflater mInflater;
	private int mDisplayMode;
	public DisplayModeAdapter(Context context,List<DisplayModeEntity> list){
		mListPlayMode = list;
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		mDisplayMode = DisplayMode.getMode(mContext);
		if(mDisplayMode < 0 || mDisplayMode >= list.size())
			mDisplayMode = DisplayMode.SCREEN_FULL;
	}
	@Override
	public int getCount() {
		return mListPlayMode.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mListPlayMode.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parents) {
		Holder holder = null;
		if(null == convertView){
			holder = new Holder();
			convertView = mInflater.inflate(R.layout.display_mode_item, null);
			holder.image = (ImageView) convertView.findViewById(R.id.display_mode_img);
			holder.text = (TextView) convertView.findViewById(R.id.display_mode_name);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		DisplayModeEntity displayEntity = mListPlayMode.get(position);
		holder.text.setText(displayEntity.modeString);
		if(mDisplayMode == displayEntity.mode)
			holder.image.setVisibility(View.VISIBLE);
		else holder.image.setVisibility(View.INVISIBLE);
		return convertView;
	}

	class Holder{
		ImageView image;
		TextView text;
	}
}
