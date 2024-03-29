import com.google.common.collect.Multimap;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffect;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.UUID;

public class PatchCritical {
    private final EntityDamageByEntityEvent e;
    public PatchCritical(EntityDamageByEntityEvent e, int percent){
        this.e = e;
        this.patchCritical(percent);
    }
    private double getAttackValue(Player p) {
        ItemStack i = p.getItemInHand();
        double c = 1.0;
        if(i != null) c = getAttackDamage(i);//Prendre les dégats de base de l'épée
        int d = p.getActivePotionEffects().stream().filter(potionEffect -> potionEffect.getType().equals(PotionEffectType.INCREASE_DAMAGE)).map(PotionEffect::getAmplifier).findFirst().orElse(-1) + 1;
        int dd = p.getActivePotionEffects().stream().filter(potionEffect -> potionEffect.getType().equals(PotionEffectType.WEAKNESS)).map(PotionEffect::getAmplifier).findFirst().orElse(-1) + 1;
        c -= 0.5D * dd;//Si il a weakness on ajoute 0.5 coeurs. On passe la weakness en priorité
        c *= 1.0D + d * 1.3;//Si il a force on fait * 2.3 pour ses dégats
        //On va ajouter des dégats si l'item a sharpness
        if(i == null) return c;
        if (i.getEnchantments() == null) return c;//pas d'enchantement donc pas de sharpness
        if (!i.containsEnchantment(Enchantment.DAMAGE_ALL)) return c;//si pas sharpness on retire
        c += i.getEnchantmentLevel(Enchantment.DAMAGE_ALL) * 1.25;//on ajoute la sharpness
        return c;
    }

    private boolean isCritical() {
        if (!(e.getDamager() instanceof Player))
            return false;//Le damager ne peut pas être un joueur donc pas de critical
        Player p = (Player) e.getDamager();
        double a = getAttackValue(p);
        DecimalFormat df = new DecimalFormat("0.00");//Les dégats de la force diffère a 0.0000001 environ.
        return !df.format(e.getOriginalDamage(EntityDamageEvent.DamageModifier.BASE)).equals(df.format(a));//Si les dégats sont pas pareil on return true
    }

    private void patchCritical(int percent) {
        //Pourcent de base pour les crits : 50%
        if (!isCritical()) return;//Si pas de critique sert a r
        double d = e.getDamage();
        e.setDamage(e.getDamage() / 1.5 * /*Pour reset le crit*/ (1 + (percent / 100F)));
        System.out.println("Critical has been patched : " + d + " to " + e.getDamage());
    }

    private double getAttackDamage(ItemStack itemStack) {//Trouver sur https://www.spigotmc.org/threads/how-to-get-attack-damage-attributemodifier-from-an-itemstack-as-displayed-on-items-in-game.284455/
        double attackDamage = 1.0;
        UUID uuid = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
        net.minecraft.server.v1_8_R3.ItemStack craftItemStack = CraftItemStack.asNMSCopy(itemStack);
        if(itemStack == null || craftItemStack == null || craftItemStack.getItem() == null) return attackDamage;
        Item item = craftItemStack.getItem();
        if (!(item instanceof ItemSword) && !(item instanceof ItemTool) && !(item instanceof ItemHoe)) return attackDamage;
        Multimap<String, AttributeModifier> map = item.i();
        Collection<AttributeModifier> attributes = map.get(GenericAttributes.ATTACK_DAMAGE.getName());
        if (attributes.isEmpty()) return attackDamage;
        for (AttributeModifier am : attributes)
            if (am.a().toString().equalsIgnoreCase(uuid.toString()) && am.c() == 0) attackDamage += am.d();

        double y = 1;
        for (AttributeModifier am : attributes)
            if (am.a().toString().equalsIgnoreCase(uuid.toString()) && am.c() == 1) y += am.d();

        attackDamage *= y;
        for (AttributeModifier am : attributes)
            if (am.a().toString().equalsIgnoreCase(uuid.toString()) && am.c() == 2)
                attackDamage *= (1 + am.d());

        return attackDamage;
    }
