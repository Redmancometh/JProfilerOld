package com.redmancometh.debugger;

import java.lang.instrument.Instrumentation;

public class AttachAgent
{
	static Instrumentation instrument;

	public static void agentmain(String agentArgs, Instrumentation inst)
	{
		instrument = inst;
	}
}
