package com.eethan.ineedu.constant;


public class URLConstant {

	public final static String INEEDU_IP = "http://"+Constant.IP+":8080";
	public final static String INEEDU_URL = INEEDU_IP+"/INeedUServer/user/";
	public final static String SHARE_IMG_URL="http://www.eethan.com/share/";
	public final static String SHARE_FOLDER_URL="http://www.eethan.com/share/";//分享截图
	public final static String BIG_HEAD_PIC_URL=INEEDU_IP+"/INeedUServer/head/head_big_";
	public final static String BG_URL=INEEDU_IP+"/INeedUServer/background/";
	public final static String DEFAULT_HEAD_URL=INEEDU_IP+"/INeedUServer/default_head/h_";
	public final static String HEAD_REQUEST_URL=INEEDU_URL+ "uploadImg.action";
	public final static String UPLOAD_CRASH_FILE_URL=INEEDU_URL+ "uploadCrashFile.action";//闪退时的错误日志
	public final static String CUSTOM_BG_FOLDER=INEEDU_IP+ "/INeedUServer/customBackground/";//Pourlisten用户自定义背景
	public final static String DEFAULT_HEAD_FOLDER=INEEDU_IP+ "/INeedUServer/default_head/";//默认头像文件夹
	public final static String PHOTO_NEWS_PHOTO_URL = INEEDU_IP+"/INeedUServer/TakePhotos/";
	public final static String ALBUM_PHOTO_URL = INEEDU_IP+"/INeedUServer/album/";
	
	public final static String LOGIN_URL = INEEDU_URL+"login.action"; 
	public final static String CHECK_RIGISTER_URL = INEEDU_URL+"checkRegister.action";
	public final static String RIGISTER_URL = INEEDU_URL+"register.action";
	public final static String ISSUE_NEED_URL = INEEDU_URL+ "commitNeed.action";
	public final static String REFRESH_POURLISTEN_URL = INEEDU_URL+ "refreshPourlisten.action";
	public final static String REFRESH_NEED_URL = INEEDU_URL+ "refreshNeed.action";
	public final static String REFRESH_NEAR_URL = INEEDU_URL+ "refreshNearUser.action";
	public final static String NEED_GET_MORE = INEEDU_URL+ "getMoreNeed.action";
	public final static String COMMENT_NEED_URL = INEEDU_URL+ "commentNeed.action";
	public final static String COMMENT_POURLISTEN_URL = INEEDU_URL+ "commentPourlisten.action";
	public final static String REFRESH_LOVE_RANK_URL = INEEDU_URL+ "refreshLoveRank.action";
	public final static String REFRESH_POPULARITY_RANK_URL = INEEDU_URL+ "refreshPopularityRank.action";
	public final static String ISSUE_POURLISTEN_URL=INEEDU_URL+ "commitPourlisten.action";
	public final static String SHOW_NEED_DETAIL_URL=INEEDU_URL+ "showNeedDetail.action";
	public final static String GET_MORE_POURLISTEN_URL=INEEDU_URL+ "getMorePourlisten.action";
	public final static String UPDATE_USER_LOCATION_URL=INEEDU_URL+ "updateUserLocation.action";
	public final static String GET_USER_DETAIL_INFO_URL=INEEDU_URL+ "showUserDetailInfo.action";
	public final static String GET_USER_DETAIL_INFO_NEW_URL=INEEDU_URL+ "showUserDetailInfoNew.action";
	public final static String UPDATE_NICKNAME_URL=INEEDU_URL+ "updateNickName.action";
	public final static String UPDATE_PASSWORD_URL=INEEDU_URL+ "updatePassword.action";
	public final static String UPDATE_SIGNATURE_URL=INEEDU_URL+ "updateSignature.action";
	public final static String UPDATE_USER_ALLINFO_URL=INEEDU_URL+ "updateAllUserInfo.action";
	public final static String UPDATE_SCHOOL_URL=INEEDU_URL+ "updateSchool.action";
	public final static String UPDATE_ACADEMY_URL=INEEDU_URL+ "updateAcademy.action";
	public final static String UPDATE_GRADE_URL=INEEDU_URL+ "updateGrade.action";
	public final static String CHECK_NOTIFICATION_URL=INEEDU_URL+ "checkNotification.action";
	public final static String SHOW_POUR_LISTEN_DETAIL_URL=INEEDU_URL+"showPourlistenDetail.action";
	public final static String THANK_URL=INEEDU_URL+"thank.action";
	public final static String GIVE_POPULARITY_URL=INEEDU_URL+"givePopularity.action";
	public final static String ISSUE_POUR_LISTEN_COMMENT_URL=INEEDU_URL+"commentPourlisten.action";
	public final static String SEARCH_USER_URL=INEEDU_URL+"searchUser.action";
	public final static String SEARCH_USER_URL_NEW=INEEDU_URL+"searchUserNew.action";
	public final static String GET_ALL_NEEDS_URL=INEEDU_URL+"getAllNeeds.action";
	public final static String GET_MORE_ALL_NEEDS_URL=INEEDU_URL+"getMoreAllNeeds.action";
	public final static String GET_USERINFO_FROM_USERID = INEEDU_URL + "getUserInfoFromUserId.action";
	public final static String UPDATE_LOCATION_URL = INEEDU_URL + "updateUserLocation.action";
	public final static String GET_ALL_NOTIFICATIONS_URL = INEEDU_URL + "getAllNotifications.action";
	public final static String GET_MORE_NEAR_USER_URL = INEEDU_URL + "getMoreNearUser.action";
	public final static String GET_MORE_NOTIFICATIONS_URL = INEEDU_URL + "getMoreAllNotifications.action";
	public final static String FORGET_PASSWORD_URL = INEEDU_URL + "retrievePassword.action";
	public final static String GET_BACKGROUND_URL = INEEDU_URL + "getBackground.action";
	public final static String DELETE_NEED_URL = INEEDU_URL + "deleteNeed.action";
	public final static String DELETE_POURLISTEN_URL = INEEDU_URL + "deletePourlisten.action";
	public final static String UPLOAD_SHARE_IMAGE_URL = INEEDU_URL + "uploadShareImg.action";
	public final static String UPLOAD_CUSTOM_BG_URL = INEEDU_URL + "uploadCustomBackground.action";
	public final static String GET_DEFAULT_HEAD_URL = INEEDU_URL + "getDefaultHead.action";
	public final static String USE_DEFAULT_HEAD_URL = INEEDU_URL + "useDefaultHead.action";
	public final static String DELETE_EMPTY_USER = INEEDU_URL + "deleteEmptyUser.action";
	public final static String QQ_LOGIN_URL = INEEDU_URL + "qqLogin.action";
	 
