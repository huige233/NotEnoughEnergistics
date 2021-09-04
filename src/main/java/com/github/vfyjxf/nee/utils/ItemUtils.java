package com.github.vfyjxf.nee.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import mezz.jei.api.gui.IGuiIngredient;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.List;

import static com.github.vfyjxf.nee.NEEConfig.*;

/**
 * @author vfyjxf
 */
public final class ItemUtils {

    public static Gson gson = new Gson();

    private static List<StackProcessor> getTransformItemBlacklist() {
        List<StackProcessor> transformItemBlacklist = new ArrayList<>();
        for (String itemJsonString : itemBlacklist) {
            JsonObject jsonObject = new JsonParser().parse(itemJsonString).getAsJsonObject();
            if (jsonObject != null) {
                String itemName = jsonObject.get("itemName").getAsString();
                if (itemName == null || itemName.isEmpty()) {
                    continue;
                }
                int meta = itemJsonString.contains("meta") ? Integer.parseInt(jsonObject.get("meta").getAsString()) : 0;
                String nbtJsonString = itemJsonString.contains("nbt") ? jsonObject.get("nbt").getAsString() : "";
                ItemStack currentStack = GameRegistry.makeItemStack(itemName, meta, 1, nbtJsonString);
                String recipeType = itemJsonString.contains("recipeType") ? jsonObject.get("name").getAsString() : "";
                transformItemBlacklist.add(new StackProcessor(currentStack, recipeType));
            }
        }
        return transformItemBlacklist;
    }

    private static List<StackProcessor> getTransformItemPriorityList() {
        List<StackProcessor> transformItemPriorityList = new ArrayList<>();
        for (String itemJsonString : itemPriorityList) {
            JsonObject jsonObject = new JsonParser().parse(itemJsonString).getAsJsonObject();
            if (jsonObject != null) {
                String itemName = jsonObject.get("itemName").getAsString();
                if (itemName == null || itemName.isEmpty()) {
                    continue;
                }
                int meta = itemJsonString.contains("meta") ? Integer.parseInt(jsonObject.get("meta").getAsString()) : 0;
                String nbtJsonString = itemJsonString.contains("nbt") ? jsonObject.get("nbt").getAsString() : "";
                ItemStack currentStack = GameRegistry.makeItemStack(itemName, meta, 1, nbtJsonString);
                String recipeType = itemJsonString.contains("recipeType") ? jsonObject.get("name").getAsString() : "";
                transformItemPriorityList.add(new StackProcessor(currentStack, recipeType));
            }
        }
        return transformItemPriorityList;
    }

    public static boolean isPreferItems(ItemStack itemStack, String recipeType) {
        ItemStack stack = itemStack.copy();
        stack.setCount(1);
        for (StackProcessor stackProcessor : getTransformItemPriorityList()) {
            if (ItemStack.areItemStacksEqual(stack, stackProcessor.itemStack)) {
                String currentRecipeType = stackProcessor.recipeType;
                if (currentRecipeType == null || currentRecipeType.isEmpty()) {
                    return true;
                } else if (recipeType.equals(currentRecipeType)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isPreferItems(ItemStack itemStack) {
        ItemStack stack = itemStack.copy();
        stack.setCount(1);
        for (StackProcessor stackProcessor : getTransformItemPriorityList()) {
            if (ItemStack.areItemStacksEqual(stack, stackProcessor.itemStack)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isInBlackList(ItemStack itemStack, String recipeType) {
        ItemStack stack = itemStack.copy();
        stack.setCount(1);
        for (StackProcessor stackProcessor : getTransformItemBlacklist()) {
            if (ItemStack.areItemStacksEqual(stack, stackProcessor.itemStack)) {
                String currentRecipeType = stackProcessor.recipeType;
                if (currentRecipeType == null || currentRecipeType.isEmpty()) {
                    return true;
                } else if (recipeType.equals(currentRecipeType)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static ItemStack getPreferModItem(IGuiIngredient<ItemStack> ingredient) {
        for (String currentId : modPriorityList) {
            for (ItemStack currentIngredient : ingredient.getAllIngredients()) {
                String itemModid = currentIngredient.getItem().getRegistryName().getNamespace();
                if (itemModid.equals(currentId)) {
                    return currentIngredient;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    public static boolean isPreferModItem(ItemStack stack) {
        for (String currentId : modPriorityList) {
            String itemModid = stack.getItem().getRegistryName().getNamespace();
            if (itemModid.equals(currentId)) {
                return true;
            }
        }
        return false;
    }

    public static int getIngredientIndex(ItemStack stack, List<ItemStack> currentIngredients) {
        ItemStack stackInput = stack.copy();
        stackInput.setCount(1);
        for (int i = 0; i < currentIngredients.size(); i++) {
            ItemStack currentStack = currentIngredients.get(i).copy();
            currentStack.setCount(1);
            if (ItemStack.areItemStacksEqual(stackInput, currentStack)) {
                return i;
            }
        }
        return -1;
    }
}
