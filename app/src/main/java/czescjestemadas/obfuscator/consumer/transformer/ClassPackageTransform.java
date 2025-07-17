package czescjestemadas.obfuscator.consumer.transformer;

import czescjestemadas.obfuscator.Mappings;
import czescjestemadas.obfuscator.Obfuscator;
import czescjestemadas.obfuscator.ObfuscatorSettings;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Map;

public class ClassPackageTransform implements ClassTransformer
{
	@Override
	public boolean run(ClassNode node, Map<String, ClassNode> classes, Mappings mappings, ObfuscatorSettings settings)
	{
		final String repackagedClassName = mappings.repackage(settings.getPackageName(), node.name);
		if (repackagedClassName != null)
		{
			Obfuscator.LOGGER.info("  {} -> {}", node.name, repackagedClassName);
			node.name = repackagedClassName;
		}
		else
			Obfuscator.LOGGER.info("  {}", node.name);

		if (node.superName != null)
		{
			final String repackagedSuperName = mappings.repackage(settings.getPackageName(), node.superName);
			if (repackagedSuperName != null)
			{
				Obfuscator.LOGGER.info("    super {} -> {}", node.superName, repackagedSuperName);
				node.superName = repackagedSuperName;
			}
		}

		node.interfaces.replaceAll(interfaceName -> {
			final String repackagedInterface = mappings.repackage(settings.getPackageName(), interfaceName);
			if (repackagedInterface == null)
				return interfaceName;
			Obfuscator.LOGGER.info("    interface {} -> {}", interfaceName, repackagedInterface);
			return repackagedInterface;
		});

		for (final FieldNode field : node.fields)
		{
			final Type type = Type.getType(field.desc);
			final Type repackagedType = mappings.repackageFieldDesc(settings.getPackageName(), type);

			if (repackagedType == null)
				continue;

			Obfuscator.LOGGER.info("    FLD {} -> {}", type, repackagedType);
			field.desc = repackagedType.getDescriptor();
		}

		for (final MethodNode method : node.methods)
		{
			final Type type = Type.getType(method.desc);
			final Type repackagedType = mappings.repackageMethodDesc(settings.getPackageName(), type);

			if (repackagedType != null)
			{
				Obfuscator.LOGGER.info("    MTH {} -> {}", type, repackagedType);
				method.desc = repackagedType.getDescriptor();
			}

			for (final AbstractInsnNode instruction : method.instructions)
			{
				if (instruction instanceof FieldInsnNode fieldInsnNode)
				{
					final String mappedName = mappings.repackage(settings.getPackageName(), fieldInsnNode.owner);
					if (mappedName == null)
						continue;

					Obfuscator.LOGGER.info("    INSN FLD {} -> {}",
							Mappings.key(fieldInsnNode.owner, fieldInsnNode.name),
							Mappings.key(mappedName, fieldInsnNode.name)
					);
					fieldInsnNode.owner = mappedName;
				}
				else if (instruction instanceof MethodInsnNode methodInsnNode)
				{
					final String repackagedOwner = mappings.repackage(settings.getPackageName(), methodInsnNode.owner);
					if (repackagedOwner != null)
					{
						Obfuscator.LOGGER.info("    INSN MTH OWNR {} -> {}",
								Mappings.key(methodInsnNode.owner, methodInsnNode.name),
								Mappings.key(repackagedOwner, methodInsnNode.name)
						);
						methodInsnNode.owner = repackagedOwner;
					}


					final Type methodDesc = Type.getMethodType(methodInsnNode.desc);
					final Type mappedMethodDesc = mappings.mapMethodDesc(methodDesc);

					if (mappedMethodDesc != null)
					{
						// todo test
						Obfuscator.LOGGER.info("    INSN MTH DESC {} -> {}", methodInsnNode.desc, mappedMethodDesc);
						methodInsnNode.desc = mappedMethodDesc.getDescriptor();
					}
				}
				else if (instruction instanceof TypeInsnNode typeInsnNode)
				{
					final String repackagedDesc = mappings.repackage(settings.getPackageName(), typeInsnNode.desc);
					if (repackagedDesc == null)
						continue;

					Obfuscator.LOGGER.info("    INSN CLS {} -> {}", typeInsnNode.desc, repackagedDesc);
					typeInsnNode.desc = repackagedDesc;
				}
			}
		}

		return false;
	}
}
