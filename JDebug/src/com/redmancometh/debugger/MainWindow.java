package com.redmancometh.debugger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

@SuppressWarnings("serial")
public class MainWindow extends JFrame
{
	JButton attachButton = new JButton("Attach");
	JButton listPIDButton = new JButton("Update PID List");
	JButton detachButton = new JButton("Detach");
	JButton showMethods = new JButton("Show Class Methods");
	JButton transform = new JButton("Transform");

	JList classListWidget = new JList();
	JList methodListWidget = new JList();
	JComboBox pidList = new JComboBox();

	VirtualMachine attachedPID;
	static AgentConnector agent = new AgentConnector();

	boolean transformerInjected = false;
	
	public void init()
	{
		System.out.println(this.getClass().getClassLoader());
		this.setTitle("JROM Profiler");
		this.setSize(800, 600);
		this.setLayout(null);
		initButtons();
		initTextFields();
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void initButtons()
	{
		classListWidget = new JList();
		attachButton.setBounds(10, 500, 100, 25);
		listPIDButton.setBounds(110, 500, 150, 25);
		detachButton.setBounds(260, 500, 150, 25);
		showMethods.setBounds(420, 470, 150, 25);
		transform.setBounds(570, 470, 150, 25);
		
		this.add(transform);
		this.add(detachButton);
		this.add(attachButton);
		this.add(listPIDButton);
		this.add(showMethods);
		setActions();
	}

	public void initTextFields()
	{
		classListWidget.setBounds(10, 200, 400, 270);
		methodListWidget.setBounds(420, 200, 400, 270);
		this.add(classListWidget);
		this.add(methodListWidget);
		pidList.setBounds(10, 480, 400, 20);
		this.add(pidList);
	}

	public void setPIDListListener()
	{
		listPIDButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				pidList.removeAllItems();
				for (VirtualMachineDescriptor vmd : VirtualMachine.list())
				{
					String displayName = vmd.displayName().split(" ")[0];
					if (!displayName.startsWith(" "))
					{
						pidList.addItem(vmd.id() + "-" + displayName);
					}
				}
			}
		});
	}

	public void setTransformListeners()
	{
		transform.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				agent.attachInPlace();
				
				if(!transformerInjected)
				{
					agent.addTransformer(new DebugTransformer());
				}
			}
		});
		showMethods.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				List<Method> methodList = new ArrayList();
				for (Method m : ((Class) MainWindow.this.classListWidget.getSelectedValue()).getDeclaredMethods())
				{
					methodList.add(m);
				}
				MainWindow.this.methodListWidget.setListData(methodList.toArray());
			}
		});
	}

	public void setAttachmentListeners()
	{
		attachButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					if (attachedPID == null)
					{
						attachedPID = VirtualMachine.attach(pidList.getSelectedItem().toString().split("-")[0]);
						attachedPID.loadAgent("C:\\Users\\Redman\\Desktop\\Debugger\\DebugAgent.jar");
						if(agent.getInstrumentation().getKey())
						{
							JOptionPane.showMessageDialog(null, "Attached Successfully!", "Detached",
									JOptionPane.INFORMATION_MESSAGE);
							initClassList();
						}
						else
						{
							attachedPID=null;
						}
					}
					else
					{
						JOptionPane.showMessageDialog(null, "Already attached a JVM instance!", "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
				catch (AttachNotSupportedException | IOException | AgentLoadException | AgentInitializationException e1)
				{
					e1.printStackTrace();
				}
			}
		});
		detachButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					if (attachedPID != null)
					{
						attachedPID.detach();
						JOptionPane.showMessageDialog(null, "Detached Successfully!", "Detached",
								JOptionPane.INFORMATION_MESSAGE);
						attachedPID = null;
						classListWidget.setListData(new Object[] { null });
					}
					else
					{
						JOptionPane.showMessageDialog(null, "Not attached to a JVM instance!", "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
				catch (Throwable e1)
				{
					e1.printStackTrace();
				}
			}
		});
	}

	public void initClassList()
	{
		classListWidget.setListData(agent.getClasses());
	}

	public void setActions()
	{
		setAttachmentListeners();
		setPIDListListener();
		setTransformListeners();
	}
}
