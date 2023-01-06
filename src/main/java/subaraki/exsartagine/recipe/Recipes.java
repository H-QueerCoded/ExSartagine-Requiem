package subaraki.exsartagine.recipe;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import subaraki.exsartagine.block.ExSartagineBlocks;
import subaraki.exsartagine.init.RecipeTypes;
import subaraki.exsartagine.item.ExSartagineItems;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class Recipes {

    private static final Set<IBlockState> heatSources = new HashSet<>();

    private static final Set<IBlockState> placeable = new HashSet<>();
    protected static final Map<IRecipeType<?>, Map<ResourceLocation,CustomRecipe<?>>> recipes = new HashMap<>();

    public static <I extends IItemHandler,T extends CustomRecipe<I>> void addRecipe(T recipe) {
        ResourceLocation name = new ResourceLocation("crafttweaker","autogenerated_"+i);
        i++;
        addRecipe(name,recipe);
    }
        public static <I extends IItemHandler,T extends CustomRecipe<I>> void addRecipe(ResourceLocation name,T recipe) {
        IRecipeType<I> type = recipe.getType();
        Map<ResourceLocation,CustomRecipe<?>> recs = recipes.get(type);

        if (recs == null) {
            recipes.put(type, new HashMap<>());
            recs = recipes.get(type);
        }
        recs.put(name,recipe);
    }

    public static void addPotRecipe(Ingredient input, ItemStack result) {
        addRecipe(result.getItem().getRegistryName(),new PotRecipe(input, result));
    }

    static int i = 0;
    public static void addWokRecipe(List<Ingredient> ingredients, FluidStack fluid, List<ItemStack> stacks) {
        WokRecipe wokRecipe = new WokRecipe(ingredients,fluid, stacks);
        addRecipe(wokRecipe);
    }

    public static void addSmelterRecipe(Ingredient ingredient, ItemStack itemStack) {
        addRecipe(itemStack.getItem().getRegistryName(),new SmelterRecipe(ingredient, itemStack));
    }

    public static void addKettleRecipe(List<Ingredient> ingredients, Ingredient catalyst, @Nullable FluidStack inputFluid,
                                       @Nullable FluidStack outputFluid, List<ItemStack> results, int cookTime) {
        addRecipe(new KettleRecipe(ingredients, catalyst, inputFluid,outputFluid, results, cookTime));
    }

    public static <T extends IItemHandler,U extends CustomRecipe<T>> boolean hasResult(ItemStack stack, IRecipeType<T> type) {
        return hasResult((T)new ItemStackHandler(NonNullList.from(ItemStack.EMPTY, stack)), type);
    }
    
    public static boolean removePotRecipe(ItemStack output) {
        return getRecipes(RecipeTypes.POT).removeIf(r -> ItemStack.areItemStacksEqual(r.getResult(new ItemStackHandler()), output));
    }
    
    public static boolean removePotRecipe(ItemStack input, ItemStack output) {
        return getRecipes(RecipeTypes.POT).removeIf(r -> r.itemMatch(new ItemStackHandler(NonNullList.from(input))) && ItemStack.areItemStacksEqual(r.getResult(new ItemStackHandler()), output));
    }
    
    public static boolean removePotRecipe(Ingredient input, ItemStack output) {
        boolean changed = false;
        for (ItemStack i : input.getMatchingStacks()) {
            if (removePotRecipe(i, output)) 
                changed = true;
        }
        return changed;
    }

    public static Collection<WokRecipe> getWokRecipes() {
        return getRecipes(RecipeTypes.WOK);
    }

    public static boolean removeWokRecipe(ResourceLocation name) {
        Map<ResourceLocation,WokRecipe> map = getRecipeMap(RecipeTypes.WOK);
        return map.remove(name) != null;
    }

    public static boolean removeSmelterRecipe(ItemStack output) {
        return getRecipes(RecipeTypes.SMELTER).removeIf(r -> ItemStack.areItemStacksEqual(r.getResult(new ItemStackHandler()), output));
    }
    
    public static boolean removeSmelterRecipe(ItemStack input, ItemStack output) {
        return getRecipes(RecipeTypes.SMELTER).removeIf(r -> r.itemMatch(new ItemStackHandler(NonNullList.from(input))) && ItemStack.areItemStacksEqual(r.getResult(new ItemStackHandler()), output));
    }
    
    public static boolean removeSmelterRecipe(Ingredient input, ItemStack output) {
        boolean changed = false;
        for (ItemStack i : input.getMatchingStacks()) {
            if (removeSmelterRecipe(i, output)) 
                changed = true;
        }
        return changed;
    }

    public static Collection<KettleRecipe> getKettleRecipes() {
        return getRecipes(RecipeTypes.KETTLE);
    }
    
    public static boolean removeKettleRecipe(ItemStack output) {
        return getKettleRecipes().removeIf(r -> r.getResults(new ItemStackHandler()).contains(output));
    }
    
    public static boolean removeKettleRecipe(List<ItemStack> outputs) {
        return getKettleRecipes().removeIf(r -> r.getResults(new ItemStackHandler()).containsAll(outputs));
    }
    
    public static boolean removeKettleRecipe(List<Ingredient> inputs, Ingredient catalyst, FluidStack fluid, List<ItemStack> outputs, int cookTime) {
        return getKettleRecipes().removeIf(r -> r.getResults(new ItemStackHandler()).containsAll(outputs)
                                               && r.getResults(new ItemStackHandler()).size() == outputs.size()
                                               && r.getIngredients().containsAll(inputs)
                                               && r.getIngredients().size() == inputs.size()
                                               && (r.getCatalyst().equals(catalyst) || catalyst == null)
                                               && (r.getInputFluid() == null && fluid == null || (r.getInputFluid() != null && r.getInputFluid().containsFluid(fluid)))
                                               && (cookTime == -1 || r.getCookTime() == cookTime));
    }
    

    public static <I extends IItemHandler,T extends CustomRecipe<I>> ItemStack getCookingResult(I handler, IRecipeType<I> type) {
        for (CustomRecipe<I> recipe : getRecipes(type)) {
            if (recipe.itemMatch(handler))
                return recipe.getResult(handler);
        }
        return ItemStack.EMPTY;
    }

    public static <I extends IItemHandler,T extends CustomRecipe<I>> List<ItemStack> getCookingResults(I handler, IRecipeType<I> type) {
        for (CustomRecipe<I> recipe : getRecipes(type)) {
            if (recipe.itemMatch(handler))
                return recipe.getResults(handler);
        }
        return new ArrayList<>();
    }

    public static <I extends IItemHandler,T extends CustomRecipe<I>> CustomRecipe<I> findRecipe(I handler,  IRecipeType<I> type) {
        Collection<T> recipes = getRecipes(type);
        for (CustomRecipe<I> recipe : recipes) {
            if (recipe.itemMatch(handler))
                return recipe;
        }
        return null;
    }

    public static <I extends IItemHandler, F extends IFluidHandler,T extends CustomFluidRecipe<I,F>>
    T findFluidRecipe(I handler,F fluidHandler,IRecipeType<I> type) {
        Collection<T> recipes = getRecipes(type);
        for (T recipe : recipes) {
            if (recipe.match(handler,fluidHandler))
                return recipe;
        }
        return null;
    }

    public static <I extends IItemHandler, F extends IFluidHandler> KettleRecipe findKettleRecipe(I handler,F fluidHandler) {
        return findFluidRecipe(handler,fluidHandler,RecipeTypes.KETTLE);
    }

    public static <I extends IItemHandler,T extends CustomRecipe<I>> boolean hasResult(I handler, IRecipeType<I> type) {
        return getRecipes(type).stream().anyMatch(customRecipe -> customRecipe.itemMatch(handler));
    }

    public static void addHeatSource(IBlockState state) {
        heatSources.add(state);
        addPlaceable(state);
    }

    public static void addHeatSource(Block block) {
        addHeatSources(block.getBlockState().getValidStates());
    }

    public static void addHeatSources(Collection<IBlockState> states) {
        heatSources.addAll(states);
        addPlaceables(states);
    }

    public static void addPlaceable(IBlockState state) {
        placeable.add(state);
    }

    public static void addPlaceable(Block block) {
        addPlaceables(block.getBlockState().getValidStates());
    }

    public static void addPlaceables(Collection<IBlockState> states) {
        placeable.addAll(states);
    }

    public static boolean removeHeatSource(IBlockState state) {
        return heatSources.removeIf(b -> b == state);
    }

    public static void removeHeatSource(Block block) {
        removeHeatSources(block.getBlockState().getValidStates());
    }

    public static boolean removeHeatSources(Collection<IBlockState> states) {
        return heatSources.removeAll(states);
    }

    public static boolean removePlaceable(IBlockState state) {
        return placeable.removeIf(b -> b == state);
    }

    public static void removePlaceable(Block block) {
        removePlaceables(block.getBlockState().getValidStates());
    }

    public static boolean removePlaceables(Collection<IBlockState> states) {
        return placeable.removeAll(states);
    }

    public static <I extends IItemHandler, R extends CustomRecipe<I>> Collection<R> getRecipes(IRecipeType<I> type) {
        Map<ResourceLocation,R> map = getRecipeMap(type);
        if (map != null) {
            return map.values();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <I extends IItemHandler, R extends CustomRecipe<I>> Map<ResourceLocation,R> getRecipeMap(IRecipeType<I> type) {
        return (Map<ResourceLocation, R>) Recipes.recipes.get(type);
    }


    public static boolean isHeatSource(IBlockState state) {
        return heatSources.contains(state);
    }

    public static boolean isPlaceable(IBlockState state) {
        return placeable.contains(state);
    }

    public static void init() {
        addPlaceable(Blocks.FURNACE);
        addHeatSource(Blocks.LIT_FURNACE);
        addPlaceable(ExSartagineBlocks.range_extension);
        addHeatSource(ExSartagineBlocks.range_extension_lit);
        addHeatSource(Blocks.LAVA);

        FurnaceRecipes.instance().addSmelting(ExSartagineItems.pizza_chicken_raw, new ItemStack(ExSartagineItems.pizza_chicken), 0.6f);
        FurnaceRecipes.instance().addSmelting(ExSartagineItems.pizza_meat_raw, new ItemStack(ExSartagineItems.pizza_meat), 0.6f);
        FurnaceRecipes.instance().addSmelting(ExSartagineItems.pizza_sweet_raw, new ItemStack(ExSartagineItems.pizza_sweet), 0.6f);
        FurnaceRecipes.instance().addSmelting(ExSartagineItems.pizza_fish_raw, new ItemStack(ExSartagineItems.pizza_fish), 0.6f);

        FurnaceRecipes.instance().addSmelting(ExSartagineItems.bread_dough, new ItemStack(ExSartagineItems.bread_fine), 0.6f);
        FurnaceRecipes.instance().addSmelting(ExSartagineItems.bread_meat_raw, new ItemStack(ExSartagineItems.bread_meat), 0.6f);
        FurnaceRecipes.instance().addSmelting(ExSartagineItems.bread_veggie_raw, new ItemStack(ExSartagineItems.bread_veggie), 0.6f);

        addPotRecipe(Ingredient.fromItem(Items.EGG), new ItemStack(ExSartagineItems.boiled_egg, 1));
        addPotRecipe(Ingredient.fromItem(Items.BEETROOT_SEEDS), new ItemStack(ExSartagineItems.boiled_beans, 1));
        addPotRecipe(Ingredient.fromItem(Items.POTATO), new ItemStack(ExSartagineItems.boiled_potato, 1));

        addPotRecipe(Ingredient.fromItem(Item.getItemFromBlock(Blocks.STONE)), new ItemStack(ExSartagineItems.salt, 1));

        addPotRecipe(Ingredient.fromItem(ExSartagineItems.spaghetti_raw), new ItemStack(ExSartagineItems.spaghetti_cooked));

        addPotRecipe(Ingredient.fromItem(ExSartagineItems.noodles_chicken), new ItemStack(ExSartagineItems.noodles_chicken_cooked));
        addPotRecipe(Ingredient.fromItem(ExSartagineItems.noodles_fish), new ItemStack(ExSartagineItems.noodles_fish_cooked));
        addPotRecipe(Ingredient.fromItem(ExSartagineItems.noodles_meat), new ItemStack(ExSartagineItems.noodles_meat_cooked));
        addPotRecipe(Ingredient.fromItem(ExSartagineItems.noodles_veggie), new ItemStack(ExSartagineItems.noodles_veggie_cooked));

        addSmelterRecipe(Ingredient.fromStacks(new ItemStack(Blocks.IRON_ORE)), new ItemStack(Items.IRON_INGOT));
        addSmelterRecipe(Ingredient.fromStacks(new ItemStack(Blocks.GOLD_ORE)), new ItemStack(Items.GOLD_INGOT));
        addSmelterRecipe(Ingredient.fromStacks(new ItemStack(Items.CLAY_BALL)), new ItemStack(Items.BRICK));
        addSmelterRecipe(Ingredient.fromStacks(new ItemStack(Blocks.NETHERRACK)), new ItemStack(Items.NETHERBRICK));

        //testRecipes();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void postInit() {
        FurnaceRecipes.instance().getSmeltingList().entrySet().stream()
                .filter(entry -> entry.getKey().getItem() instanceof ItemFood)
                .forEach(entry -> {

                    List<Ingredient> i = new ArrayList<>();
                    i.add(Ingredient.fromStacks(entry.getKey()));

                    List<ItemStack> o = new ArrayList<>();

                    o.add(entry.getValue());

                    WokRecipe wokRecipe =  new WokRecipe(i,null,o);

                    addRecipe(entry.getKey().getItem().getRegistryName(),wokRecipe);

                });
    }

    public static <I extends IItemHandler, R extends CustomRecipe<I>> NonNullList<ItemStack> getRemainingItems(I craftMatrix, World worldIn, IRecipeType<I> type) {

        Collection<R> recipes = getRecipes(type);

        for (R irecipe : recipes) {
            if (irecipe.itemMatch(craftMatrix)) {
                return irecipe.getRemainingItems(craftMatrix);
            }
        }

        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(9, ItemStack.EMPTY);

        for (int i = 0; i < nonnulllist.size(); ++i) {
            nonnulllist.set(i, craftMatrix.getStackInSlot(i));
        }

        return nonnulllist;
    }


}
