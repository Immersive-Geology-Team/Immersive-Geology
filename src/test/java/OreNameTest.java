import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class OreNameTest
{
	private static final Map<String, String> entries = new HashMap<>();

	private static final Path EN_US = Path.of("src/main/resources/assets/immersivegeology/lang/en_us.json");

	@BeforeAll
	static void loadLanguage() throws IOException
	{
		Pattern pair = Pattern.compile("\"((?:[^\"\\\\]|\\\\.)*)\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"");
		Matcher matcher = pair.matcher(Files.readString(EN_US));
		while(matcher.find()) entries.put(matcher.group(1), matcher.group(2));

		Language.inject(new Language()
		{
			@Override
			public String getOrDefault(String key, String fallback)
			{
				return entries.getOrDefault(key, fallback);
			}

			@Override
			public boolean has(String key)
			{
				return entries.containsKey(key);
			}

			@Override
			public boolean isDefaultRightToLeft()
			{
				return false;
			}

			@Override
			public net.minecraft.util.FormattedCharSequence getVisualOrder(net.minecraft.network.chat.FormattedText text)
			{
				return net.minecraft.util.FormattedCharSequence.EMPTY;
			}
		});
	}

	private static String graded(String grade, String host, String mineral)
	{
		return Component.translatable("block.immersivegeology.ore_block",
				Component.translatable("material.immersivegeology.ore."+grade),
				Component.translatable("material.immersivegeology."+host),
				Component.translatable("material.immersivegeology."+mineral)).getString();
	}

	private static String ungraded(String host, String mineral)
	{
		return Component.translatable("block.immersivegeology.ore_block.ungraded",
				Component.translatable("material.immersivegeology.ore.normal"),
				Component.translatable("material.immersivegeology."+host),
				Component.translatable("material.immersivegeology."+mineral)).getString();
	}

	@Test
	void gradedOreReadsNaturally()
	{
		assertEquals("Rich Stone Pyrite Ore", graded("rich", "stone", "pyrite"));
		assertEquals("Poor Granite Pyrite Ore", graded("poor", "granite", "pyrite"));
	}

	@Test
	void normalGradeHasNoStraySpace()
	{
		String name = ungraded("stone", "pyrite");
		assertEquals("Stone Pyrite Ore", name);
		assertFalse(name.startsWith(" "), "normal grade left a leading space: '"+name+"'");
		assertFalse(name.contains("  "), "normal grade left a doubled space: '"+name+"'");
	}

	@Test
	void unreferencedGradeArgumentIsAccepted()
	{
		assertFalse(ungraded("stone", "pyrite").contains("ore_block"),
				"pattern failed to format and fell back to the key");
	}

	@Test
	void patternIsFullyRewritable()
	{
		assertEquals("Stone-hosted Pyrite (Rich)", render("%2$s-hosted %3$s (%1$s)", "rich", "stone", "pyrite"));
		assertEquals("Rich Pyrite Stone", render("%1$s %3$s %2$s", "rich", "stone", "pyrite"));
	}

	private static String render(String pattern, String grade, String host, String mineral)
	{
		entries.put("test.pattern", pattern);
		return Component.translatable("test.pattern",
				Component.translatable("material.immersivegeology.ore."+grade),
				Component.translatable("material.immersivegeology."+host),
				Component.translatable("material.immersivegeology."+mineral)).getString();
	}

	private static String oreItem(String flag, String material)
	{
		return Component.translatable("item.immersivegeology."+flag,
				Component.translatable("material.immersivegeology."+material).getString()).getString();
	}

	private static String nativeOreItem(String flag, String metal)
	{
		String qualified = Component.translatable("material.immersivegeology.native_material",
				Component.translatable("material.immersivegeology."+metal).getString()).getString();
		return Component.translatable("item.immersivegeology."+flag, qualified).getString();
	}

	@Test
	void oreItemNamesAreUnchanged()
	{
		assertEquals("Poor Pyrite", oreItem("poor_ore", "pyrite"));
		assertEquals("Pyrite", oreItem("normal_ore", "pyrite"));
		assertEquals("Rich Pyrite", oreItem("rich_ore", "pyrite"));
	}

	@Test
	void nativeMetalOreKeepsItsQualifier()
	{
		assertEquals("Rich Native Copper", nativeOreItem("rich_ore", "copper"));
		String normal = nativeOreItem("normal_ore", "copper");
		assertEquals("Native Copper", normal);
		assertFalse(normal.startsWith(" "), "normal grade left a leading space: '"+normal+"'");
	}
}
