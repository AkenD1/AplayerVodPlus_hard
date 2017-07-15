package com.ev.player.model;

public class Drama {

	/**剧集名称(01,02,03)*/
	private String name;
	/**force://list.btvgod.com:9906/526b38c60001a2245cf603cd11a20474*/
	private String url;
	private String streamip;
	private String channelId;
	private String link;
	
	
	public String getStreamip() {
		return streamip;
	}
	public void setStreamip(String streamip) {
		this.streamip = streamip;
	}
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	
	@Override
	public String toString() {
		return "Drama [name=" + name + ", url=" + url + ", streamip="
				+ streamip + ", channelId=" + channelId + ", link=" + link
				+ "]";
	}
	
}
