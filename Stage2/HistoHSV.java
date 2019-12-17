import ij.*;
import ij.IJ;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.*;
import ij.plugin.frame.*;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import ij.ImagePlus;

public class HistoHSV implements PlugIn {

	public void run(String args) {
	
		ImagePlus imp = IJ.getImage();
		ImageProcessor ip = imp.getChannelProcessor();
		
        int M = ip.getWidth();
        int N = ip.getHeight();
        Color color;
		
		int r;
		int g;
		int b;
		
        float[] h = new float[360];
        float[] s = new float[101];
        float[] v = new float[101];
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
		//System.out.println("Channel:  "+ "Hue" + "  Intensity:  " + "0" + "  Value:  " + h[0]);
        for (int i = 1; i < 360; i++) {
			h[i] = h[i] / (M * N);
            h[i] = h[i - 1] + h[i];
			//System.out.println("Channel:  "+ "Hue" + "  Intensity:  " + i + "  Value:  " + h[i]);
        } 
		s[0] = s[0] / (M * N);
        v[0] = v[0] / (M * N);
		//System.out.println("Channel:  "+ "Saturation" + "  Intensity:  " + "0" + "  Value:  " + s[0]);
		System.out.println("Channel:  "+ "Value" + "  Intensity:  " + "0" + "  Value:  " + v[0]);
        for (int i = 1; i < 101; i++) {
			s[i] = s[i] / (M * N);
            v[i] = v[i] / (M * N);
            s[i] = s[i - 1] + s[i];
            v[i] = v[i - 1] + v[i];
			//System.out.println("Channel:  "+ "Saturation" + "  Intensity:  " + i + "  Value:  " + s[i]);
	    	System.out.println("Channel:  "+ "Value" + "  Intensity:  " + i + "  Value:  " + v[i]);
        }
    }
}