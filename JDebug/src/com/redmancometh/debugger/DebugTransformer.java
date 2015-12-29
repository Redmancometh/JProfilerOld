package com.redmancometh.debugger;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;

public class DebugTransformer implements ClassFileTransformer
{
	public DebugTransformer()
	{
		super();
	}
	
	public byte[] transform(ClassLoader loader, String className, Class classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException
	{
		try
		{
			ClassReader reader = new ClassReader(className);
			ClassNode classNode = new ClassNode();
			reader.accept(classNode, ClassReader.SKIP_DEBUG);
			for (MethodNode methodNode : classNode.methods)
			{
				System.out.println("Transformer says: "+methodNode.name);
				System.out.println("\n");
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return classfileBuffer;
	}

}
