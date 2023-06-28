package subaraki.exsartagine.integration;

import com.google.common.collect.Lists;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.recipes.InspirationsRecipes;
import net.minecraft.init.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreIngredient;
import subaraki.exsartagine.recipe.ModRecipes;

public class Inspirations {
    public static void addRecipes(){
        if(!Config.cauldronStew) return;
        //int bowlSize = Config.enableBiggerCauldron ? 250 : 333;
        //base recipes
        ModRecipes.addKettleRecipe(Lists.newArrayList(new OreIngredient("mushroomAny")), null,
                new FluidStack(FluidRegistry.WATER, 250),
                new FluidStack(InspirationsRecipes.mushroomStew, 250), Lists.newArrayList(), 200);
        ModRecipes.addKettleRecipe(Lists.newArrayList(Ingredient.fromItem(Items.BEETROOT)), null,
                new FluidStack(FluidRegistry.WATER, 100),
                new FluidStack(InspirationsRecipes.beetrootSoup, 100), Lists.newArrayList(), 80);
        ModRecipes.addKettleRecipe(Lists.newArrayList(new OreIngredient("cookedPotato")), null,
                new FluidStack(InspirationsRecipes.mushroomStew, 250),
                new FluidStack(InspirationsRecipes.potatoSoup, 250), Lists.newArrayList(), 200);
        ModRecipes.addKettleRecipe(Lists.newArrayList(Ingredient.fromItem(Items.COOKED_RABBIT)), null,
                new FluidStack(InspirationsRecipes.potatoSoup, 500),
                new FluidStack(InspirationsRecipes.rabbitStew, 500), Lists.newArrayList(), 400);
        //combined recipes
        ModRecipes.addKettleRecipe(Lists.newArrayList(new OreIngredient("mushroomAny"),
                new OreIngredient("cookedPotato")), null, new FluidStack(FluidRegistry.WATER, 250),
                new FluidStack(InspirationsRecipes.potatoSoup, 250), Lists.newArrayList(), 200);
    }
}
