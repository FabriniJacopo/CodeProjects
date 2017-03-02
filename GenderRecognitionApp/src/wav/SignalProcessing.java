package com.wav;

import org.jtransforms.fft.DoubleFFT_1D;
import android.util.Log;

/*
 * Class {@code SignalProcessing} is implement even method:
 *  - FFT
 *  - ComputeSpectrum
 *  - GetPitchOfEveryFrame
 *  - FindFinalPitch
 * 
 * @author Walter Cesarini <cesaw93@hotmail.it> 
 * @author Jacopo Fabrini <jacfabr@live.it>
 * @see App Multimediali e Internet del futuro
 * @since 2015
 */
public class SignalProcessing
{
	short[] audioData = null;
	int[][] frames = null;
	private final String TAG = "SignalProcessing";
	private final int SAMPLE_FREQUENCY = 44100;
	private final double FRAME_LENGTH = 0.025;
	private final int NUMBER_OF_SAMPLE_PER_FRAME = (int)(SAMPLE_FREQUENCY * FRAME_LENGTH);
	private int NUMBER_OF_FRAMES = -1;
	double[][] fftValueOutput;
	double[][] spectrum;
	int[] pitchOfFrames;
	int finalPitch;

////////////////////////////////////////////////////////////////////////////////////////////////
	public SignalProcessing(int[][] frames)
	{
		this.frames = frames;
	
		NUMBER_OF_FRAMES = frames.length;
		Log.i(TAG, "Number of frames:" + NUMBER_OF_FRAMES); 
	}
////////////////////////////////////////////////////////////////////////////////////////////////
	
////////////////////////////////////////////////////////////////////////////////////////////////	
/// Define compute the FFT and return matrix of value coupled in real and image part 
///	(because it's a complex number!)
	/**Documentation of 'FFT' algorithm:
	  * \link<FFT>{https://sites.google.com/site/piotrwendykier/software/jtransforms}
	  */
/// [FFT]: (@ref FFT)
public double[][] FFT()
{
	/// Matrix which stores the n° of frames needed for the FFT computing for every frame.
	/// (The product for two is due to the complex exit of the FFT)
	fftValueOutput = new double[NUMBER_OF_FRAMES][2* NUMBER_OF_SAMPLE_PER_FRAME];
	
	/// This is the array that, at first, contains the samples of the frame in time domain, 
	/// after,the same samples but in the Fourier domain
	double[] singleFrame = new double[2* NUMBER_OF_SAMPLE_PER_FRAME];

	for (int i = 0; i < NUMBER_OF_FRAMES; i++) 
	{

		/// Single frame
		for (int j = 0; j < NUMBER_OF_SAMPLE_PER_FRAME; j++) 
		{
			singleFrame[j] = (double)frames[i][j];
		}

		///Compute FFT
		DoubleFFT_1D fftSingleFrame = new DoubleFFT_1D(NUMBER_OF_SAMPLE_PER_FRAME);
		fftSingleFrame.realForwardFull(singleFrame);
		
		for (int j = 0; j < singleFrame.length; j++) 
		{	
			fftValueOutput[i][j] = singleFrame[j];
		}
	}
	return fftValueOutput;
}
////////////////////////////////////////////////////////////////////////////////////////////////
	
////////////////////////////////////////////////////////////////////////////////////////////////
/// [Compute Spectrum]: (@ref computeSpectrum)
public double[][] computeSpectrum (double [][] fftValueOutput)
{
	spectrum = new double[fftValueOutput.length][fftValueOutput[0].length/2];
	
	for(int i =0; i< fftValueOutput.length; i++)
	{
		for(int j = 0; j<fftValueOutput[i].length; j+=2)
		{
			double realPart = 0;
			double imagePart = 0;
			
			realPart = fftValueOutput[i][j];
			imagePart = fftValueOutput[i][j+1];
			
			realPart = Math.pow(Math.abs(realPart), 2);
			imagePart = Math.pow(Math.abs(imagePart), 2);
			 
			double amplitude = Math.sqrt(realPart + imagePart);
			
			/// Index (j+1)/2 -> due to the fact we have any more real and image part
			spectrum[i][(j+1)/2] = amplitude;
		}
	}
	return spectrum;
}
////////////////////////////////////////////////////////////////////////////////////////////////
	
////////////////////////////////////////////////////////////////////////////////////////////////
/// [Get Pitch of every frames]: (@ref getPitchOfEveryFrames)
public int[] getPitchOfEveryFrames(double[][] spectrum)
{
	int freqIndex = 0;
	int freqPitch = 0;
	pitchOfFrames = new int[NUMBER_OF_FRAMES];
	
	int NUMBER_SAMPLES_PER_SPECTRUM = spectrum[0].length / 2;
	Log.i(TAG, "Spectrum length: " + spectrum[0].length);
	
	/// 'frequencyStep' represents the frequency gap between the samples 
	int frequencyStep = (SAMPLE_FREQUENCY / (NUMBER_SAMPLES_PER_SPECTRUM*2));
	int frequencyArray[] = new int[NUMBER_SAMPLES_PER_SPECTRUM];
	
	for(int k = 0; k < NUMBER_SAMPLES_PER_SPECTRUM; k++)
	{
		frequencyArray[k] = k * frequencyStep;
		Log.i(TAG, "frequencyArray[" + k +"]: " + frequencyArray[k]);
	}
		
	for(int i = 0; i< NUMBER_OF_FRAMES; i++)
	{
		double max = 0;
					
		for(int j = 0; j< NUMBER_SAMPLES_PER_SPECTRUM; j++)
		{
			/// This block finds the index related to the maximum value of amplitude of the spectrum
			/// for every frame
			if (spectrum[i][j] > max && j<= 8)
			{
				max = spectrum[i][j];
				freqIndex = j;
			}		
		}
		
		freqPitch = frequencyArray[freqIndex];
		
		/// Links the frequency with the right sample
		pitchOfFrames[i] = freqPitch;
		Log.i(TAG, "Max pitch of frame:" + freqPitch);
	}
	return pitchOfFrames;
}
////////////////////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////////////////////
/// [Find final pitch]: (@ref findFinalPitch)
public int findFinalPitch(int[] pitchOfFrames)
{
	int sumOfPitch = 0;
	
	for(int i = 0; i< pitchOfFrames.length; i++)
	{
		sumOfPitch += pitchOfFrames[i];
	}
	
	/// This line compute the average of pitch frequency for every frame
	finalPitch = sumOfPitch/(pitchOfFrames.length);
	return finalPitch;
}
////////////////////////////////////////////////////////////////////////////////////////////////

}
