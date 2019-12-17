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

public class Matching_HSV implements PlugIn {

	public void run(String args) {
		
	ImagePlus imp1 = IJ.getImage();
	ImageProcessor ip1 = imp1.getChannelProcessor(); //the target image
	
	Opener opener = new Opener();  
	String imageFilePath = "C:\\Users\\lharutyunyan\\Desktop\\48a.jpg"; // the reference image
	ImagePlus imp2 = opener.openImage(imageFilePath);
	ImageProcessor ip2 = imp2.getChannelProcessor(); // ImageProcessor from ImagePlus
	
	float[] h1 = new float[360];
    float[] s1 = new float[101];
    float[] v1 = new float[101];
	
	float[] h2 = new float[360];
    float[] s2 = new float[101];
    float[] v2 = new float[101];
	
	normCumulativeHist(ip1, h1, s1, v1);
	normCumulativeHist(ip2, h2, s2, v2);
	
	int[] matchHUE = matchHistograms(h1, h2);
	int[] matchSATURATION = matchHistograms(s1, s2);
	int[] matchVALUE = matchHistograms(v1, v2);
	
	applyTable(ip1, matchHUE, matchSATURATION, matchVALUE);
	}
	
	public void applyTable(ImageProcessor ip, int[] matchHUE, int[] matchSATURATION,int[] matchVALUE) {
		int M = ip.getWidth();
		int N = ip.getHeight();
		Color val;
		int r;
		int g;
		int b;
		int[] rgb;
		float[] hsv  = new float[3];
		for(int i=0; i<M; i++) {
			for(int j=0; j<N; j++) {
				val = new Color(ip.getPixel(i, j));
				r = val.getRed();
                g = val.getGreen();
                b = val.getBlue();
                Color.RGBtoHSB (r, g, b, hsv);
				rgb = Color.HSBtoRGB(1.0*matchHUE[(int)(hsv[0])*360]/360.0, 1.0*matchSATURATION[(int)(hsv[1])*100]/101.0, 1.0*matchVALUE[(int)(hsv[2])*100]/101.0);
				ip.putPixel(i, j, rgb); 
		}
	}
}	
	
	public int[] matchHistograms(float[] hA, float[] hR) { //this function is taken from the book, page 73
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
	
	 
		
	public void normCumulativeHist(ImageProcessor ip, float[] h, float[] s, float[] v) {
		int M = ip.getWidth();
        int N = ip.getHeight();
        Color color;
		int r;
		int g;
		int b;
		float[] hsv  = new float[3];

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) { 
                color = new Color(ip.getPixel(j,i));
                r = color.getRed();
                g = color.getGreen();
                b = color.getBlue();
                Color.RGBtoHSB (r, g, b, hsv);
                h[(int)(hsv[0]*360)] = h[(int)(hsv[0]*360)]+1;
                s[(int)(hsv[1]*100)] = s[(int)(hsv[1]*100)]+1;
                v[(int)(hsv[2]*100)] = v[(int)(hsv[2]*100)]+1;
            }
        }
		h[0] = h[0] / (M * N);
        for (int i = 1; i < 360; i++) {
			h[i] = h[i] / (M * N);
            h[i] = h[i - 1] + h[i];
        } 
		s[0] = s[0] / (M * N);
        v[0] = v[0] / (M * N);
        for (int i = 1; i < 101; i++) {
			s[i] = s[i] / (M * N);
            v[i] = v[i] / (M * N);
            s[i] = s[i - 1] + s[i];
            v[i] = v[i - 1] + v[i];
		}
	}
}