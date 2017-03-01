package com.genderrecognitionapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.genderrecognitionapp.R;

/*
 * Class {@code MainActivity} :
 *  - OnCreate
 *  - ShowConfirmationPopUp (function)
 *  - GetDeviceId (function)
 * 
 * @author Walter Cesarini <cesaw93@hotmail.it> 
 * @author Jacopo Fabrini <jacfabr@live.it>
 * @see App Multimediali e Internet del futuro
 * @since 2015
 */
public class MainActivity extends Activity 
{
	private final String TAG = "MainActivity";
	private Button bttAudioRecorder;
	private Button bttTrainMale;
	private Button bttTrainFemale;
	private TextView textDeviceID;
	public static TextView pitchInfo;
	public static TextView thresholdInfo;
	private static Context context;
	private final int sampleFrequency = 44100;
	public static ImageView imageview;

	private final String PATH = "example";
	private final String FILE_NAME = "AudioRecorder.wav";
	short[] samples = null;
	short choiceFlag = 0;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		bttAudioRecorder = (Button) findViewById(R.id.btt_classify);
		textDeviceID = (TextView)findViewById(R.id.deviceid);
		bttTrainMale = (Button) findViewById(R.id.button_male);
		bttTrainFemale = (Button) findViewById(R.id.button_female);
		pitchInfo = (TextView)findViewById(R.id.textView7);
		thresholdInfo = (TextView)findViewById(R.id.textView8);
		context = this;
				
		
		/// Retrieve a reference to an instance of TelephonyManager
	    TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	    textDeviceID.setText(getDeviceID(telephonyManager));

		imageview= (ImageView)findViewById(R.id.imview1);		
		imageview.setImageResource(R.drawable.qmark_yellow);
		///////////////////////////////////////////////////////////////////////////////////
		bttTrainMale.setOnClickListener(new OnClickListener()
		{			
			public void onClick(View v)
			{
				choiceFlag = 1;
				Log.i(TAG, "Begin recording...");
				Rec rec = new Rec(context, 5, sampleFrequency, samples, choiceFlag);
				rec.execute(PATH, FILE_NAME);
			}	
		});
		////////////////////////////////////////////////////////////////////////////////////
				
		////////////////////////////////////////////////////////////////////////////////////
		bttTrainFemale.setOnClickListener(new OnClickListener()
		{				
			public void onClick(View v)
			{
				choiceFlag = 2;
				Log.i(TAG, "Begin recording...");
				Rec rec = new Rec(context, 5, sampleFrequency, samples, choiceFlag);
				rec.execute(PATH, FILE_NAME);
			}
		});
		/////////////////////////////////////////////////////////////////////////////////////

		//////////////////////////////////////////////////////////////////////////////////////
		/// 'rec' object is executed with 'rec.execute' with the same syntax we have seen
		/// used for the asynctask (as 'rec' is).
		bttAudioRecorder.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				choiceFlag = 3;
				Log.i(TAG, "Begin recording...");
				Rec rec = new Rec(context, 5, sampleFrequency, samples, choiceFlag);
				rec.execute(PATH, FILE_NAME);
			}
		});
	}
///////////////////////////////////////////////////////////////////////////////////////////			
	
///////////////////////////////////////////////////////////////////////////////////////////	
/// In order to link 'custom_dialog.xml' with the XML Activity we have created this class and
/// we have linked with 'setContentView'.
/// The differences between the previous code is that, here, we have to specify the related layout
/// 'confirmationPopUp.findViewById' by dot operator: this indicate that the image we are using is 
/// the 'custom_dialog.xml' one, defined in the initialization of the 'confirmationPopUp' object
/// [Show confirmation of recording]: (@ref showConfirmationPopUp)
public static void showConfirmationPopUp(String title, String text, int image) 
{
	final Dialog confirmationPopUp = new Dialog(context);
	confirmationPopUp.setContentView(R.layout.custom_dialog);

	/// Set the dialog components
	TextView _text = (TextView) confirmationPopUp.findViewById(R.id.text);
	String _t = "<b>" + text + "</b> ";
	_text.setText(Html.fromHtml(_t));

	ImageView _i = (ImageView) confirmationPopUp.findViewById(R.id.image);
	_i.setImageResource(image);

	Button ok = (Button) confirmationPopUp.findViewById(R.id.dialogButtonOK);
	/// If button is clicked, close the custom dialog
	ok.setOnClickListener(new OnClickListener() 
	{
		@Override
		public void onClick(View v) 
		{
			confirmationPopUp.dismiss();
		}
	});

	confirmationPopUp.setTitle(title);
	confirmationPopUp.show();					
}
///////////////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////////
/// [Get Device ID]: (@ref getDeviceID)
String getDeviceID(TelephonyManager phonyManager)
{
	 String id = phonyManager.getDeviceId();
	 if (id == null)
	{
	  id = "not available";
	 }

	 int phoneType = phonyManager.getPhoneType();
	 switch(phoneType)
	 {
	 	case TelephonyManager.PHONE_TYPE_NONE:
	 		return "NONE: " + id;

	 	case TelephonyManager.PHONE_TYPE_GSM:
	 		return "GSM: IMEI=" + id;

	 	case TelephonyManager.PHONE_TYPE_CDMA:
	 		return "CDMA: MEID/ESN=" + id;
		 /**
		  *  for API Level 11 or above
		  *  case TelephonyManager.PHONE_TYPE_SIP:
		  *   return "SIP";
		  */		
	 	default:
	 		return "UNKNOWN: ID=" + id;
	 }
 }
///////////////////////////////////////////////////////////////////////////////////////////
}
