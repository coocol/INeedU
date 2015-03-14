package com.eethan.ineedu.CommonUse;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.eethan.ineedu.adapter.PhotoNewsAdapter;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.database.DbUtil;
import com.eethan.ineedu.databasebeans.PlaysParticipant;
import com.eethan.ineedu.jackson.JacksonUtil;
import com.eethan.ineedu.model.PlayJoin;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.util.ToastHandler;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;


public class PlayJoinEvent {

	private PlaysParticipant playsParticipant;
	private Context context;
	private Handler handler;
	private int myUserId;

	public PlayJoinEvent(PlaysParticipant playsParticipant, Context context) {
		this.playsParticipant = playsParticipant;
		this.context = context;
		handler = new ToastHandler(this.context);
	}

	public void start() {
		exeute();
	}

	public void exeute() {
		myUserId = new SPHelper(context).GetUserId();

		new Thread() {
			public void run() {
				Message message = handler.obtainMessage();
				if (playsParticipant.getOwner() == playsParticipant.getUserId()) {
					message.what = Constant.PLAY_JOIN_MYSELF;
					message.sendToTarget();
					return;
				}
				PlayJoin PlayJoin = null;
				try {
					PlayJoin = DbUtil.getDbUtils(context).findFirst(
							Selector.from(PlayJoin.class)
									.where(PlayJoin.PLAYID, "=", playsParticipant.getPlaysId()));
				} catch (DbException e1) {
					e1.printStackTrace();
				}
				if (PlayJoin != null) {
					message.what = Constant.PLAY_JOIN_ALREADY;
					message.sendToTarget();
					return;
				}
				try {
					String resString = ServerCommunication.request(
							playsParticipant,
							URLConstant.PLAYS_PARTICIPATE_PALY);
					boolean isOk = JacksonUtil.json().fromJsonToObject(
							resString, boolean.class);
					if (isOk) {
						message.what = Constant.PLAY_JOIN_SUCCESS;
					} else {
						message.what = Constant.PLAY_JOIN_FAIL;
					}
					message.sendToTarget();
					PlayJoin playJoin2 = new PlayJoin();
					playJoin2.setPlayId(playsParticipant.getPlaysId());
					try {
						DbUtil.getDbUtils(context).save(playJoin2);
					} catch (DbException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return;
				} catch (Exception e) {
					e.printStackTrace();
					message.what = Constant.CONNECT_FAILED;
					message.sendToTarget();
					return;
				}

			}
		}.start();

	}

}
