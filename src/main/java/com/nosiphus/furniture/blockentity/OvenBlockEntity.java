package com.nosiphus.furniture.blockentity;

import com.nosiphus.furniture.client.menu.OvenMenu;
import com.nosiphus.furniture.core.ModBlockEntities;
import com.nosiphus.furniture.recipe.CookingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class OvenBlockEntity extends BlockEntity implements MenuProvider {

    private final ItemStackHandler itemHandler = new ItemStackHandler(10) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int progress1 = 0;
    private int progress2 = 0;
    private int progress3 = 0;
    private int progress4 = 0;
    private int maxProgress = 3;

    public OvenBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.OVEN.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> OvenBlockEntity.this.progress1;
                    case 1 -> OvenBlockEntity.this.progress2;
                    case 2 -> OvenBlockEntity.this.progress3;
                    case 3 -> OvenBlockEntity.this.progress4;
                    case 4 -> OvenBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> OvenBlockEntity.this.progress1 = value;
                    case 1 -> OvenBlockEntity.this.progress2 = value;
                    case 2 -> OvenBlockEntity.this.progress3 = value;
                    case 3 -> OvenBlockEntity.this.progress4 = value;
                    case 4 -> OvenBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 5;
            }
        };
    }

    public int getContainerSize() {
        return 10;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.nfm.oven");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new OvenMenu(id, inventory, this, this.data);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putInt("oven.progress1", this.progress1);
        tag.putInt("oven.progress2", this.progress2);
        tag.putInt("oven.progress3", this.progress3);
        tag.putInt("oven.progress4", this.progress4);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        progress1 = tag.getInt("oven.progress1");
        progress2 = tag.getInt("oven.progress2");
        progress3 = tag.getInt("oven.progress3");
        progress4 = tag.getInt("oven.progress4");
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, OvenBlockEntity blockEntity) {
        if(level != null) {
            if(level.isClientSide()) {
                return;
            }

            if(hasRecipe(blockEntity, 1)) {
                blockEntity.progress1++;
                setChanged(level, pos, state);
                if(blockEntity.progress1 >= blockEntity.maxProgress) {
                    craftItem(blockEntity, 1);
                }
            } else if(hasRecipe(blockEntity, 3)) {
                blockEntity.progress2++;
                setChanged(level, pos, state);
                if(blockEntity.progress2 >= blockEntity.maxProgress) {
                    craftItem(blockEntity, 3);
                }
            } else {
                blockEntity.resetProgress(1);
                blockEntity.resetProgress(3);
                setChanged(level, pos, state);
            }
        }
    }

    private void resetProgress(int inputSlot) {
        if (inputSlot == 1) {
            this.progress1 = 0;
        } else if (inputSlot == 3) {
            this.progress2 = 0;
        } else if (inputSlot == 5) {
            this.progress3 = 0;
        } else if (inputSlot == 7) {
            this.progress4 = 0;
        }
    }

    private static void craftItem(OvenBlockEntity blockEntity, int inputSlot) {
        Level level = blockEntity.level;
        SimpleContainer inventory = new SimpleContainer(blockEntity.itemHandler.getSlots());
        for (int i = 0; i < blockEntity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, blockEntity.itemHandler.getStackInSlot(i));
        }
        Optional<CookingRecipe> recipe = level.getRecipeManager().getRecipeFor(CookingRecipe.Type.INSTANCE, inventory, level);
        if (hasRecipe(blockEntity, inputSlot)) {
            blockEntity.itemHandler.extractItem(inputSlot, 1, false);
            blockEntity.itemHandler.setStackInSlot(inputSlot + 1, new ItemStack(recipe.get().getResultItem().getItem(),
                    blockEntity.itemHandler.getStackInSlot(inputSlot + 1).getCount() + 1));
            blockEntity.resetProgress(inputSlot);
        }
    }

    private static boolean hasRecipe(OvenBlockEntity blockEntity, int inputSlot) {
        Level level = blockEntity.level;
        SimpleContainer inventory = new SimpleContainer(blockEntity.itemHandler.getSlots());

        for (int i = 0; i < blockEntity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, blockEntity.itemHandler.getStackInSlot(i));
        }

        boolean hasRedstoneBlockInFirstSlot = blockEntity.itemHandler.getStackInSlot(0).getItem() == Items.REDSTONE_BLOCK;

        Optional<CookingRecipe> recipe = level.getRecipeManager().getRecipeFor(CookingRecipe.Type.INSTANCE, inventory, level);

        return hasRedstoneBlockInFirstSlot && recipe.isPresent() && canInsertAmountIntoOutputSlot(inventory, inputSlot + 1) &&
                canInsertItemIntoOutputSlot(inventory, recipe.get().getResultItem(), inputSlot + 1);
    }

    private static boolean canInsertItemIntoOutputSlot(SimpleContainer inventory, ItemStack stack, int outputSlot) {
        return inventory.getItem(outputSlot).getItem() == stack.getItem() || inventory.getItem(outputSlot).isEmpty();
    }

    private static boolean canInsertAmountIntoOutputSlot(SimpleContainer inventory, int outputSlot) {
        return inventory.getItem(outputSlot).getMaxStackSize() > inventory.getItem(outputSlot).getCount();
    }

    public boolean stillValid(Player player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return !(player.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) > 64.0D);
        }
    }


}