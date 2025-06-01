package czescjestemadas.obfuscator.consumer.transformer;

import czescjestemadas.obfuscator.Mappings;
import czescjestemadas.obfuscator.Obfuscator;
import czescjestemadas.obfuscator.ObfuscatorSettings;
import czescjestemadas.obfuscator.util.StrUtil;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Map;

public class ClassNameTransform implements ClassTransformer
{
	@Override
	public boolean run(ClassNode node, Map<String, ClassNode> classes, Mappings mappings, ObfuscatorSettings settings)
	{
		final String packageName = StrUtil.classPackage(node.name);

		boolean showHeader = true;

		final String mappedSimpleName = mappings.getClassMapping(node.name);
		if (mappedSimpleName != null)
		{
			final String mappedName = packageName + '/' + mappedSimpleName;
			Obfuscator.LOGGER.info("  {} -> {}", node.name, mappedName);
			node.name = mappedName;

			showHeader = false;
		}

		final String mappedSuperSimpleName = mappings.getClassMapping(node.superName);
		if (mappedSuperSimpleName != null)
		{
			final String superPackage = StrUtil.classPackage(node.superName);
			final String mappedSuperName = superPackage + '/' + mappedSuperSimpleName;

			if (showHeader)
			{
				Obfuscator.LOGGER.info("  {}", node.name);
				showHeader = false;
			}

			Obfuscator.LOGGER.info("    super {} -> {}", node.superName, mappedSuperName);
			node.superName = mappedSuperName;
		}

		for (int i = 0; i < node.interfaces.size(); i++)
		{
			final String anInterface = node.interfaces.get(i);
			final String mappedInterfaceSimpleName = mappings.getClassMapping(anInterface);
			if (mappedInterfaceSimpleName == null)
				continue;

			final String mappedInterfaceName = StrUtil.classPackage(anInterface) + '/' + mappedInterfaceSimpleName;

			if (showHeader)
			{
				Obfuscator.LOGGER.info("  {}", node.name);
				showHeader = false;
			}

			Obfuscator.LOGGER.info("    interface {} -> {}", anInterface, mappedInterfaceName);
			node.interfaces.set(i, mappedInterfaceName);
		}

		runInsn(node, classes, mappings, settings);

		return false;
	}

	private void runInsn(ClassNode node, Map<String, ClassNode> classes, Mappings mappings, ObfuscatorSettings settings)
	{
		for (final MethodNode method : node.methods)
		{
			final Type type = Type.getType(method.desc);
			final Type mappedType = mappings.mapMethodDesc(type);

			if (mappedType != null)
			{
				Obfuscator.LOGGER.info("    DSC {} -> {}", type, mappedType);
				method.desc = mappedType.getDescriptor();
			}


			for (final AbstractInsnNode instruction : method.instructions)
			{
				if (instruction instanceof FieldInsnNode fieldInsnNode)
				{
					final String packageName = StrUtil.classPackage(fieldInsnNode.owner);
					final String mappedSimpleName = mappings.getClassMapping(fieldInsnNode.owner);
					if (mappedSimpleName == null)
						continue;

					final String mappedName = packageName + '/' + mappedSimpleName;
					Obfuscator.LOGGER.info("    FLD {} -> {}",
							Mappings.key(fieldInsnNode.owner, fieldInsnNode.name),
							Mappings.key(mappedName, fieldInsnNode.name)
					);
					fieldInsnNode.owner = mappedName;
				}
				else if (instruction instanceof MethodInsnNode methodInsnNode)
				{
					final String packageName = StrUtil.classPackage(methodInsnNode.owner);
					final String mappedSimpleName = mappings.getClassMapping(methodInsnNode.owner);

					if (mappedSimpleName != null)
					{
						final String mappedName = packageName + '/' + mappedSimpleName;
						Obfuscator.LOGGER.info("    MTH {} -> {}",
								Mappings.key(methodInsnNode.owner, methodInsnNode.name),
								Mappings.key(mappedName, methodInsnNode.name)
						);
						methodInsnNode.owner = mappedName;
					}


					final Type methodDesc = Type.getMethodType(methodInsnNode.desc);
					final Type mappedMethodDesc = mappings.mapMethodDesc(methodDesc);

					if (mappedMethodDesc != null)
					{
						Obfuscator.LOGGER.info("    MTH {} -> {}", methodInsnNode.desc, mappedMethodDesc);
						methodInsnNode.desc = mappedMethodDesc.getDescriptor();
					}
				}
				else if (instruction instanceof TypeInsnNode typeInsnNode)
				{
					final String mappedSimpleName = mappings.getClassMapping(typeInsnNode.desc);
					if (mappedSimpleName == null)
						continue;

					final String mappedName = StrUtil.classPackage(typeInsnNode.desc) + '/' + mappedSimpleName;

					Obfuscator.LOGGER.info("    CLS {} -> {}", typeInsnNode.desc, mappedName);
					typeInsnNode.desc = mappedName;
				}
			}
		}
	}
}
