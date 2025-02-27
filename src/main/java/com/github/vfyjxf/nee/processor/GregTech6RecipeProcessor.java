package com.github.vfyjxf.nee.processor;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.IRecipeHandler;
import cpw.mods.fml.relauncher.ReflectionHelper;
import gregapi.NEI_RecipeMap;
import gregapi.NEI_RecipeMap.FixedPositionedStack;
import gregapi.data.FL;
import gregapi.recipes.Recipe;
import net.minecraft.item.ItemStack;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author vfyjxf
 */
public class GregTech6RecipeProcessor implements IRecipeProcessor {
    @Override
    public Set<String> getAllOverlayIdentifier() {
        Set<String> identifiers = new HashSet<>();
        for (Recipe.RecipeMap tMap : Recipe.RecipeMap.RECIPE_MAPS.values()) {
            if (tMap.mNEIAllowed) {
                identifiers.add(tMap.mNameNEI);
            }
        }
        return identifiers;
    }

    @Override
    public String getRecipeProcessorId() {
        return "GregTech6";
    }

    @Override
    public List<PositionedStack> getRecipeInput(IRecipeHandler recipe, int recipeIndex, String identifier) {
        for (String ident : getAllOverlayIdentifier()) {
            if (ident.equals(identifier)) {
                List<PositionedStack> recipeInputs = new ArrayList<>(recipe.getIngredientStacks(recipeIndex));
                //remove fluid
                recipeInputs.removeIf(positionedStack -> FL.getFluid(positionedStack.item, true) != null || positionedStack.item.stackSize == 0);
                //try to remove machine
                if (recipe instanceof NEI_RecipeMap) {
                    Field mRecipeMapField = ReflectionHelper.findField(NEI_RecipeMap.class, "mRecipeMap");
                    Recipe.RecipeMap mRecipeMap = null;
                    try {
                        mRecipeMap = (Recipe.RecipeMap) mRecipeMapField.get(recipe);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    if (mRecipeMap != null) {
                        ItemStack lastItem = (recipeInputs.get(recipeInputs.size() - 1)).items[0];
                        for (ItemStack stack : mRecipeMap.mRecipeMachineList) {
                            if (ItemStack.areItemStackTagsEqual(lastItem, stack)) {
                                recipeInputs.remove(recipeInputs.size() - 1);
                                break;
                            }
                        }
                    }
                }
                return recipeInputs;
            }
        }
        return null;
    }

    @Override
    public List<PositionedStack> getRecipeOutput(IRecipeHandler recipe, int recipeIndex, String identifier) {
        for (String ident : getAllOverlayIdentifier()) {
            if (ident.equals(identifier)) {
                List<PositionedStack> recipeOutput = new ArrayList<>(recipe.getOtherStacks(recipeIndex));
                recipeOutput.removeIf(positionedStack -> FL.getFluid(positionedStack.item, true) != null || positionedStack.item.stackSize == 0);
                //try to remove item output if it's chance != 100%
                recipeOutput.removeIf(positionedStack -> positionedStack instanceof FixedPositionedStack && ((FixedPositionedStack) positionedStack).mChance > 0 && ((FixedPositionedStack) positionedStack).mChance != ((FixedPositionedStack) positionedStack).mMaxChance);
                return recipeOutput;
            }
        }
        return null;
    }
}
