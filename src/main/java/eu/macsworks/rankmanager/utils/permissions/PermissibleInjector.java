package eu.macsworks.rankmanager.utils.permissions;

import me.lucko.helper.Events;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.PermissibleBase;

import java.lang.reflect.Field;

//Taken from an old project of mine, which employed this LuckPerms snippet
public class PermissibleInjector {

  private static final Field HUMAN_ENTITY_PERMISSIBLE;

  private static final Field PERMISSIBLE_BASE_ATTACHMENTS;

  static {
    try {
      // Load the permissible field
      Field humanEntityPermissibleField;
      humanEntityPermissibleField = CraftBukkitImplementation.obcClass("entity.CraftHumanEntity").getDeclaredField("perm");
      humanEntityPermissibleField.setAccessible(true);
      HUMAN_ENTITY_PERMISSIBLE = humanEntityPermissibleField;

      // Load the attachments field
      PERMISSIBLE_BASE_ATTACHMENTS = PermissibleBase.class.getDeclaredField("attachments");
      PERMISSIBLE_BASE_ATTACHMENTS.setAccessible(true);
    } catch (ClassNotFoundException | NoSuchFieldException e) {
      throw new ExceptionInInitializerError(e);
    }

    //Subscribe to the event via helper
    Events.subscribe(PlayerJoinEvent.class)
        .handler(event -> {
          try {
            inject(event.getPlayer());
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        });
  }

  public static void inject(Player player) throws Exception {
    HUMAN_ENTITY_PERMISSIBLE.set(player, new FakePermissible(player));
  }


}