package czescjestemadas.obfuscator;

import czescjestemadas.obfuscator.util.JarUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.jar.JarFile;

@Slf4j
public class Main
{
	public static void main(String[] args)
	{
		final ObfuscatorSettings settings = ObfuscatorSettings.builder()
				.namesCharLimit(127)
				.skippedName("czescjestemadas.kmitems.ItemsPlugin#onLoad")
				.packagePrefix("czescjestemadas.")
				.fieldFinalRemove(true)
				.inline(true)
				.strings(true)
				.numbers(true)
				.booleans(true)
				.junkCodeGen(true)
				.signature("czescjestemadasczescjestemadas\nczescjestemadasczescjestemadas\nczescjestemadasczescjestemadas\nczescjestemadasczescjestemadas\nczescjestemadasczescjestemadas\nczescjestemadasczescjestemadas")
				.build();

		log.info("Generated {} mapping name chars", Mappings.genChars(settings.getNamesCharLimit()));
		log.info("Obfuscator settings: {}", settings);

		final Obfuscator obf = new Obfuscator(settings);

		clearDir(new File("run/out/"));

//		final Path outputFile = Path.of("run/out/obf/obfuscator-test-jar.jar");
//		obf.obfuscate(new File("test-jar/target/obfuscator-test-jar-0.1.jar"), outputFile);
//		extractJar(outputFile);

		final Path pacOutputFile = Path.of("/home/adas/srv/plugin/ProjectAC-0.1.jar");
		obf.obfuscate(new File("/home/adas/Programming/krystal/ProjectAC/build/libs/ProjectAC-0.1.jar"), pacOutputFile);
//		extractJar(pacOutputFile);

//		final Path outputFile2 = Path.of("run/out/apl/AdasPluginLib.jar");
//		obf.obfuscate(new File("run/AdasPluginLib.jar"), outputFile2);
//		extractJar(outputFile2);
	}

	private static void extractJar(Path jarPath)
	{
		final Path jarDir = jarPath.getParent();

		try
		{
			final Map<String, byte[]> entries = JarUtil.read(new JarFile(jarPath.toFile()));

			entries.forEach((entryName, bytes) -> {
				try
				{
					final Path path = jarDir.resolve(entryName);
					path.getParent().toFile().mkdirs();
					Files.write(path, bytes);
				}
				catch (IOException e)
				{
					throw new RuntimeException(e);
				}
			});
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	private static void clearDir(File dir)
	{
		for (File file : dir.listFiles())
		{
			if (file.isDirectory())
				clearDir(file);
			file.delete();
		}
	}
}
