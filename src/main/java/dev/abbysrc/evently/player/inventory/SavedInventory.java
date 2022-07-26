package dev.abbysrc.evently.player.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.UUID;

/**
 * Found on the SpigotMC forums, edited by me
 * @author smorce / abbysrc
 */
public class SavedInventory {

    public static HashMap<UUID, SavedInventory> inventories = new HashMap<>();

    public static @Nullable SavedInventory get(Player p) {
        return inventories.get(p.getUniqueId());
    }

    public static @Nullable SavedInventory save(Player p) {
        inventories.put(p.getUniqueId(), new SavedInventory(p.getInventory()));
        p.getInventory().clear();
        return get(p);
    }

    private final ItemStack[] contents;
    private final ItemStack[] armor;
    private final int level;
    private final float exp;

    public SavedInventory(PlayerInventory inventory) {
        this(inventory.getContents(), inventory.getArmorContents(),
                inventory.getHolder() instanceof Player ? ((Player)inventory.getHolder()).getLevel() : 0,
                inventory.getHolder() instanceof Player ? ((Player)inventory.getHolder()).getExp() : 0F);
    }

    public SavedInventory(ItemStack[] contents, ItemStack[] armor, int level, float exp) {
        this.contents = contents;
        this.armor = armor;
        this.level = level;
        this.exp = exp;
    }

    public ItemStack[] getContents() {
        return contents;
    }

    public ItemStack[] getArmor() {
        return armor;
    }

    public float getExp() {
        return exp;
    }

    public int getLevel() {
        return level;
    }

    public void apply(Player player) {
        Player p = player.getPlayer();
        PlayerInventory inv = p.getInventory();
        inv.setContents(this.contents);
        inv.setArmorContents(this.armor);
        p.setLevel(this.level);
        p.setExp(this.exp);
        p.updateInventory();
    }
}
