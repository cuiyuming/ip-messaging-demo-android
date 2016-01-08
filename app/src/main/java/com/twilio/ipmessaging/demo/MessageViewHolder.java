package com.twilio.ipmessaging.demo;

import com.twilio.ipmessaging.Message;
import com.twilio.ipmessaging.demo.R;

import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import uk.co.ribot.easyadapter.ItemViewHolder;
import uk.co.ribot.easyadapter.PositionInfo;
import uk.co.ribot.easyadapter.annotations.LayoutId;
import uk.co.ribot.easyadapter.annotations.ViewId;

@LayoutId(R.layout.message_item_layout)
public class MessageViewHolder extends ItemViewHolder<Message> {


	private static final String RSA_PUBLIC_KEY1 = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDosvH1gCpQTTZLXGMcSeeqDjWuDVY0+Aab1VbtGJqWdkPd32D4hEUwFjVJ+FJbq7UpvFFDQ3k2y2n/1rzxWapFk/e+BNNCSKP9e6+Of1SLs83So27dgiAeAKmdQoxwfXrgvP1/QRMJJ0i6m3CRRyTlXO+cMGbYqRv1iTT9uaRolQIDAQAB";
	private static final String RSA_PRIVATE_KEY1 = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAOiy8fWAKlBNNktcYxxJ56oONa4NVjT4BpvVVu0YmpZ2Q93fYPiERTAWNUn4UlurtSm8UUNDeTbLaf/WvPFZqkWT974E00JIo/17r45/VIuzzdKjbt2CIB4AqZ1CjHB9euC8/X9BEwknSLqbcJFHJOVc75wwZtipG/WJNP25pGiVAgMBAAECgYEAv4PXY8hyCtkhYHDPGU8yHWHIiFFtq/ad6c9x1X00bbU0Mf1Q3/hswSDmBtUbY1s0pP7amtODhbdwrCFeK/0yBrOegb2fQeJs/QL6/y4/DPzRB21k9N8cQjgmv5tQb72fwdY8nDROXnzKQceMo6b/xkWaIhvhdUq6nCqPvoIGRIECQQD+lOKTQk769G9BQd7HW+2H2NioPbxri+V27daC1M5uBfBj8Wt3NDJ5IyMvOHz5yTlm8FsE2Zz1/aFdLJ/Rv4IRAkEA6f7ZOMcuxlRsAiN708+r3q3sxAyBood+qAJ1MKhOrdR94RcAPUkcjFTZ8j1v0eclj6+w2RChcpb5Ath93ia6RQJBAP3b6x+axHUcn4A8NfEn6vFGu6zwet3nT3bLbddia0JtK6wNhfMFGruO3TvuITlXfaT3UlvAv/LP6kOmBuw6AnECQQDR3r29awjM4ZMuJ908EJs6Ugx1mjH7MEOtNOcfCRXoWxm79QFF9nkgdEo2NlxAi2zo/s9DIONs/3O/1aSux1VxAkBkkOdc0f2ogWZHqtCYfVfYjwbMvlW/6lnbq0B76V1SVqogoSubwnF7EUBdmqpzWmzqM4xURBh9QqDnUUfBzPMW";


	@ViewId(R.id.body)
	TextView body;
	
	@ViewId(R.id.txtInfo)
	TextView txtInfo;
	
	@ViewId(R.id.singleMessageContainer)
	LinearLayout singleMessageContainer;

	View view;
	private RSAUtil rsaUtil = new RSAUtil();

	public MessageViewHolder(View view) {
		super(view);
		this.view = view;
	}

	@Override
	public void onSetListeners() {
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				OnMessageClickListener listener = getListener(OnMessageClickListener.class);
				if (listener != null) {
					listener.onMessageClicked(getItem());
				}
			}
		});
	}
	@Override
	public void onSetValues(Message message, PositionInfo pos) {
		StringBuffer textInfo = new StringBuffer();
		if(message != null) {
			String dateString = message.getTimeStamp();
			if(dateString != null) {
				textInfo.append(message.getAuthor()).append(":").append(dateString);
			} else {
				textInfo.append(message.getAuthor());
			}
			txtInfo.setText(textInfo.toString());

			String bodyText = message.getMessageBody();
			//bodyText = bodyText.replace("\n","");
			if(message.getAuthor().equals("user1")) {
				try {
					bodyText = new String(rsaUtil.decryptByPrivateKey(message.getMessageBody().getBytes("utf-8"), RSA_PRIVATE_KEY1));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else if(message.getAuthor().equals("user2")) {
				try {
					bodyText = new String(rsaUtil.decryptByPrivateKey(message.getMessageBody().getBytes("utf-8"), RSA_PRIVATE_KEY1));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			body.setText(bodyText);
			
			boolean left = (message.getAuthor().compareTo(LoginActivity.local_author) ==0)? true:false;
			body.setBackgroundResource(left ? R.drawable.bubble_a : R.drawable.bubble_b);
			singleMessageContainer.setGravity(left ? Gravity.LEFT : Gravity.RIGHT);
		}
		
	}
	
	public interface OnMessageClickListener {
		void onMessageClicked(Message message);
	}

}
