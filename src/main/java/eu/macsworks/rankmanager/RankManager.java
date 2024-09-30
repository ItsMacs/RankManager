/**
 * RankManager developed by Macs @ MacsWorks.eu in 2024
 **/

package eu.macsworks.rankmanager;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.InvalidCommandArgument;
import eu.macsworks.rankmanager.commands.RankCommand;
import eu.macsworks.rankmanager.commands.RankManagerCommand;
import eu.macsworks.rankmanager.listeners.SignChangeListener;
import eu.macsworks.rankmanager.managers.GroupManager;
import eu.macsworks.rankmanager.objects.Group;
import eu.macsworks.rankmanager.utils.PlaceholderAPIHook;
import eu.macsworks.rankmanager.utils.PluginLoader;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class RankManager extends ExtendedJavaPlugin {

  @Setter(AccessLevel.PRIVATE) @Getter //Double getter needed as otherwise the singleton isn't gettable
  private static RankManager instance = null;

  private PluginLoader macsPluginLoader;
  private GroupManager groupManager;

  @Override
  public void enable() {
    setInstance(this);

    //Save the default config in a non-overriding way
    saveDefaultConfig();

    groupManager = new GroupManager();

    macsPluginLoader = new PluginLoader();
    macsPluginLoader.load();

    loadTasks();
    loadEvents();
    loadCommands();

    //If PAPI is enabled (it's a softdepend) enable the integration
    if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) new PlaceholderAPIHook().register();

    Bukkit.getLogger().info("--------------------------------------");
    Bukkit.getLogger().info("RankManager was enabled successfully!");
    Bukkit.getLogger().info("--------------------------------------");
  }

  private void loadTasks() {
    if(!getConfig().getBoolean("supply-prefix")) return;

    //Automatically set the player's group prefix. This is enabled via config.
    Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
      Bukkit.getOnlinePlayers().forEach(p -> {
        Group group = groupManager.getGroupForPlayer(p.getUniqueId());
        p.setDisplayName(group.getPrefix() + " " +  ChatColor.RESET + p.getName());
      });
    }, 0L, 40L);

    //Avoid data loss by asynchronously saving each minute
    Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
      macsPluginLoader.save();
    }, 0L, 20L * 60);
  }

  private void loadEvents() {
    Bukkit.getPluginManager().registerEvents(new SignChangeListener(), this);
  }

  @SuppressWarnings({"deprecation"})
  private void loadCommands() {
    BukkitCommandManager commandManager = new BukkitCommandManager(this);
    commandManager.enableUnstableAPI("brigadier");
    commandManager.enableUnstableAPI("help");

    commandManager.getCommandCompletions().registerAsyncCompletion("groups", c -> groupManager.getGroups().stream().map(Group::getName).toList());
    commandManager.getCommandContexts().registerContext(Group.class, c -> {
      String name = c.popFirstArg();
      Optional<Group> group = groupManager.getGroup(name);
      return group.orElseThrow(() -> new InvalidCommandArgument(true));
    });

    commandManager.registerCommand(new RankManagerCommand());
    commandManager.registerCommand(new RankCommand());
  }

  @Override
  public void disable() {
    macsPluginLoader.save();

    Bukkit.getLogger().info("--------------------------------------");
    Bukkit.getLogger().info("RankManager was disabled successfully!");
    Bukkit.getLogger().info("--------------------------------------");
  }
}
