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


	private static final String RSA_PRIVATE_KEY1 =
			"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";
	private static final String RSA_PUBLIC_KEY1 =
			"MIICITANBgkqhkiG9w0BAQEFAAOCAg4AMIICCQKCAgBZicF3u9K/hdIw86dS5dJe" +
			"5MhdSrnFlEGmN2tQJp0GdyoxCdHT2/1YdcSJUJLDyv0r/NH4uXGS1f/mAbA15ppt" +
			"aD2T2RXSwqHS3YZyk6k+T/oW00DJ3Of0kIteIgmxXQhKgfaBrR+s/NHuvnnFjafk" +
			"errGJV21/h27XHd/d7vi/kPhxdJUQ997J6VKyOXfPTjwgQppqXlKxwjJGvTyT5CF" +
			"4IxFHzL1lhCNYjqi0yt78c1ku6KCb7fc3g/+P+NsBcS9HTe5NQB1R4iueYGZ77JD" +
			"X6pr4PlXcvqYV28EiT03/5+LgWdH1S8qv+HCdA7OczT2ZjFdJtO+XJMMoAT7kffN" +
			"SZuqK1b2j7Lo2UjLrBNBWYdAmP9T3HPex5Ee3wala5efSrVoSnyGbze5JqdO3108" +
			"k+QbOV3IcOL1ZOtsYPVEp6sqFsrI1NYQ6S5f0pyAK2XU3UmW9OrnaDZ/xjSQS6eQ" +
			"rMfNOzbMkE5wxbL5tzC9DJ3djEgpeYk4/eRW7Mytf7nE0ixH/U0IvvuxtmoTd1Kt" +
			"eztUxknkPTPDn0qvjBGuZQylqt/kgLXLqp2dMdPREnHVDaVK9S3sw1eMkCmonwMl" +
			"ls5Aw9QkOO8aBtebHCCu0Zsu7eR/5QOiqr3c+q7Ag5n8eRE6bncCw4xCRiXtEZy3" +
			"mb5tsjp+cLxA2Hom4BWtMwIDAQAB";


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

			String bodyText = "";//message.getMessageBody();
			//bodyText = bodyText.replace("\n","");
			//if(message.getAuthor().equals("user1")) {
				try {
					bodyText = new String(rsaUtil.decryptByPrivateKey(message.getMessageBody().getBytes("utf-8"), RSA_PRIVATE_KEY1));
				} catch (Exception e) {
					e.printStackTrace();
				}
//			}else if(message.getAuthor().equals("user2")) {
//				try {
//					bodyText = new String(rsaUtil.decryptByPrivateKey(message.getMessageBody().getBytes("utf-8"), RSA_PRIVATE_KEY1));
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
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
