package com.genderrecognitionapp;

import java.io.File;
import android.app.ProgressDialog;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import com.wav.ButtonChoice;
import com.wav.WavIO;

/*
 * Class {@code ButtonChoice} :
 *  - ChoiceSelected
 *  - TrainMale
 *  - TrainFemale
 *  - Recognition
 *  - SignalProcessingOfWav
 * 
 * @author Ing. Andrea Sciarrone <andrea.sciarrone@unige.it>
 * @author Walter Cesarini <cesaw93@hotmail.it> 
 * @author Jacopo Fabrini <jacfabr@live.it>
 * @see App Multimediali e Internet del futuro
 * @since 2015
 */
public class Rec extends AsyncTask<String, String, Void> 
{
	private Context mContext;
	private final String TAG = "RecorderAudio";

	///Progress Bar
	private ProgressDialog mProgressRecorder = null, mProgressFeatures = null;

	///Registration Length
	int mRegistrationLenghtInSeconds;
	int mFS;
	int mNumberOfSamples;
	
	/// <b> Useful remainder </b>
	///@{ Due to the fact that people take a while between pressing the button
	/// and starting to talk. They take care about everything is started correctly.
	//@}
	final int waitPreRecording = 2000;
	
	///Audio Samples
	short[] mAudioData = null;

	///Recorder
	private AudioRecord mRecorder = null;
	
	public short flag = 0;

/////////////////////////////////////////////////////////////////////////////
	public Rec(Context context, int registrationLenght, int sampleFrequency, short[] audioData, short flag) 
	{
		this.mContext = context;
		this.mRegistrationLenghtInSeconds = registrationLenght;
		this.mFS = sampleFrequency;
		this.flag = flag;

		this.mNumberOfSamples = this.mFS * this.mRegistrationLenghtInSeconds;
		Log.i(TAG, "Registration length (second):" + mRegistrationLenghtInSeconds);
	}
/////////////////////////////////////////////////////////////////////////////

/////////////////////////////////////////////////////////////////////////////
@Override
protected void onPreExecute() 
{
	super.onPreExecute();

	mProgressRecorder = new ProgressDialog(mContext);
	mProgressRecorder.setIndeterminate(true);
	mProgressRecorder = ProgressDialog.show(mContext, "Recording Audio", "Speak Now!");
	mProgressFeatures = new ProgressDialog(mContext);
	mProgressFeatures.setIndeterminate(true);
	
	///@{ Object recorder in Android.
	///   'Media...MIC' is used to access in a static way to the integer that identify the micorphone
	///   'mNumberOfSamples * 2' is due to the fact that are 'byte', but we need to use 'short' 
	//@}
	mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, mFS, AudioFormat.CHANNEL_IN_MONO,  AudioFormat.ENCODING_PCM_16BIT, mNumberOfSamples * 2);
}
/////////////////////////////////////////////////////////////////////////////
	
/////////////////////////////////////////////////////////////////////////////
@Override
protected Void doInBackground(String... params) 
{
	/// In 'params[]' will be stored value of path and filename that I have passed from the 
	/// 'MainActivity' in the 'execute' of the asynctask
	String _path = params[0];
	String _filename = params[1];
	
	/// 'startRecording' è un API di Android
	Log.i(TAG, "REC: Start!");
	mRecorder.startRecording();

	mAudioData = new short[mNumberOfSamples];
	Log.i(TAG, "Number of sample (220.500):" + mNumberOfSamples);
	mRecorder.read(mAudioData , 0, mNumberOfSamples);
	
	
	////////////////////// Write WAV file ///////////////////////////////////
	String storeDir = Environment.getExternalStorageDirectory() + "/" + _path;
	File f = new File(storeDir);
	if(!f.exists())
		if(!f.mkdir())
			Log.e("Error","Can't create download directory");
	

	byte[] dataByte = new byte[2 * mAudioData.length];

	/** '0x00ff' is used to compute the ANDlogic in binary logic and isolate 8 bit per time.
	 * If we would like to store in an other audio format we could be but implementing a different header:
	 * there is the problem about the signal processing behind other audio format (e.g. MP3), .WAV is not
	 * compressed
	 */
	for (int i = 0; i < mAudioData.length; i++) 
	{
		dataByte[2*i] = (byte)(mAudioData[i] & 0x00ff);
		dataByte[2*i+1] = (byte)((mAudioData[i] >> 8) & 0x00ff);
	}

	/// Entering in the 'WavIO' class in order to read documentation
	WavIO writeWav = new WavIO(Environment.getExternalStorageDirectory() + "/" + _path + "/" + _filename,16, 1, 1, 44100, 2, 16, dataByte);
	writeWav.save();
	
	return null;
}
/////////////////////////////////////////////////////////////////////////////

/////////////////////////////////////////////////////////////////////////////
@Override
protected void onPostExecute(Void _v)
{
	super.onPostExecute(_v);

	mRecorder.stop();
	mRecorder.release();

	Log.i("OnClick", "REC: Stop!");

	mProgressRecorder.dismiss();
	mProgressRecorder = null;
	ButtonChoice choice = new ButtonChoice(this.mContext, mAudioData, flag);
	choice.ChoiceSelected();
}
/////////////////////////////////////////////////////////////////////////////
	
////////////////////////////////////////////////////////////////////////////
}
