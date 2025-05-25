package czescjestemadas.obfuscator.consumer.transformer;

import czescjestemadas.obfuscator.Mappings;
import czescjestemadas.obfuscator.Obfuscator;
import czescjestemadas.obfuscator.ObfuscatorSettings;
import czescjestemadas.obfuscator.util.StrUtil;
import org.objectweb.asm.tree.ClassNode;

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

		return false;
	}
}
