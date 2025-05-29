package czescjestemadas.obfuscator;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

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
				.inline(true)
				.strings(true)
				.numbers(true)
				.booleans(true)
				.junkCodeGen(true)
				.build();

		log.info("Generated {} mapping name chars", Mappings.genChars(settings.getNamesCharLimit()));
		log.info("Obfuscator settings: {}", settings);

		final Obfuscator obf = new Obfuscator(settings);

//		obf.obfuscate("run/km-items-0.1.10.3.jar", "run/out/km-items.jar");
		obf.obfuscate(new File("test-jar/target/obfuscator-test-jar-0.1.jar"), Path.of("run/out/obfuscator-test-jar.jar"));
	}
}
