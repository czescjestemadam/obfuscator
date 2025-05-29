package czescjestemadas.obfuscator.consumer.mapper;

import czescjestemadas.obfuscator.Mappings;
import czescjestemadas.obfuscator.ObfuscatorSettings;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Map;

public class ClassMethodMap implements ClassMapper
{
	@Override
	public boolean run(ClassNode node, Map<String, ClassNode> classes, Mappings mappings, ObfuscatorSettings settings)
	{
		if (Mappings.isSkipAnnotated(node.invisibleAnnotations))
			return false;

		for (final MethodNode method : node.methods)
		{
			if (!Mappings.isNameIgnored(method.name) && !Mappings.isSkipAnnotated(method.invisibleAnnotations))
				mappings.generateMapping(Mappings.key(node, method), settings.getMethodNameLength());
		}

		return false;
	}
}
