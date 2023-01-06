package subaraki.exsartagine.tileentity.util;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import subaraki.exsartagine.recipe.CustomFluidRecipe;
import subaraki.exsartagine.recipe.IRecipeType;
import subaraki.exsartagine.recipe.Recipes;
import subaraki.exsartagine.tileentity.WokBlockEntity;

import java.util.List;

public abstract class FluidRecipeBlockEntity<T extends IItemHandler,U extends IFluidHandler,V extends CustomFluidRecipe<T,U>> extends TileEntity implements ITickable {

    protected T inventoryInput;

    protected T inventoryOutput;
    protected U fluidInventoryInput;
    protected U fluidInventoryOutput;

    protected V cached;

    protected IRecipeType<T> recipeType;

    public int progress;
    public boolean running;
    public int cookTime = -1;

    protected FluidRecipeBlockEntity() {
        initInventory();
    }

    protected abstract void initInventory();

    @Override
    public void update() {

    }

    public T getInventoryInput() {
        return inventoryInput;
    }

    public T getInventoryOutput() {
        return inventoryOutput;
    }

    public U getFluidInventoryInput() {
        return fluidInventoryInput;
    }

    public V getOrCreateRecipe() {
        if (cached != null && cached.match(inventoryInput, fluidInventoryInput)) {
            return cached;
        }
        return cached = Recipes.findFluidRecipe(inventoryInput, fluidInventoryInput,recipeType);
    }

    public boolean canStart() {
        V recipe = getOrCreateRecipe();
        if (recipe == null)
            return false;

        if (!checkFluids(recipe)) return false;


        List<ItemStack> results = recipe.getResults(inventoryInput);
        for (ItemStack stack : results) {
            ItemStack remainder = stack.copy();
            for (int i = 0; i < inventoryInput.getSlots(); i++) {
                remainder = inventoryInput.insertItem(i, remainder, true);
                if (remainder.isEmpty()) break;
            }
            if (!remainder.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public void decreaseProgress() {
        if (progress > 0) {
            progress--;
            markDirty();
        }
        running = false;
    }

    public void process() {
        progress = 0;
        processItems();
        processFluids();
    }

    public void start() {
        running = true;
        cookTime = cached.getCookTime();
    }

    public abstract void processItems();

    public void processFluids() {
        if (cached.getInputFluid() != null) {
            fluidInventoryInput.drain(cached.getInputFluid().amount, true);
        }
        if (cached.getOutputFluid() != null) {
            fluidInventoryOutput.fill(cached.getOutputFluid(), true);
        }
    }

    public boolean checkFluids(V recipe) {
        if (!recipe.fluidMatch(fluidInventoryInput)) {
            return false;
        }
        return checkFluidInv(recipe);
    }

    public abstract boolean checkFluidInv(V recipe);

}
