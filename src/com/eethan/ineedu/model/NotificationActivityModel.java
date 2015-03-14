package com.eethan.ineedu.model;

public class NotificationActivityModel {
	private int ReplyManId;//回复者 为-1时表示是Pourlisten的回复
	private String replyName;
	private String replySex;
	private String replyContent;
	
	private int TYPE;//1,-1 Need   2,-2 Pourlisten 
	private int MessageId;//Need或PL的ID  -1每日赞  
	
	
	
	public int getReplyManId() {
		return ReplyManId;
	}
	public void setReplyManId(int replyManId) {
		ReplyManId = replyManId;
	}
	public String getReplyName() {
		return replyName;
	}
	public void setReplyName(String replyName) {
		this.replyName = replyName;
	}
	public String getReplySex() {
		return replySex;
	}
	public void setReplySex(String replySex) {
		this.replySex = replySex;
	}
	public String getReplyContent() {
		return replyContent;
	}
	public void setReplyContent(String replyContent) {
		this.replyContent = replyContent;
	}
	public int getTYPE() {
		return TYPE;
	}
	public void setTYPE(int tYPE) {
		TYPE = tYPE;
	}
	public int getMessageId() {
		return MessageId;
	}
	public void setMessageId(int messageId) {
		MessageId = messageId;
	}
}
