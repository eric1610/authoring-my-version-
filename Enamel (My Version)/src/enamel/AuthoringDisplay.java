package enamel;

import javax.swing.*;
import javax.swing.text.DefaultHighlighter;
//import javax.swing.JPanel;
//import javax.swing.JScrollPane;
//import javax.swing.JTextField;
//import javax.swing.SpringLayout;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

import java.util.*;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.*;
import java.io.*;

public class AuthoringDisplay 
{
	
	// Used to store the different sections
	private static ArrayList <String> identifiers; 
	private static ArrayList <SectionNode> nodes;
	private static int counter = 0;
	private static JTextArea field = new JTextArea (14, 32);
	private static JFrame frame = new JFrame ();
	private static SectionNode currNode;
	private static SectionNode root;
	private static ArrayDeque <SectionNode> q = new ArrayDeque <SectionNode> ();
	private static boolean state;
	
	public AuthoringDisplay() 
	{
		// TODO Auto-generated constructor stub
	}
	
	private static void prepBFS ()
	{
		nodes.clear ();
		q.add(root);
		SectionNode n;
		while (!q.isEmpty())
		{
			n = q.pop ();
			nodes.add(n);
			for (int a = 0; a < n.getNumChildren (); a ++)
			{
				if (!q.contains(n.getChild(a)))
				{
					q.add(n.getChild(a));
				}
			}
		}
		
	}
	public static void change ()
	{
		if (state)
		{
			prepBFS ();
			state = false;
		}
		if (counter < nodes.size () - 1)
		{
			counter ++;
		}
		else
		{
			counter = 0;
		}
		field.setText(nodes.get(counter).display());
	}
	
	public static void add ()
	{
		 JFrame frame = new JFrame("Editor");
			SpringLayout layout = new SpringLayout ();

		    frame.setVisible(true);
		    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		    frame.setSize(450, 350);
		    frame.setLocation(430, 100);
		    JPanel panel = new JPanel();

		    frame.add(panel);
		    panel.setLayout(layout);
		    String[] choices = {"Normal-text:", "Pause:", "Display-string:", "Set-voice:", "Skip-button:", "user-input", "Identifier:"
		    		, "Skip:", "Sound:", "Clear-display", "Repeat:", "Reset-buttons", "Repeat-button:", "Display-cell-pins:",
		    		"Display-cell-char:", "Display-raise:", "Display-lower"};

		    JComboBox<String> cb = new JComboBox<String>(choices);
		    
		    JTextArea area = new JTextArea (15, 22);
		   
		    JScrollPane scroll = new JScrollPane (area);
			JPanel textPane = new JPanel ();
			JButton okay = new JButton ("OK");
			area.setFont(new Font ("Serif", Font.PLAIN, 16));
			area.setLineWrap (true);
	
			area.setWrapStyleWord (true);
		//	area.setEditable(false);
			textPane.add(scroll);		    
			panel.add(textPane);
			
			cb.setVisible(true);
		    panel.add(cb);
		    panel.add(okay);
		  
		    okay.addActionListener(new ActionListener () {
				
				@Override
				public void actionPerformed (ActionEvent arg0)
				{
					System.out.println (area.getText());
				}				
			});
		    
		    layout.putConstraint(SpringLayout.WEST, cb, 10, SpringLayout.WEST, panel);
			layout.putConstraint(SpringLayout.NORTH, cb, 13, SpringLayout.NORTH, panel);
			layout.putConstraint(SpringLayout.WEST, textPane, 150, SpringLayout.WEST, panel);
			layout.putConstraint(SpringLayout.NORTH, textPane, 10, SpringLayout.NORTH, panel);
			layout.putConstraint(SpringLayout.WEST, okay, 20, SpringLayout.WEST, panel);
			layout.putConstraint(SpringLayout.SOUTH, okay, -20, SpringLayout.SOUTH, panel);
			
	}
	
