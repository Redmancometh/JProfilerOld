package com.redmancometh.debugger;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import javafx.util.Pair;

public class AgentConnector
{
	static Instrumentation i;
	
	@Deprecated
	public Pair<Boolean, Instrumentation> getInstrumentation()
	{
		System.out.println("attempting to pull instrumentation instance...");
		try
		{
			Class type = Class.forName("com.redmancometh.debugger.AttachAgent");
			Field f = type.getDeclaredField("instrument");
			this.i = (Instrumentation) f.get(null);
			if(i!=null)
			{
				System.out.println("Got instance of instrumentation class...");
				return new Pair(true, i);
			}
			else
			{
				System.out.println("Failed to attach...");
			}
		}
		catch (ClassNotFoundException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e)
		{
			e.printStackTrace();
		}
		return new Pair(false, null);
	}
	
	public void attachInPlace()
	{
		
	}
	
	public void setInstrumentation(Instrumentation inst)
	{
		i=inst;
		System.out.println("Transformation callback successful, instrumentation field set.");
	}
	
	public Class[] getClasses()
	{
		if(i==null)
		{
			System.out.println("I IS NULL");
		}
		return i.getAllLoadedClasses();
	}
	
	public void addTransformer(ClassFileTransformer transformer)
	{
		i.addTransformer(transformer);
	}
}
