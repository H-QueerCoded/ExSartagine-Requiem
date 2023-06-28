package subaraki.exsartagine.integration;

import net.minecraftforge.fml.common.Loader;

public class Integration {
    public static void postInit(){
        if(Loader.isModLoaded("betterwithmods")){ BetterWithMods.addPlaceables(); }
        if(Loader.isModLoaded("pyrotech")){Pyrotech.addPlaceables();}
        if(Loader.isModLoaded("inspirations")){Inspirations.addRecipes();}
    }
}