	public static void main (String [] args)
	{
		try
		{
			File f = new File ("SampleScenarios/Scenario_One.txt");
			Scanner fileScanner = new Scanner (f);
			SpringLayout layout = new SpringLayout ();
			
			int cellNum = Integer.parseInt(fileScanner.nextLine().split("\\s")[1]);
			int buttonNum = Integer.parseInt(fileScanner.nextLine().split("\\s")[1]);
			identifiers = new ArrayList <String> ();
			nodes = new ArrayList <SectionNode> ();
			
			String identifier = "RootNode";
			root = new SectionNode ("RootNode");
			identifiers.add(identifier);
			nodes.add(root);
		    currNode = nodes.get(identifiers.lastIndexOf(identifier));

		    String lines, oldidentifier, rootidentifier = identifier;
			while (fileScanner.hasNextLine ())
			{
				oldidentifier = identifier;
				lines = fileScanner.nextLine();
				if (lines.length () > 0)
				{

					for (String i : identifiers)
					{
						if (lines.equals("/~" + i))
						{
							identifier = i;
							currNode = nodes.get(identifiers.lastIndexOf(identifier));
							break;
						}
					}

					if (lines.length() >= 14 && lines.substring(0, 14).equals("/~skip-button:"))
					{
						identifier = lines.substring (16);
						identifiers.add(identifier);
						nodes.add (new SectionNode (identifier));

						currNode.addChild(nodes.get(identifiers.lastIndexOf(identifier)));
					}
					
					currNode.addInfo (lines);
					if (lines.equals("/~user-input"))
					{
						nodes.remove(identifiers.lastIndexOf(rootidentifier));
						identifiers.remove(rootidentifier);
						
					}
					else
					{
						if (lines.length () >= 7 && lines.substring(0, 7).equals("/~skip:"))
						{
							oldidentifier = identifier;
							identifier = lines.substring(7);
							rootidentifier = identifier;

							if (!identifiers.contains(identifier))
							{
								identifiers.add(identifier);
								nodes.add (new SectionNode (identifier));
							}

							currNode.addChild(nodes.get(identifiers.lastIndexOf(identifier)));
							
							nodes.remove(identifiers.lastIndexOf(oldidentifier));
							identifiers.remove(oldidentifier);

						}
					}
				}
			}
		
			fileScanner.close ();
				
			field.setText(root.display());
			state = true;
			Simulator sim = new Simulator (cellNum, buttonNum);
			JScrollPane scroll = new JScrollPane (field);
			JPanel textPane = new JPanel ();
			JLabel afterLabel = new JLabel ("Add statement after the highlighted text:");
			JLabel beforeLabel = new JLabel ("Add statement before the highlighted text:");
			String[] additional = {"'Set-voice'", "'Skip-button'", "'Identifier'"
		    		, "'Skip'", "'Clear-display'", "'Repeat'", "'Reset buttons'", "'Repeat button'", "'Display cell pins'",
		    		"'Display char'", "'Raise one pin'", "'Lower one pin'"};

			
			
			field.setFont(new Font ("Serif", Font.PLAIN, 16));
			field.setLineWrap (true);
	
			field.setWrapStyleWord (true);
			field.setEditable(false);
			textPane.add(scroll);
		//	frame.getContentPane().add(afterLabel);
		//	frame.getContentPane().add(beforeLabel);
				
			Highlighter highlighter = field.getHighlighter();
		    HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.pink);
				
			// Exposed Simulator.java.frame to public
				
			sim.getButton(0).addActionListener(new ActionListener () {
				
				@Override
				public void actionPerformed (ActionEvent arg0)
				{
					change ();
				}				
			});
				
			
			frame.setSize (600, 555);
	
			JButton addLines = new JButton ("Add 'Normal text'");
			JButton addSound = new JButton ("Add 'Sound file'");
			JButton dispChar = new JButton ("Add 'Display char'");
			JButton skipButton = new JButton ("Add 'Skip-button'");
			JButton skipper = new JButton ("Add 'Skip to'");
			JButton userInp = new JButton ("Add 'Get user-input'"); 
			JButton repeatButton = new JButton ("Add 'Repeat'");
			JButton resetButton = new JButton ("Add 'Reset buttons'");
			JButton dispClearAll = new JButton ("Add 'Clear all display'");
			JButton editButton = new JButton ("Edit");
			JButton deleteButton = new JButton ("Delete");
			JButton upButton = new JButton ("Up");
			JButton downButton = new JButton ("Down");

			JLabel options = new JLabel ("More options to add: ");
		    JComboBox<String> cb = new JComboBox<String>(additional);
		    JButton addOptions = new JButton ("Add");
			
			addLines.addActionListener(new ActionListener () {
				
				@Override
				public void actionPerformed (ActionEvent arg0)
				{
					add ();
				}				
			});
			
			addSound.addActionListener(new ActionListener () {
				
				@Override
				public void actionPerformed (ActionEvent arg0)
				{
					add ();
				}				
			});
			
			dispChar.addActionListener(new ActionListener () {
				
				@Override
				public void actionPerformed (ActionEvent arg0)
				{
					add ();
				}				
			});
			frame.getContentPane().setLayout(layout);
		//	frame.getContentPane().add(sim.frame.getContentPane());
			frame.getContentPane().add (textPane);
			frame.getContentPane().add (addLines);
			frame.getContentPane().add (addSound);
			frame.getContentPane().add (dispChar);
			frame.getContentPane().add (skipButton);
			frame.getContentPane().add (skipper);
			frame.getContentPane().add (userInp);
			frame.getContentPane().add (repeatButton);
			frame.getContentPane().add (resetButton);
			frame.getContentPane().add (dispClearAll);
			frame.getContentPane().add (options);
			frame.getContentPane().add (cb);
			frame.getContentPane().add (addOptions);
			frame.getContentPane().add (editButton);
			frame.getContentPane().add (deleteButton);
			frame.getContentPane().add (upButton);
			frame.getContentPane().add (downButton);
			
			layout.putConstraint(SpringLayout.WEST, textPane, 220, SpringLayout.WEST, frame.getContentPane());
			layout.putConstraint(SpringLayout.NORTH, textPane, 195, SpringLayout.NORTH, frame.getContentPane());
			layout.putConstraint(SpringLayout.WEST, addLines, 40, SpringLayout.WEST, frame.getContentPane());
			layout.putConstraint(SpringLayout.NORTH, addLines, 25, SpringLayout.NORTH, frame.getContentPane());
			layout.putConstraint(SpringLayout.WEST, addSound, 220, SpringLayout.WEST, frame.getContentPane());
			layout.putConstraint(SpringLayout.NORTH, addSound, 25, SpringLayout.NORTH, frame.getContentPane());
			layout.putConstraint(SpringLayout.WEST, dispChar, 400, SpringLayout.WEST, frame.getContentPane());
			layout.putConstraint(SpringLayout.NORTH, dispChar, 25, SpringLayout.NORTH, frame.getContentPane());
			layout.putConstraint(SpringLayout.WEST, skipButton, 40, SpringLayout.WEST, frame.getContentPane());
			layout.putConstraint(SpringLayout.NORTH, skipButton, 90, SpringLayout.NORTH, frame.getContentPane());
			layout.putConstraint(SpringLayout.WEST, skipper, 220, SpringLayout.WEST, frame.getContentPane());
			layout.putConstraint(SpringLayout.NORTH, skipper, 90, SpringLayout.NORTH, frame.getContentPane());
			layout.putConstraint(SpringLayout.WEST, userInp, 400, SpringLayout.WEST, frame.getContentPane());
			layout.putConstraint(SpringLayout.NORTH, userInp, 90, SpringLayout.NORTH, frame.getContentPane());
			layout.putConstraint(SpringLayout.WEST, repeatButton, 40, SpringLayout.WEST, frame.getContentPane());
			layout.putConstraint(SpringLayout.NORTH, repeatButton, 145, SpringLayout.NORTH, frame.getContentPane());
			layout.putConstraint(SpringLayout.WEST, resetButton, 220, SpringLayout.WEST, frame.getContentPane());
			layout.putConstraint(SpringLayout.NORTH, resetButton, 145, SpringLayout.NORTH, frame.getContentPane());
			layout.putConstraint(SpringLayout.WEST, dispClearAll, 400, SpringLayout.WEST, frame.getContentPane());
			layout.putConstraint(SpringLayout.NORTH, dispClearAll, 145, SpringLayout.NORTH, frame.getContentPane());
			layout.putConstraint(SpringLayout.WEST, options, 20, SpringLayout.WEST, frame.getContentPane());
			layout.putConstraint(SpringLayout.NORTH, options, 200, SpringLayout.NORTH, frame.getContentPane());	
			layout.putConstraint(SpringLayout.WEST, cb, 20, SpringLayout.WEST, frame.getContentPane());
			layout.putConstraint(SpringLayout.NORTH, cb, 220, SpringLayout.NORTH, frame.getContentPane());	
			layout.putConstraint(SpringLayout.WEST, addOptions, 160, SpringLayout.WEST, frame.getContentPane());
			layout.putConstraint(SpringLayout.NORTH, addOptions, 220, SpringLayout.NORTH, frame.getContentPane());
			layout.putConstraint(SpringLayout.WEST, editButton, 60, SpringLayout.WEST, frame.getContentPane());
			layout.putConstraint(SpringLayout.NORTH, editButton, 280, SpringLayout.NORTH, frame.getContentPane());	
			layout.putConstraint(SpringLayout.WEST, deleteButton, 60, SpringLayout.WEST, frame.getContentPane());
			layout.putConstraint(SpringLayout.NORTH, deleteButton, 340, SpringLayout.NORTH, frame.getContentPane());
			layout.putConstraint(SpringLayout.WEST, upButton, 20, SpringLayout.WEST, frame.getContentPane());
			layout.putConstraint(SpringLayout.SOUTH, upButton, -10, SpringLayout.SOUTH, frame.getContentPane());
			layout.putConstraint(SpringLayout.WEST, downButton, 150, SpringLayout.WEST, frame.getContentPane());
			layout.putConstraint(SpringLayout.SOUTH, downButton, -10, SpringLayout.SOUTH, frame.getContentPane());	
		//	layout.putConstraint(SpringLayout.NORTH, beforeLabel, 20, SpringLayout.NORTH, frame.getContentPane());
		//	layout.putConstraint(SpringLayout.WEST, afterLabel, 10, SpringLayout.WEST, frame.getContentPane());
		//	layout.putConstraint(SpringLayout.NORTH, afterLabel, 80, SpringLayout.NORTH, frame.getContentPane());
			
			
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
		}
		catch (Exception e)
		{
			
		}
	}
}