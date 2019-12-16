import ij.*;
import ij.io.Opener;
import ij.IJ;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import java.awt.Color;
import ij.plugin.*;
import ij.plugin.frame.*;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import ij.ImagePlus;

public class Matching implements PlugIn {

	public void run(String args) {
		
	ImagePlus imp1 = IJ.getImage();
	ImageProcessor ip1 = imp1.getChannelProcessor(); //the targer image
	
	Opener opener = new Opener();  
	String imageFilePath = "C:\\Users\\lharutyunyan\\Desktop\\48a.jpg"; // the reference image
	ImagePlus imp2 = opener.openImage(imageFilePath);
	ImageProcessor ip2 = imp2.getChannelProcessor(); // ImageProcessor from ImagePlus
	
	
	double[] ip1RED = normCumulativeHist(ip1, "RED");
	double[] ip1GREEN = normCumulativeHist(ip1, "GREEN");
	double[] ip1BLUE = normCumulativeHist(ip1, "BLUE");  //target image histograms
	
	double[] ip2RED = normCumulativeHist(ip2, "RED");
	double[] ip2GREEN = normCumulativeHist(ip2, "GREEN");
	double[] ip2BLUE = normCumulativeHist(ip2, "BLUE"); //reference image, benchmark histograms
	
	int[] matchRED = matchHistograms(ip1RED, ip2RED);
	int[] matchGREEN = matchHistograms(ip1GREEN, ip2GREEN);
	int[] matchBLUE = matchHistograms(ip1BLUE, ip2BLUE);
	
	applyTable(ip1, matchRED, matchGREEN, matchBLUE);
	}
	
	
	public void applyTable(ImageProcessor ip, int[] matchRED, int[] matchGREEN,int[] matchBLUE) {
		int M = ip.getWidth();
		int N = ip.getHeight();
		Color val;
		for(int i=0; i<M; i++) {
			for(int j=0; j<N; j++) {
				val = new Color(ip.getPixel(i, j));
				int[] arr = new int[]{matchRED[val.getRed()], matchGREEN[val.getGreen()], matchBLUE[val.getBlue()]};
				ip.putPixel(i, j, arr); 
		}
	}
}	
	
	public int[] matchHistograms (double[] hA, double[] hR) { //this function is taken from the book, page 73
		int K = hA.length; 
		int[] F = new int[K]; 
		for (int a = 0; a < K; a++) {
			int j = K - 1;
			do {
				F[a] = j;
				j--;
			} while (j >= 0 && hA[a] <= hR[j]);
		}
		return F;
	}
		
	public double[] normCumulativeHist(ImageProcessor ip, String channel) {
		int M = ip.getWidth();
		int N = ip.getHeight();
		int K = 256; // number of intensity values
	
		if(channel.equals("RED")) {
			ColorProcessor.setWeightingFactors(1,0,0);	
		}
		else if(channel.equals("GREEN")) {
			ColorProcessor.setWeightingFactors(0,1,0);	
		}
		else {
			ColorProcessor.setWeightingFactors(0,0,1);	
		}
		int[] H = ip.getHistogram();
		double[] L = new double[H.length];  
		int pixelNumber=M*N;
			
		L[0]= (double)(H[0])/pixelNumber;
		for (int i = 1; i < H.length; i++) {
			L[i] = (double)(H[i])/pixelNumber; //normalize
			L[i] = L[i - 1] + L[i];            //cumulative histogram
		}
		return L; //normalized cumulative
	}
}