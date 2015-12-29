package com.redmancometh.debugger;

import javax.swing.JFrame;
import javax.swing.JTextPane;

@SuppressWarnings("serial")
public class PIDListWindow extends JFrame
{
	JTextPane pidList = new JTextPane();
	
	public void display()
	{
		this.setSize(800, 600);
		this.add(pidList);
		this.setVisible(true);
	}
}
