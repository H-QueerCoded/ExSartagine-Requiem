package subaraki.exsartagine.integration;

import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.commons.io.FilenameUtils;
import subaraki.exsartagine.ExSartagine;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;

import static net.blay09.mods.cookingforblockheads.compat.JsonCompatLoader.parse;

public class CookingForBlockheads
{
	public static void readJSON(){
		ModContainer mod = Loader.instance().getIndexedModList().get(ExSartagine.MODID);
		CraftingHelper.findFiles(mod, "assets/cookingforblockheads/compat", (root) -> true, (root, file) -> {
			String relative = root.relativize(file).toString();
			if (!"json".equals(FilenameUtils.getExtension(file.toString())) || relative.startsWith("_")) {
				return true;
			}
			try (BufferedReader reader = Files.newBufferedReader(file)) {
				parse(reader);
			}
			catch(IOException e){
				e.printStackTrace();
			}
			return true;
		}, true, true);
	}
}
