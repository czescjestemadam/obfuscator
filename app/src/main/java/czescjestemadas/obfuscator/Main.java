package czescjestemadas.obfuscator;

import czescjestemadas.obfuscator.util.JarUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Slf4j
public class Main
{
	public static void main(String[] args)
	{
		final ObfuscatorSettings settings = ObfuscatorSettings.builder()
				.namesCharLimit(127)
				.skippedNames(List.of(
						"czescjestemadas.kmitems.ItemsPlugin#onLoad"
				))
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

		final Path outputFile = Path.of("run/out/obfuscator-test-jar.jar");
		clearDirOf(outputFile);
		obf.obfuscate(new File("test-jar/target/obfuscator-test-jar-0.1.jar"), outputFile);
		extractJar(outputFile);
	}

	private static void extractJar(Path jarPath)
	{
		final Path jarDir = jarPath.getParent();

		try
		{
			final Map<JarEntry, byte[]> entries = JarUtil.read(new JarFile(jarPath.toFile()));

			entries.forEach((entry, bytes) -> {
				try
				{
					final Path path = jarDir.resolve(entry.getName());
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

	private static void clearDirOf(Path outputFile)
	{
		clearDir(outputFile.getParent().toFile());
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
