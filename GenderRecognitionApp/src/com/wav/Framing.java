package com.wav;

import android.util.Log;

/*
 * Class {@code Framing} is implement even method:
 *  - CreateFramesArray
 * 
 * @author Walter Cesarini <cesaw93@hotmail.it> 
 * @author Jacopo Fabrini <jacfabr@live.it>
 * @see App Multimediali e Internet del futuro
 * @since 2015
 */
public class Framing 
{		
	private final String TAG = "Framing";
	private final int SAMPLE_FREQUENCY = 44100;
	private final double FRAME_LENGTH = 0.025;
	private final double NUMBER_OF_SAMPLE_PER_FRAME = (SAMPLE_FREQUENCY * FRAME_LENGTH);
	byte[] initforDFT = new byte [4410];
	private short[] audioData = null;
	
	
	
	public Framing(short[] audioData) 
	{
		this.audioData = audioData;
	}
	
///////////////////////////////////////////////////////////////////////////////		
		
///This method fills the array "frames" with the samples from the "buffer" array.
///It executes also the overlapping (50%) shifting through the sample of
///1102 (=> 2204/2).
/// [Create Array of Frames]: (@ref CreateFramesArray)
public int[][] CreateFramesArray()
{
	///Here we calculate the number of frame (25 ms) that the file contains.
	///In order to do that we divide buffer for 1102.5 (=> ((44.1*25))
	///(We found that usually are used 2 bytes for sample)
	int numberOfFrames = (int)((audioData.length) / NUMBER_OF_SAMPLE_PER_FRAME);
	Log.i(TAG, "Number of frame:" + audioData.length);
	
	///Here we create a bytes bidimensional array in which the first index
	///corresponds to the position of the frame.
	///The second index is the position of the sample inside the frame. 
	int[][] frames = new int[numberOfFrames] [(int)NUMBER_OF_SAMPLE_PER_FRAME];
	
	int flagFrames = 0;
	int flagBytes = 0;
	
	for(int i = 0; i < numberOfFrames; i++)
	{
		for(int j = 0; j < NUMBER_OF_SAMPLE_PER_FRAME - 1; j++)
		{
			frames[i][j] = audioData[flagBytes];
			flagBytes++;
		}
		
		flagFrames++;
		
		/// Overlapping of 50 per cent
		flagBytes = flagFrames * (((int)NUMBER_OF_SAMPLE_PER_FRAME - 1)/2);
	}
	Log.i(TAG, "Bidimesional frames array created");
	return frames;
}
///////////////////////////////////////////////////////////////////////////////	
}
