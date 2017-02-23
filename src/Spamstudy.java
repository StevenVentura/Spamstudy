import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;


public class Spamstudy
{
	
/*
Spamstudy.java plan:
take in a file with comma separated lines
example
Texas,Austin
(will ignore case)
*/

/*

GUI layout:
A display with the letter
an input on the bottom asking what it is
tells you you are wrong until you get it right, then moves on.


So, a JTextArea (jscrollpane) and then a JTextField.
*/
	
	public Spamstudy(String arguments)
	{
		System.out.println("arguments = " + arguments);
		String[] splittho = arguments.split(" ");
		desiredFinalRight = Integer.parseInt(splittho[0]);
		desiredTimesRight = Integer.parseInt(splittho[1]);
		String fileName = ".//" + splittho[2];
		ArrayList<String> hiraganas = new ArrayList<String>();
		ArrayList<ArrayList<String>> romajis = new ArrayList<ArrayList<String>>();
		try{

			Scanner scan = new Scanner(new File(fileName),"UTF-8");
			
			
			while(scan.hasNextLine())
			{
				ArrayList<String> thisRomajis = new ArrayList<String>();
				String line = scan.nextLine();
				String[] split = line.split(",");
				hiraganas.add(split[0]);
				for (int i = 1; i < split.length; i++)
				{
					thisRomajis.add(split[i]);
				}
				
				romajis.add(thisRomajis);
			}
			if (scan.ioException() != null) {
		        throw scan.ioException();
		      }
			tr = new int[hiraganas.size()];
			scan.close();
		
		}catch(Exception e){e.printStackTrace();
		System.out.println("somehow it made it there and didnt have correct name or something");
		this.makeDefaultSettingsFile();
		System.exit(2);
		}
		
		Hiragana = hiraganas;
		RomajiForHiragana = romajis;
	}
	
	public EditGUI gui;
	public void begin()
	{
		
		gui = new EditGUI(this);
		
		
		
		gui.println(Hiragana.get(current));
		
		
	}
	
	
	public int current = 0;
	
	public final ArrayList<String> Hiragana; 		
	public final ArrayList<ArrayList<String>> RomajiForHiragana;
	
	
	
	
	
	public int[] tr;
	public int desiredFinalRight = 12;
	public int timesRight = 0;
	public int desiredTimesRight = 3;
	
	
	public static String loadOptionsFromFile()
	{
		try{
		
		String filename = "";
		{
			Scanner scan = new Scanner(new File("spamstudysettings.txt"));
			
			//pass it over as a string cos static and shit
			String xdesiredFinalRight = scan.nextLine().split(" ")[1];
			String xdesiredTimesRight = scan.nextLine().split(" ")[1];
			
			return xdesiredFinalRight + " " + xdesiredTimesRight;
			
		}
		
		
		
		}catch(Exception e)
		{
			//relying on this xd 
			makeDefaultSettingsFile();
			return null;
		}
		
	}
	public static void makeDefaultSettingsFile()
	{
		try{
		try (Writer file = new BufferedWriter(new OutputStreamWriter(
	              new FileOutputStream("spamstudysettings.txt"), "utf-8"))) {
	   
		file.write("desiredFinalRight: 12\r\n"
				+ "desiredTimesRight: 3\r\n"
				+ "\r\n###########################"
				+ ""
				+ "\r\n\r\nControls:"
				+ "\r\nenter a question mark to get the answer. Type the answer in the top blank."
				+ "here is an example input file:\r\n"
				+ "一,one,ichi,itsu,hitotsu\r\n"
				+ "二,two,ni,futatsu\r\n"
				+ "\r\nyou need to make the spaminput.txt yourself manually\r\n"
				+ "  and then  \r\n" 
				+ "put all that your lines study stuff in the file.\r\n"
				+ "and then re-run doubleclick the jar file again."
				+ "\r\n\r\ndesiredFinalRight is the number of times you have to get each entry correct before the program exits.\r\n"
				+ " DesiredtimesRight is the number of times you need to spam type it in a row.\r\n");
			
			file.close();
	}
		
		}catch(Exception e){e.printStackTrace();}
		finally
		{
			System.out.println("didnt work so we had to make the file lol xd xd d x");
			System.exit(1);
			
		}
		
	}
	
