package enamel;

import java.util.*;
public class SectionNode 
{
	//List of children
	private List <SectionNode> children;
	private String identifier;
	//The information stored in each section
	private List <String> sectionInfo;
	
	public SectionNode(String identifier) 
	{
		children = new ArrayList <SectionNode> ();
		sectionInfo = new ArrayList <String> ();
		this.identifier = identifier;
	}
	
	public void addChild (SectionNode node)
	{
		children.add(node);
	}
	public void addInfo (String info)
	{
		sectionInfo.add(info);
	}
	
	public String getIdentity ()
	{
		return identifier;
	}
	
	public SectionNode getChild (int childNum)
	{
		return children.get (childNum);
	}
	
	public int getNumChildren ()
	{
		return children.size ();
	}
	public String display ()
	{
		StringBuilder sb = new StringBuilder ();
		for (String i : sectionInfo)
		{
			sb.append(i + " \n");
		}
		return (sb.toString ());
	}
}
