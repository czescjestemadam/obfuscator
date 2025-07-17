package czescjestemadas.obfuscator.consumer.generator;

import czescjestemadas.obfuscator.Mappings;
import czescjestemadas.obfuscator.Obfuscator;
import czescjestemadas.obfuscator.ObfuscatorSettings;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Map;

public class SignatureGen implements ClassGenerator
{
	@Override
	public boolean run(ClassNode node, Map<String, ClassNode> classes, Mappings mappings, ObfuscatorSettings settings)
	{
		if (settings.getSkippedNames().contains(node.name))
			return false;

		if (!settings.getPackagePrefixes().contains(node.name))
			return false;

		final String[] lines = settings.getSignature().split("\\n");

		final MethodNode method = new MethodNode(
				Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC,
				Mappings.genName(8),
				Type.getMethodDescriptor(Type.VOID_TYPE),
				null,
				null
		);

		for (int i = 0; i < lines.length; i++)
		{
			method.instructions.add(new LdcInsnNode(lines[i]));
			method.instructions.add(new VarInsnNode(Opcodes.ASTORE, i));
		}

		method.instructions.add(new InsnNode(Opcodes.RETURN));
		method.maxStack = lines.length;
		method.maxLocals = lines.length;

		node.methods.addFirst(method);
		Obfuscator.LOGGER.info("  Added {}", Mappings.key(node, method));

		return false;
	}
}