	public void chooseNext()
	{
		
		current = (int)(Math.random()*Hiragana.size());
		
		if (tr[current] >= desiredFinalRight)
			chooseNext();
	}
	
	public String currentMulti = "";
	
	
	
	public void tryAnswer(String romaji)
	{
		if (romaji.equals(""))
			return;
		
		boolean gotit = false;
		for (String c : RomajiForHiragana.get(current))
		{
			if (romaji.equalsIgnoreCase(c))
		{
				gotit=true;
			//right
			gui.println(Hiragana.get(current) + " is " + c);
			tr[current]++;
			timesRight++;
			if (timesRight > desiredTimesRight-1)
			{
				//make them enter it 3 times in a row because yeah
			timesRight = 0;
			chooseNext();
			
			gui.println(Hiragana.get(current));
			}
			
		}
		}
		if (gotit == false && romaji.equals("?"))
		{
			//give them the answer
			for(String s : RomajiForHiragana.get(current))
				gui.println(s);
		}
		else if (gotit==false && romaji.equalsIgnoreCase("status"))
		{
			int total = 0;
			for (int i = 0; i < tr.length; i++)
			{
				total += tr[i];
			}
			gui.println(""+(int)(100 * total / (desiredFinalRight*tr.length))+"%");
			
			
		}
		else if (gotit == false)
		{//wrong
			gui.println("not " + romaji);
			gui.println(Hiragana.get(current));
		}
			
		
	}

	public static String getFileNameFromChooser()
	{
		String filename = "";
		
		JFileChooser chooser = new JFileChooser();
		File workingDirectory = new File(System.getProperty("user.dir"));
		chooser.setCurrentDirectory(workingDirectory);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
		    "spamstudythings", "txt");
		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(null);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
		        return chooser.getSelectedFile().getName();
		}
		
		
		return filename;
		
	}
	public static void main(String[]args)
	{
		//arguments:
		//return xdesiredFinalRight + " " + xdesiredTimesRight + " " + fileName;
		String arguments = Spamstudy.loadOptionsFromFile();
		String fileName = Spamstudy.getFileNameFromChooser();
		arguments += " " + fileName;
		if (arguments == null)
		{
			Spamstudy.makeDefaultSettingsFile();
			System.exit(1);
		}
		Spamstudy k = new Spamstudy(arguments);
		k.begin();
		
		
		
	}


class EditGUI//kawaii gui
{
public Spamstudy k;
private JFrame f;
private JTextArea console;
private JScrollPane jsp;

public EditGUI(Spamstudy k)
	{
	this.k = k;
		console = new JTextArea();
		console.setEditable(false);
		int width = (int)(200*1.5), height = (int)(500*1.5);
		f = new JFrame("Spamstudy by Steven Ventura");
		f.setLocation(100,100);
		f.setLayout(new FlowLayout());

		f.setSize(width,height);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		

		final JTextField field = new JTextField();
		field.setBackground(new Color(0.025f,0.025f,0.025f));
		field.setForeground(Color.WHITE);
		field.setFont(new Font("DejaVu Sans", Font.BOLD, 26));

		field.setPreferredSize(new Dimension(width-14,88));

		field.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					
					
					String text = e.getActionCommand().toLowerCase();
					
					tryAnswer(text);
					
					field.setText("");
					
					
					
				}
			}
					);
		f.add(field);
		
		
		jsp = new JScrollPane(console);
		
		console.setBackground(new Color(0.1686274509803922f,0.0588235294117647f, 0.003921568627451f));
		console.setForeground(new Color(0.7372549019607843f,0.7333333333333333f, 0.5019607843137255f));
		console.setFont(new Font("DejaVu Sans", Font.BOLD, 31));

		jsp.setPreferredSize(new Dimension(width-14,height-88-42));

		f.add(jsp);

		f.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e)
			{
				int width = e.getComponent().getWidth();
				int height = e.getComponent().getHeight();
				field.setPreferredSize(new Dimension(width-14,88));
				jsp.setPreferredSize(new Dimension(width-14,height-88-42));
				console.setFont(new Font("DejaVu Sans", Font.BOLD, (int)(31*width/200)));
			}
			
			
		});


		f.setVisible(true);
	}


public void print(String s)
{
console.append(s);
}
public void println(String s)
{
this.print(s);
this.print("\r\n");
console.scrollRectToVisible(new Rectangle(0,console.getBounds().height,1,1));
}
}
}