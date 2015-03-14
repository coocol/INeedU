package com.eethan.ineedu.primaryactivity;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.eethan.ineedu.adapter.PourListenDetailActivityAdapter;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.databasebeans.Pourlisten;
import com.eethan.ineedu.databasebeans.PourlistenComment;
import com.eethan.ineedu.databasebeans.UserInfo;
import com.eethan.ineedu.databasebeans.UserLocation;
import com.eethan.ineedu.jackson.DetailPourlistenJsonObject;
import com.eethan.ineedu.jackson.JacksonUtil;
import com.eethan.ineedu.model.PourlistenDetailModel;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.mycontrol.ScrollListView;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.util.CommentUtil;
import com.eethan.ineedu.util.DataTraslator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PourListenDetailActivity extends BaseActivity{
	public int PourlistenId;
	public int userId;//此条Pourlisten发布者ID
	//适配器与数据源
	private PourListenDetailActivityAdapter pldAdapter;
	private ArrayList<PourlistenDetailModel> commentsForAdapter=new ArrayList<PourlistenDetailModel>();
	//服务器数据
	Pourlisten pl=new Pourlisten();
	ArrayList<PourlistenComment> plComments=new ArrayList<PourlistenComment>();
	ArrayList<UserInfo> plUserinfos=new ArrayList<UserInfo>();
	ArrayList<UserLocation> plUserLocations=new ArrayList<UserLocation>();
	ArrayList<PourlistenComment> InitComments=new ArrayList<PourlistenComment>();
	//imageLoader
	private ImageLoader imageLoader=ImageLoader.getInstance();
	//控件
	private TextView commentButton;
	private ImageButton backButton;
	private TextView content;
	private ImageView bgImageView;
	private TextView commentNum;
	private ScrollListView commentListView;
	
	private ImageView sexImgVu;
	private TextView sendtimeTxtVu;
	private TextView distanceTxtVu;
	
	private ScaleGestureDetector mScaleDetector;  
    private GestureDetector mGestureDetector;  
    private float mScaleFactor = 1;  
	private static final int ZOOM_IN = 4;  
    private static final int ZOOM_OUT = 5;  
    private final int MAX_ZOOM_IN_SIZE = 60;  
    private final int MAX_ZOOM_OUT_SIZE = 20;  
    private final int THE_SIZE_OF_PER_ZOOM = 9;  
    private float mTextSize = 27;  
    private int mZoomMsg = -1;  
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setPageName("PourlistenDetailActivity");
		setContentView(R.layout.pour_listen_detail_page);
		PourlistenId=getIntent().getExtras().getInt("pourlistenId");
		userId=getIntent().getExtras().getInt("userId");
		findView();
		//设置背景图片获得焦点，这样子点进去的时候ScrollView就不会自动翻到最底部了
		bgImageView.setFocusable(true);
		bgImageView.setFocusableInTouchMode(true);
		bgImageView.requestFocus();
		SetListener();
		loadingDialogShow();
		new GetDataTask().execute();
	}

	@SuppressLint("NewApi")
	private void findView() {
		// TODO Auto-generated method stub
		backButton=(ImageButton)findViewById(R.id.pour_listen_issue_page_back_button);
		commentButton=(TextView)findViewById(R.id.pour_listen_detail_page_comment_button);
		content=(TextView)findViewById(R.id.pour_listen_detail_page_text);
		bgImageView=(ImageView)findViewById(R.id.pour_listen_detail_page_bg);
		commentNum=(TextView)findViewById(R.id.pour_listen_detail_page_commentnum);
		commentListView=(ScrollListView)findViewById(R.id.pour_listen_detail_page_comment);
		sexImgVu=(ImageView)findViewById(R.id.iv_sex);
		distanceTxtVu=(TextView)findViewById(R.id.tv_distance);
		sendtimeTxtVu=(TextView)findViewById(R.id.tv_time);
		content.setTransformationMethod(HideReturnsTransformationMethod 
                .getInstance());  
		content.setTextIsSelectable(true);  
        mScaleDetector = new ScaleGestureDetector(this, new MyScaleListener());  
        mGestureDetector = new GestureDetector(this,  
                new GestureDetector.SimpleOnGestureListener() {  
                });
        mGestureDetector.setOnDoubleTapListener(null);  
		
	}

	private void InitView() {
		// TODO Auto-generated method stub
		if(pl!=null)
		{
			content.setText(pl.getContent());
			sendtimeTxtVu.setText(DataTraslator.LongToTimePastGeneral(pl.getTime()));
			distanceTxtVu.setText(DataTraslator.DistanceToString(DataTraslator.GetDistanceToMe(PourListenDetailActivity.this,pl.getLng(), pl.getLat())));
			String sexString = getIntent().getStringExtra("sex");
			if(sexString!=null && sexString.equals("男")){
				sexImgVu.setImageResource(R.drawable.sex_boy_press);
			}else {
				sexImgVu.setImageResource(R.drawable.sex_girl_press);
			}
			//bgScrollView.setBackgroundResource() 
			commentNum.setText(pl.getCommentNum()+"");
		}
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		//.showStubImage(R.drawable.ic_stub_vertical)
		//.showImageForEmptyUri(R.drawable.ic_error_vertical)
		//.showImageOnFail(R.drawable.ic_error_vertical)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
		imageLoader.displayImage(pl.getImageUrl(),bgImageView,options);
	}

	private void InitListView() {
		// TODO Auto-generated method stub
		commentsForAdapter.clear();
		if(plComments!=null&&plComments.size()!=0)
			for(int i=0;i<plComments.size();i++)
			{
				if(plComments.get(i).getCommentedUserId()==-1)
				{
					addComment(i);
					checkReply();
				}
			}
		InitComments.clear();
		for(int i=0;i<plComments.size();i++)
		{
			if(plComments.get(i).getCommentedUserId()==-1)
				InitComments.add(plComments.get(i));
		}
		pldAdapter=new PourListenDetailActivityAdapter(PourListenDetailActivity.this, commentsForAdapter,InitComments);
		commentListView.setAdapter(pldAdapter);
		pldAdapter.notifyDataSetChanged();
	}
	public void addComment(int i)
	{
		PourlistenDetailModel d=new PourlistenDetailModel();
		d.setUserId(plComments.get(i).getUserId());
		d.setComment(plComments.get(i).getComment());
		d.setTime(plComments.get(i).getTime());
		d.setCommentId(plComments.get(i).getId());
		d.setCommentedUserId(plComments.get(i).getCommentedUserId());
		d.setSex(plUserinfos.get(i).getSex());
		String disString = DataTraslator.DistanceToString(DataTraslator.GetDistanceToMe(PourListenDetailActivity.this, plUserLocations.get(i).getLat(), plUserLocations.get(i).getLng()));
		d.setDistance(disString);
		d.setLastLogTime(DataTraslator.LongToTimePastGeneral(plUserLocations.get(i).getTime()));
		commentsForAdapter.add(d);
	}
	public void checkReply()
	{
		for(int j=0;j<plComments.size();j++)
		{
			if(plComments.get(j).getCommentedUserId()!=-1)//对评论的回复
			if(commentsForAdapter.get(commentsForAdapter.size()-1).getCommentId()
					==plComments.get(j).getCommentedUserId())
			{
				addComment(j);
				checkReply();
			}
		}
	}
	private void SetListener() {
		// TODO Auto-generated method stub
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				PourListenDetailActivity.this.finish();
			}
		});
		
		commentButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				Intent intent=new Intent(PourListenDetailActivity.this,IssuePourListenCommentActivity.class);
