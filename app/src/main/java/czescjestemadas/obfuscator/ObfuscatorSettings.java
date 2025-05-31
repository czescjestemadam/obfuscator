package czescjestemadas.obfuscator;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Builder
@Getter
@ToString
public class ObfuscatorSettings
{
	// remove
	@Builder.Default
	private final boolean removeDebugInfo = true;
	private final boolean fieldFinalRemove;

	// values
	private final boolean inline;
	private final boolean strings;
	private final boolean numbers;
	private final boolean booleans;

	// names
	@Builder.Default
	private final int namesCharLimit = Character.MAX_CODE_POINT;

	@Builder.Default
	private final String packageName = "czescjestemadas";
	@Builder.Default
	private final int classNameLength = 24;
	@Builder.Default
	private final int fieldNameLength = 16;
	@Builder.Default
	private final int methodNameLength = 32;

	private final List<String> skippedNames;


	// code
	@Builder.Default
	private final boolean fieldShuffle = true;
	@Builder.Default
	private final boolean methodShuffle = true;
	private final boolean junkCodeGen;
	private final String signature;
}
