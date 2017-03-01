package com.wav;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import com.example.genderrecognitionapp.R;
import com.genderrecognitionapp.MainActivity;

/*
 * Class {@code ButtonChoice} is implement even method:
 *  - ChoiceSelected
 *  - TrainMale
 *  - TrainFemale
 *  - Recognition
 *  - SignalProcessingOfWav
 * 
 * @author Walter Cesarini <cesaw93@hotmail.it> 
 * @author Jacopo Fabrini <jacfabr@live.it>
 * @see App Multimediali e Internet del futuro
 * @since 2015
 */
public class ButtonChoice 
{
	private Context context = null;
	short[] audioData = null;
	short flag = 0;
	private final String TAG = "ButtonChoice";
	
///////////////////////////////////////////////////////////////////////////////////////////
	public ButtonChoice(Context c, short[] audioData, short flag)
	{
		this.context = c;
		this.audioData = audioData;
		this.flag = flag;
	}
///////////////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////////
/// [Choice menu]: (@ref ChoiceSelected)
public void ChoiceSelected()
{
	switch(flag)
	{
		case 1:
			TrainMale();
			break;
		case 2:
			TrainFemale();
			break;
		case 3:
			Recognition();
			break;
	}
}
///////////////////////////////////////////////////////////////////////////////////////////
	
///////////////////////////////////////////////////////////////////////////////////////////
/// [Male training]: (@ref TrainMale)
private void TrainMale()
{
	File externalStorageDir = Environment.getExternalStorageDirectory();
	File myFile = new File(externalStorageDir ,"male.txt");
	
	if(myFile.exists())
	{
	   try
	   {
		   Log.i(TAG, "Open data file...");
	       BufferedWriter writer = new BufferedWriter(new FileWriter(myFile, true));
	       //writer.flush();
	       Log.i(TAG, "Begin processing...");
	       
	       int pitch = SignalProcessingOfWav (audioData);
	       writer.write(pitch + "\n");
	       MainActivity.pitchInfo.setText("PITCH(male): "+ pitch);
	       writer.close();
	       
	       Log.i(TAG, "Male trained");
	       Toast.makeText(this.context ,"Male trained!",Toast.LENGTH_SHORT).show();
	    } 
	   catch(IOException e)
	    {
		   e.printStackTrace();
	    }
	}
	else
	{
		try 
		{						
			myFile.createNewFile();
			FileOutputStream fOut = new FileOutputStream(myFile);
			Log.i(TAG, "Open existing .txt for adding value...");
			OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
			
			int pitch = SignalProcessingOfWav (audioData);
			Log.i(TAG, "Pitch: " + pitch);
			myOutWriter.write(pitch + "\n");
			
			Log.i(TAG, "Added value...");
			myOutWriter.close();
			fOut.close();
			Toast.makeText(this.context,"Male trained!",Toast.LENGTH_SHORT).show();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}	
}
///////////////////////////////////////////////////////////////////////////////////////////
		
///////////////////////////////////////////////////////////////////////////////////////////
/// [Female training]: (@ref TrainFemale)
private void TrainFemale()	
{
	File externalStorageDir = Environment.getExternalStorageDirectory();
	File myFile = new File(externalStorageDir ,"female.txt");
	
	if(myFile.exists())
	{
	   try
	   {
	       BufferedWriter writer = new BufferedWriter(new FileWriter(myFile, true));
	       //writer.flush();
	       
	       int pitch = SignalProcessingOfWav (audioData);
	       MainActivity.pitchInfo.setText("PITCH (female):" + pitch);	
	       writer.write(pitch + "\n");
	       
	       writer.close();
	       Toast.makeText(this.context,"Female trained!",Toast.LENGTH_SHORT).show();
	    } 
	   catch(IOException e)
	    {
		   e.printStackTrace();
	    }
	}
	else
	{
		try 
		{						
			myFile.createNewFile();
			FileOutputStream fOut = new FileOutputStream(myFile);
			OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
			
			int pitch = SignalProcessingOfWav (audioData);
			myOutWriter.write(pitch + "\n");		
			
			myOutWriter.close();
			fOut.close();
			Toast.makeText(this.context,"Female trained!",Toast.LENGTH_SHORT).show();
		}
		catch(IOException e)
	    {
		   e.printStackTrace();
	    }
	}
}
///////////////////////////////////////////////////////////////////////////////////////////
	
///////////////////////////////////////////////////////////////////////////////////////////
/// [Speaker recognition]: (@ref Recognition)
private void Recognition()
{
	int pitchFreq = SignalProcessingOfWav(audioData);
	MainActivity.pitchInfo.setText("Pitch of the speaker: " + pitchFreq);
	List<Integer> male = new ArrayList<Integer>();
	List<Integer> female = new ArrayList<Integer>();
	double totalSumMl = 0;
	double totalSumFm = 0;
	
	/// Male value reading from file
	try 
	{
		 Log.i(TAG, "Open male.txt...");
		File myFile = new File("/sdcard/male.txt");
		FileInputStream fIn = new FileInputStream(myFile);
		BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
		String aDataRow = "";
		 Log.i(TAG, "Reading male.txt...");
		while ((aDataRow = myReader.readLine()) != null) 
		{
			male.add(Integer.parseInt(aDataRow));
		}
		myReader.close();				
	}
	catch(IOException e)
    {
	   e.printStackTrace();
    }
	
	for(int i = 0; i < male.size(); i++)
	{
		totalSumMl += male.get(i);
	}
	
	Log.i(TAG, "Calculating male average...");
	double maleAverage = (totalSumMl / male.size());
	Log.i(TAG, "Male average is:" + maleAverage);
	
	/// Male value reading from file
	try 
	{
		Log.i(TAG, "Open female.txt...");
		File myFile = new File("/sdcard/female.txt");
		FileInputStream fIn = new FileInputStream(myFile);
		BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
		String aDataRow = "";
		Log.i(TAG, "Reading female.txt...");
		while ((aDataRow = myReader.readLine()) != null) 
		{
			female.add(Integer.parseInt(aDataRow));
		}
		myReader.close();				
	}
	catch(IOException e)
    {
	   e.printStackTrace();
    }
	
	for(int i = 0; i < female.size(); i++)
	{
		totalSumFm += female.get(i);
	}
	
	Log.i(TAG, "Calculating female average...");
	double femaleAverage = (totalSumFm / female.size());
	Log.i(TAG, "Female average is:" + femaleAverage);
	
	double differenceFm = femaleAverage - pitchFreq; 
	Log.i(TAG, "Difference Female:" + differenceFm);
	double differenceMl = maleAverage - pitchFreq;
	Log.i(TAG, "Difference Male:" + differenceMl);
	
	int threshold = (int)((femaleAverage + maleAverage)/2);
	MainActivity.thresholdInfo.setText("THRESHOLD: " + threshold);
	
	 Log.i(TAG, "Starting comparison between value taken from the two .txt");
	if(Math.abs(differenceMl) < Math.abs(differenceFm))
	{
		Toast.makeText(this.context,"You are a man!",Toast.LENGTH_SHORT).show();
		MainActivity.imageview.setImageResource(R.drawable.male_logo);
	}
	else if(Math.abs(differenceMl) > Math.abs(differenceFm))
	{
		Toast.makeText(this.context,"You are a woman!",Toast.LENGTH_SHORT).show();
		MainActivity.imageview.setImageResource(R.drawable.female_logo);
	}
	else
	{
		Toast.makeText(this.context,"You are an hermaphrodite?!",Toast.LENGTH_SHORT).show();
	}
}
///////////////////////////////////////////////////////////////////////////////////////////
	
///////////////////////////////////////////////////////////////////////////////////////////
/// [Signal processing of wav file]: (@ref SignalProcessingOfWav)
public int SignalProcessingOfWav (short[] audioData)
{
	Framing framing = new Framing(audioData);
	Log.i(TAG, "Created framing object");
	int[][] frames = framing.CreateFramesArray();
	SignalProcessing processor = new SignalProcessing(frames);
	double[][] fftValueOutput = processor.FFT();
	double[][] spectrum = processor.computeSpectrum(fftValueOutput);
	int[] pitchOfFrames = processor.getPitchOfEveryFrames(spectrum);
	int finalPitch = processor.findFinalPitch(pitchOfFrames);
	Log.i(TAG, "Value of pitch:" + finalPitch);
	return finalPitch;
}
///////////////////////////////////////////////////////////////////////////////////////////
}
