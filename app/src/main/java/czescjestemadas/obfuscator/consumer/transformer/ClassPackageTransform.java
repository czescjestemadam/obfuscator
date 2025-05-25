package czescjestemadas.obfuscator.consumer.transformer;

import czescjestemadas.obfuscator.Mappings;
import czescjestemadas.obfuscator.Obfuscator;
import czescjestemadas.obfuscator.ObfuscatorSettings;
import czescjestemadas.obfuscator.util.StrUtil;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Map;

public class ClassPackageTransform implements ClassTransformer
{
	@Override
	public boolean run(ClassNode node, Map<String, ClassNode> classes, Mappings mappings, ObfuscatorSettings settings)
	{
		final String className = node.name;
		node.name = Mappings.repackage(settings.getPackageName(), node.name);
		Obfuscator.LOGGER.info("  {} -> {}", className, node.name);

		if (node.superName != null)
		{
			final String superName = node.superName;
			node.superName = Mappings.repackage(settings.getPackageName(), node.superName);
			Obfuscator.LOGGER.info("    super {} -> {}", superName, node.superName);
		}

		node.interfaces.replaceAll(s -> Mappings.repackage(settings.getPackageName(), s));

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
//					final String mappedName = Mappings.repackage(settings.getPackageName(), fieldInsnNode.owner);
//					if (fieldInsnNode.owner.equals(mappedName))
//						continue;
//
//					Obfuscator.LOGGER.info("    FLD {} -> {}",
//							Mappings.key(fieldInsnNode.owner, fieldInsnNode.name),
//							Mappings.key(mappedName, fieldInsnNode.name)
//					);
//					fieldInsnNode.owner = mappedName;
				}
				else if (instruction instanceof MethodInsnNode methodInsnNode)
				{
//					final String packageName = StrUtil.classPackage(methodInsnNode.owner);
//					final String mappedSimpleName = mappings.getClassMapping(methodInsnNode.owner);
//
//					if (mappedSimpleName != null)
//					{
//						final String mappedName = packageName + '/' + mappedSimpleName;
//						Obfuscator.LOGGER.info("    MTH {} -> {}",
//								Mappings.key(methodInsnNode.owner, methodInsnNode.name),
//								Mappings.key(mappedName, methodInsnNode.name)
//						);
//						methodInsnNode.owner = mappedName;
//					}
//
//
//					final Type methodDesc = Type.getMethodType(methodInsnNode.desc);
//					final Type mappedMethodDesc = mappings.mapMethodDesc(methodDesc);
//
//					if (mappedMethodDesc != null)
//					{
//						Obfuscator.LOGGER.info("    MTH {} -> {}", methodInsnNode.desc, mappedMethodDesc);
//						methodInsnNode.desc = mappedMethodDesc.getDescriptor();
//					}
				}
				else if (instruction instanceof TypeInsnNode typeInsnNode)
				{
//					final String mappedSimpleName = mappings.getClassMapping(typeInsnNode.desc);
//					if (mappedSimpleName == null)
//						continue;
//
//					final String mappedName = StrUtil.classPackage(typeInsnNode.desc) + '/' + mappedSimpleName;
//
//					Obfuscator.LOGGER.info("    CLS {} -> {}", typeInsnNode.desc, mappedName);
//					typeInsnNode.desc = mappedName;
				}
			}
		}

		return false;
	}
}
