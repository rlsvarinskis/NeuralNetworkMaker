package ai.app.data;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import ai.network.utils.Data;

public class InputsOutputs
{
	public static final File SOURCE_DIRECTORY = new File("D:\\Programming\\Data-zone\\AI\\Characters\\");
	
	//public static final String[] labels = "A B C D E F G H I J K L M N O P Q R S T U V W X Y Z a b c d e f g h i j k l m n o p q r s t u v w x y z 0 1 2 3 4 5 6 7 8 9 . , ! ? * ( ) - + = [ ] { } ; : ' \" < > Diacritic".split(" ");
	//public static final String[] labels = "A B C D E F G H I J K L M N O P Q R S T U V W X Y Z a b c d e f g h i j k l m n o p q r s t u v w x y z 0 1 2 3 4 5 6 7 8 9 . , ! ? ( ) - ; : \" Diacritic".split(" ");
	public static final String[] labels = "A B C D E F G H I J K L M N O P Q R S T U V W X Y Z Diacritic".split(" ");
	
	//public static char[] outputs = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789.,!?*()-+=[]{};:'\"<>~".toCharArray();
	//public static char[] outputs = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789.,!?()-;:\"~".toCharArray();
	public static char[] outputs = "ABCDEFGHIJKLMNOPQRSTUVWXYZ~".toCharArray();
	
	//private static float[][] answers;
	
	public static ArrayList<String> name = new ArrayList<>();
	public static ArrayList<Data> data = new ArrayList<>();
	public static ArrayList<BufferedImage> thumbnail = new ArrayList<>();
	
	static
	{
		File[] files = getAllFiles();
		
		for (int i = 0; i < files.length; i++)
		{
			try
			{
				FileInputStream in = new FileInputStream(files[i]);
				
				Data d = read(in);
				
				if (d != null)
				{
					name.add(files[i].getName());
					data.add(d);
					thumbnail.add(getThumbnail(d));
				}
				
				in.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public static Data read(InputStream in) throws IOException
	{
		ByteBuffer intBuffer = ByteBuffer.allocate(4);
		if (in.read(intBuffer.array()) != 4)
			return null;
		
		float[] inputs = new float[intBuffer.getInt()];
		
		ByteBuffer floatBuffer = ByteBuffer.allocate(inputs.length * 4);
		if (in.read(floatBuffer.array()) != inputs.length * 4)
			return null;
		
		floatBuffer.asFloatBuffer().get(inputs);
		
		intBuffer.position(0);
		
		if (in.read(intBuffer.array()) != 4)
			return null;
		
		float[] outputs = new float[intBuffer.getInt()];
		
		floatBuffer = ByteBuffer.allocate(outputs.length * 4);
		if (in.read(floatBuffer.array()) != outputs.length * 4)
			return null;
		
		floatBuffer.asFloatBuffer().get(outputs);
		
		float[] trimmedO = new float[InputsOutputs.outputs.length];
		
		System.arraycopy(outputs, 0, trimmedO, 0, trimmedO.length);
		
		trimmedO[trimmedO.length - 1] = outputs[outputs.length - 1];
		
		return new Data(inputs, trimmedO);
	}
	
	public static void save(Data d, String name) throws IOException
	{
		File f = new File(SOURCE_DIRECTORY, name + ".dat");
		f.createNewFile();
		save(d, new FileOutputStream(f));
	}
	
	public static void save(Data d, OutputStream out) throws IOException
	{
		float[] inputs = d.getInputs();
		float[] outputs = d.getOutputs();
		
		ByteBuffer bb = ByteBuffer.allocate(4 + inputs.length * 4 + 4 + outputs.length * 4);
		bb.putInt(inputs.length);
		bb.asFloatBuffer().put(inputs);
		bb.position(bb.position() + inputs.length * 4);
		bb.putInt(outputs.length);
		bb.asFloatBuffer().put(outputs);
		
		out.write(bb.array());
	}
	
	public static BufferedImage getThumbnail(Data d) throws IOException
	{
		//TODO make this dynamic
		float[] inputs = d.getInputs();
		
		BufferedImage bi = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
		
		for (int i = 0; i < 30; i++)
		{
			for (int j = 0; j < 30; j++)
			{
				int v = (int) (inputs[i + j * 30] * 255);
				bi.setRGB(i, j, (255 << 24) | (v << 16) | (v << 8) | (v));
			}
		}
		
		return bi;
	}
	
	public static File[] getAllFiles()
	{
		return SOURCE_DIRECTORY.listFiles(new FilenameFilter()
		{
			public boolean accept(File dir, String name)
			{
				if (name.toLowerCase().endsWith(".dat"))
					return true;
				return false;
			}
		});
	}
}