	public final static String COMMIT_PHOTOs_URL = INEEDU_URL + "commitPhotos.action";
	public final static String REFRESH_PHOTOS_URL = INEEDU_URL + "refreshPhotos.action";
	public final static String REFRESH_MOOD_URL = INEEDU_URL + "refreshMood.action";
	public final static String GET_MORE_MOOD_URL = INEEDU_URL + "getMoreMood.action";
	public final static String DETAIL_MOOD_URL = INEEDU_URL + "showMoodDetail.action";
	public final static String COMMIT_MOOD_URL = INEEDU_URL + "commitMood.action";
	public final static String COMMIT_ALBUM_URL = INEEDU_URL + "commitAlbum.action";
	public final static String COMMENT_MOOD_URL = INEEDU_URL + "commentMood.action";
	public final static String GET_MORE_PHOTOS_URL = INEEDU_URL + "getMorePhotos.action";
	public final static String TRANSMIT_PHOTOS_URL = INEEDU_URL + "transmitPhotos.action";
	public final static String DETAIL_PHOTOS_URL = INEEDU_URL + "showPhotoDetail.action";
	public final static String COMMENT_PHOTOS_URL = INEEDU_URL + "commentPhotos.action";
	public final static String LIKE_PHOTOS_URL = INEEDU_URL + "photoPraise.action";
	public final static String LIKE_MOOD_URL = INEEDU_URL + "moodPraise.action";
	
	public final static String CONFIRM_WAKE_UP_URL = INEEDU_URL + "confirmWakeUp.action";
	public final static String REMOVE_WAKE_UP_URL = INEEDU_URL + "removeWakeUp.action";
	public final static String MATCH_WAKE_UP_URL = INEEDU_URL + "matchWakeUp.action";
	public final static String CHOOSE_MATCH_URL = INEEDU_URL + "chooseMatch.action";
	public final static String SHOW_WAKE_UP_URL = INEEDU_URL + "showWakeUp.action";
	
	public final static String PLAYS_COMMIT_PLAY = INEEDU_URL + "commitPlays.action";
	public final static String PLAYS_REFREH_PLAYS = INEEDU_URL + "refreshPlays.action";
	public final static String PLAYS_GETMORE_PLAYS = INEEDU_URL + "getMorePlays.action";
	public final static String PLAYS_COMMENT_PALY = INEEDU_URL + "commentPlays.action";
	public final static String PLAYS_PARTICIPATE_PALY = INEEDU_URL + "participate.action";
	public final static String PLAYS_DETAIL_PALY = INEEDU_URL + "showPlayDetail.action";
	
	public final static String REFRESH_CONTACT = INEEDU_URL + "refreshContact.action";
	
	public final static String UPLOAD_TAKE_PHOTOS_URL = INEEDU_URL + "uploadTakePhotos.action";
	public final static String UPLOAD_ALBUM_PHOTO_URL = INEEDU_URL + "uploadAlbumPhoto.action";

	public final static String SHOW_MOLEST_URL = INEEDU_URL + "showMolest.action";
	public final static String COMMIT_MOLEST_URL = INEEDU_URL + "commitMolest.action";
	public final static String GET_MORE_MOLEST_URL = INEEDU_URL + "getMoreMolest.action";

	public final static String WISH_COMMENT_WISH = INEEDU_URL + "commentWish.action";
	public final static String WISH_COMMIT_WISH = INEEDU_URL + "commitWish.action";
	public final static String WISH_FLOWER_WISH = INEEDU_URL + "wishFlower.action";
	public final static String WISH_PICK_WISH = INEEDU_URL + "wishComeTrue.action";
	public final static String WISH_DETAIL = INEEDU_URL + "showWishDetail.action";
	public final static String WISH_REFRESH = INEEDU_URL + "refreshWish.action";
	public final static String WISH_GETMORE = INEEDU_URL + "getMoreWish.action";
	public final static String WISH_REFRESH_NEW = INEEDU_URL + "refreshWishNew.action";
	public final static String WISH_GETMORE_NEW = INEEDU_URL + "getMoreWishNew.action";
	public final static String WISH_JOIN = INEEDU_URL + "joinPickers.action";
	public final static String WISH_GET_PICKERS = INEEDU_URL + "getWishPickers.action";
	
	public final static String GET_FNAS = INEEDU_URL + "getFlowerFans.action";
	public final static String ADD_CHAT_CONTACT = INEEDU_URL + "addChatContact.action";
	public final static String ADD_LOVE_NUM = INEEDU_URL + "updateLoveNum.action";
	
	public final static String GET_PRO_CITY_COL_SCH = INEEDU_URL + "getProCityCoSchool.action";
	
}