//				intent.putExtra("pourlistenId", PourlistenId);
//				intent.putExtra("userId", userId);
//				startActivity(intent);
				new PLDetailCommentUtil(PourListenDetailActivity.this, PourlistenId,
						-1, commentNum, Constant.COMMENT_POUR_LISTEN_DETAIL);
				isScrollDown=true;
			}
		});
		commentListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long id) {
				// TODO Auto-generated method stub
				if(id<-1) { 
			        return; 
			    } 
				TextView commentNumTextView=(TextView)findViewById(R.id.pour_listen_detail_page_commentnum);
				int realPosition=(int)id; 
				realPosition=check(realPosition);
				new PLDetailCommentUtil(PourListenDetailActivity.this,
						PourlistenId, commentsForAdapter.get(realPosition).getCommentId(),
						commentNumTextView, Constant.COMMENT_POUR_LISTEN_DETAIL);
			}
		});
	}
	
private class GetDataTask extends AsyncTask<Void, Void, Object> {

		
		@Override
		protected Object doInBackground(Void... params) {
			String response;
			DetailPourlistenJsonObject pourListenDetail = null;
			try {
				response = ServerCommunication.
						request(PourlistenId+"", URLConstant.SHOW_POUR_LISTEN_DETAIL_URL);
				 pourListenDetail = JacksonUtil.json().fromJsonToObject(response,DetailPourlistenJsonObject.class);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}

			return pourListenDetail;
		}

		
		@Override
		protected void onPostExecute(Object r) {
			if(r==null){
				MyToast.showToast(getContext(), "加载失败");
				loadingDialogDismiss();
				super.onPostExecute(r);
				return;
			}
			if(!ServerCommunication.checkResult(getContext(), r))
			{
				MyToast.showToast(getContext(), (String)r);
				loadingDialogDismiss();
				super.onPostExecute(r);
				return;
			}
			DetailPourlistenJsonObject result=(DetailPourlistenJsonObject)r;
			pl=result.getPourlisten();
			plComments=(ArrayList<PourlistenComment>) result.getPourlistenComments();
			plUserinfos=(ArrayList<UserInfo>) result.getCommUserInfos();
			plUserLocations=(ArrayList<UserLocation>) result.getCommUserLocations();
			InitView();
			InitListView();
			if(isScrollDown)
			{
				final ScrollView scrollView=(ScrollView)findViewById(R.id.pour_listen_detail_scrollview);
				new Handler().post(new Runnable() {
				    @Override
				    public void run() {
				        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
				    }
				});
				isScrollDown=false;
			}
			loadingDialogDismiss();
			super.onPostExecute(r);
		}

		
	}
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		new GetDataTask().execute();
	}
	
	private boolean isScrollDown=false;//获取数据后是否滚动到最底部
	public class PLDetailCommentUtil extends CommentUtil
	{

		public PLDetailCommentUtil(Context _context, int _whichId,
				int _whoId, TextView _commentNumTextView, int COMMNET_FLAG) {
			super(_context, _whichId, _whoId, _commentNumTextView, COMMNET_FLAG);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public void NeedDetailUIAction() {
			// TODO Auto-generated method stub
			super.NeedDetailUIAction();
			new GetDataTask().execute();
		}
		
	}
	
	public int check(int pos){
		if(pos==commentsForAdapter.size()-1)
			return pos;
		if(commentsForAdapter.get(pos).getCommentedUserId()==-1)
			if((pos+1)>commentsForAdapter.size()-1)
				return pos;
			if(commentsForAdapter.get(pos+1).getCommentedUserId()==-1)
				return pos;
		pos++;
		if(commentsForAdapter.get(pos).getCommentedUserId()==-1)
		{
			return pos-1;
		}
		return check(pos);
	}
	
	@SuppressLint("HandlerLeak")
	private Handler mUiHandler = new Handler() {  
        @Override  
        public void handleMessage(Message msg) {  
            switch (msg.what) {  
            case ZOOM_IN:  
                zoomIn();  
                content.invalidate();  
                break;  
            case ZOOM_OUT:  
                zoomOut();  
                content.invalidate();//修改TextView后，调用该方法刷新TextView，这样才能看到重新绘制的界面。  
                break;  
            default:  
                break;  
            }  
        }  
    };  
	
	private void zoomIn() {  
        mTextSize = mTextSize + THE_SIZE_OF_PER_ZOOM <= MAX_ZOOM_IN_SIZE ? mTextSize  
                + THE_SIZE_OF_PER_ZOOM  
                : MAX_ZOOM_IN_SIZE;  
        if (mTextSize >= MAX_ZOOM_IN_SIZE) {  
            mTextSize = MAX_ZOOM_IN_SIZE;  
        }  
        content.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);  
    }  
  
    private void zoomOut() {  
        mTextSize = mTextSize - THE_SIZE_OF_PER_ZOOM < MAX_ZOOM_OUT_SIZE ? MAX_ZOOM_OUT_SIZE  
                : mTextSize - THE_SIZE_OF_PER_ZOOM;  
        if (mTextSize <= MAX_ZOOM_OUT_SIZE) {  
            mTextSize = MAX_ZOOM_OUT_SIZE;  
        }  
        content.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);  
    }  
  
    private class MyScaleListener extends  
            ScaleGestureDetector.SimpleOnScaleGestureListener {  
        @Override  
        public boolean onScale(ScaleGestureDetector detector) {  
            float scale = detector.getScaleFactor();  
            if (scale < 0.999999 || scale > 1.00001) {  
                mScaleFactor = scale;  
            }  
            return true;  
        }  
  
        @Override  
        public boolean onScaleBegin(ScaleGestureDetector detector) {  
            return true;  
        }  
  
        @Override  
        public void onScaleEnd(ScaleGestureDetector detector) {  
            float scale = detector.getScaleFactor();  
            if (mScaleFactor > 1.0) {  
                mZoomMsg = ZOOM_IN;  
            } else if (mScaleFactor < 1.0) {  
                mZoomMsg = ZOOM_OUT;  
            }  
        }  
    }  
  
    public boolean onInterceptTouchEvent(MotionEvent ev) {  
        mScaleDetector.onTouchEvent(ev);  
        final int action = ev.getAction();  
        switch (action) {  
        case MotionEvent.ACTION_DOWN:  
            mGestureDetector.onTouchEvent(ev);  
            return false;  
  
        case MotionEvent.ACTION_MOVE:  
            mGestureDetector.onTouchEvent(ev);  
            return false;  
  
        case MotionEvent.ACTION_UP:  
            mGestureDetector.onTouchEvent(ev);  
            Message msg = Message.obtain();  
            msg.what = mZoomMsg;  
            mUiHandler.sendMessage(msg);  
            mZoomMsg = -1;  
            return false;  
        }  
        return true;  
    }  
  
    public boolean onTouchEvent(MotionEvent ev) {  
        mScaleDetector.onTouchEvent(ev);  
        final int action = ev.getAction();  
  
        switch (action) {  
        case MotionEvent.ACTION_DOWN:  
            mGestureDetector.onTouchEvent(ev);  
            return true;  
  
        case MotionEvent.ACTION_MOVE:  
            mGestureDetector.onTouchEvent(ev);  
            return true;  
  
        case MotionEvent.ACTION_UP:  
            mGestureDetector.onTouchEvent(ev);  
            Message msg = Message.obtain();  
            msg.what = mZoomMsg;  
            mUiHandler.sendMessage(msg);  
            mZoomMsg = -1;  
            return true;  
  
        case MotionEvent.ACTION_CANCEL:  
            mGestureDetector.onTouchEvent(ev);  
            return true;  
  
        default:  
            if (mGestureDetector.onTouchEvent(ev)) {  
                return true;  
            }  
  
            return true;  
        }  
    }  
  
    @Override  
    protected void onDestroy() {  
        super.onDestroy();  
        mUiHandler.removeCallbacksAndMessages(null);  
    }
	
	
}
