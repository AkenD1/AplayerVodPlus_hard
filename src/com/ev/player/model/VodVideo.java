package com.ev.player.model;

import android.os.Parcel;
import android.os.Parcelable;

public class VodVideo implements Parcelable {
	private String catid;
	private String channelId;
	private String drama;
	private String grade;
	private String link;
	private String name;
	private String pic;
	private String protagonist;
	private String region;
	private String regisseur;
	private String streamip;
	private String subcatid;
	private String type;
	private String url;
	private String year;
	private String adpic="";
	private int adsec=0;
	
	public String getAdpic() {
		return adpic;
	}



	public void setAdpic(String adpic) {
		this.adpic = adpic;
	}



	public int getAdsec() {
		return adsec;
	}



	public void setAdsec(int adsec) {
		this.adsec = adsec;
	}



	public VodVideo(){}
	
	
	
	public VodVideo(String catid, String channelId, String drama, String grade,
			String link, String name, String pic, String protagonist,
			String region, String regisseur, String streamip, String subcatid,
			String type, String url, String year) {
		super();
		this.catid = catid;
		this.channelId = channelId;
		this.drama = drama;
		this.grade = grade;
		this.link = link;
		this.name = name;
		this.pic = pic;
		this.protagonist = protagonist;
		this.region = region;
		this.regisseur = regisseur;
		this.streamip = streamip;
		this.subcatid = subcatid;
		this.type = type;
		this.url = url;
		this.year = year;
	}



	public String getCatid() {
		return this.catid;
	}

	public String getChannelId() {
		return this.channelId;
	}

	public String getDrama() {
		return this.drama;
	}

	public String getGrade() {
		return this.grade;
	}

	public String getLink() {
		return this.link;
	}

	public String getName() {
		return this.name;
	}

	public String getPic() {
		return this.pic;
	}

	public String getProtagonist() {
		return this.protagonist;
	}

	public String getRegion() {
		return this.region;
	}

	public String getRegisseur() {
		return this.regisseur;
	}

	public String getStreamip() {
		return this.streamip;
	}

	public String getSubcatid() {
		return this.subcatid;
	}

	public String getType() {
		return this.type;
	}

	public String getUrl() {
		return this.url;
	}

	public String getYear() {
		return this.year;
	}

	public void setCatid(String paramString) {
		this.catid = paramString;
	}

	public void setChannelId(String paramString) {
		this.channelId = paramString;
	}

	public void setDrama(String paramString) {
		this.drama = paramString;
	}

	public void setGrade(String paramString) {
		this.grade = paramString;
	}

	public void setLink(String paramString) {
		this.link = paramString;
	}

	public void setName(String paramString) {
		this.name = paramString;
	}

	public void setPic(String paramString) {
		this.pic = paramString;
	}

	public void setProtagonist(String paramString) {
		this.protagonist = paramString;
	}

	public void setRegion(String paramString) {
		this.region = paramString;
	}

	public void setRegisseur(String paramString) {
		this.regisseur = paramString;
	}

	public void setStreamip(String paramString) {
		this.streamip = paramString;
	}

	public void setSubcatid(String paramString) {
		this.subcatid = paramString;
	}

	public void setType(String paramString) {
		this.type = paramString;
	}

	public void setUrl(String paramString) {
		this.url = paramString;
	}

	public void setYear(String paramString) {
		this.year = paramString;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel reply, int parcelableWriteReturnValue) {
		reply.writeString(catid);
		reply.writeString(channelId);
		reply.writeString(drama);
		reply.writeString(grade);
		reply.writeString(link);
		reply.writeString(name);
		reply.writeString(pic);
		reply.writeString(protagonist);
		reply.writeString(region);
		reply.writeString(regisseur);
		reply.writeString(streamip);
		reply.writeString(subcatid);
		reply.writeString(type);
		reply.writeString(url);
		reply.writeString(year);
	}
	
	public static final Parcelable.Creator<VodVideo> CREATOR = new Parcelable.Creator<VodVideo>() {
		public VodVideo createFromParcel(Parcel in) {
			return new VodVideo(in.readString(),in.readString(),in.readString(),in.readString(),in.readString(),
					in.readString(),in.readString(),in.readString(),in.readString(),in.readString(),in.readString(),
					in.readString(),in.readString(),in.readString(),in.readString());
		}

		public VodVideo[] newArray(int size) {
			return new VodVideo[size];
		}
	};
}

