package eu.macsworks.rankmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import eu.macsworks.rankmanager.RankManager;
import eu.macsworks.rankmanager.objects.Group;
import eu.macsworks.rankmanager.utils.ColorTranslator;
import java.util.Optional;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("rm|rankmanager")
@CommandPermission("rm.admin")
public class RankManagerCommand extends BaseCommand {

  @Dependency
  private RankManager rankManager;

  @HelpCommand
  public void help(CommandSender sender, CommandHelp help){
    help.showHelp();
  }

  @Subcommand("group|gr|g")
  public class GroupCommands extends BaseCommand {

    @Subcommand("create|new")
    @CommandCompletion("<groupname>")
    public void groupCreate(CommandSender sender, String name) {
      Optional<Group> alreadyExisting = rankManager.getGroupManager().getGroup(name);
      if (alreadyExisting.isPresent()) {
        sender.sendMessage(rankManager.getMacsPluginLoader().getMessage("group-already-exists"));
        return;
      }

      Group newGroup = new Group(name);
      rankManager.getGroupManager().addGroup(newGroup);
      sender.sendMessage(rankManager.getMacsPluginLoader().getMessage("group-created"));
    }


    @Subcommand("delete|remove")
    @CommandCompletion("@groups")
    public void groupRemove(CommandSender sender, String groupName) {
      Optional<Group> group = rankManager.getGroupManager().getGroup(groupName);
      if (group.isEmpty()) {
        sender.sendMessage(rankManager.getMacsPluginLoader().getMessage("group-doesnt-exist"));
        return;
      }

      rankManager.getGroupManager().removeGroup(group.get());
      sender.sendMessage(rankManager.getMacsPluginLoader().getMessage("group-deleted"));
    }

    @Subcommand("prefix")
    public class GroupPrefixCommands extends BaseCommand {

      @Subcommand("set")
      @CommandCompletion("<group> <prefix>")
      public void groupCreate(CommandSender sender, Group group, String prefix) {
        group.setPrefix(ColorTranslator.translate(prefix));
        sender.sendMessage(rankManager.getMacsPluginLoader().getMessage("prefix-set"));
      }


      @Subcommand("delete|remove")
      @CommandCompletion("@groups")
      public void groupRemove(CommandSender sender, Group group) {
        group.setPrefix("");
        sender.sendMessage(rankManager.getMacsPluginLoader().getMessage("prefix-deleted"));
      }
    }

    @Subcommand("user|usr|u")
    public class GroupUserCommands extends BaseCommand {

      @Subcommand("addperm")
      @CommandCompletion("@groups")
      public void groupPermAddPlayer(CommandSender sender, Group group, Player player) {
        rankManager.getGroupManager().setPermanentGroupForPlayer(player.getUniqueId(), group);
        sender.sendMessage(
            rankManager.getMacsPluginLoader().getMessage("member-permanent-added"));
      }

      @Subcommand("addtemp")
      @CommandCompletion("@groups")
      public void groupTempAddPlayer(CommandSender sender, Group group, Player player,
          String expiration) {
        long time = convertTimeToSeconds(expiration);

        rankManager.getGroupManager().setGroupForPlayer(player.getUniqueId(), group, time);
        sender.sendMessage(rankManager.getMacsPluginLoader().getMessage("member-timed-added"));
      }

      @Subcommand("remove")
      @CommandCompletion("@groups")
      public void groupRemovePlayer(CommandSender sender, Group group, Player player) {
        group.removeMember(player.getUniqueId());
        sender.sendMessage(rankManager.getMacsPluginLoader().getMessage("member-removed"));
      }


      private long convertTimeToSeconds(String timeStr) {
        //Replace everything to have no separators
        timeStr = timeStr.replace(" ", "");
        timeStr = timeStr.replace(":", "");
        timeStr = timeStr.replace("_", "");
        timeStr = timeStr.replace("-", "");

        long totalTime = 0L;
        StringBuilder currentNumber = new StringBuilder();

        for (int i = 0; i < timeStr.length(); i++) {
          char c = timeStr.charAt(i);
          if (Character.isDigit(
              c)) { //If this is not a letter, we are still waiting for the complete number, store it here
            currentNumber.append(c);
            continue;
          }

          //We have reached a non-digit, multiply the stored number by the unit-to-second coefficient and add it to the time
          totalTime += Integer.parseInt(currentNumber.toString()) * switch (c) {
            case 'd' -> 86400L;
            case 'h' -> 3600L;
            case 'm' -> 60L;
            case 's' -> 1L;
            default -> 0L;
          };

          currentNumber = new StringBuilder();
        }

        //Add any trailing number as it could be an unspecified second value
        if (!currentNumber.isEmpty())
          totalTime += Long.parseLong(currentNumber.toString());

        return totalTime;
      }
    }

    @Subcommand("permission|perm|p")
    public class GroupPermissionCommands extends BaseCommand {

      @Subcommand("add|new")
      @CommandCompletion("@groups")
      public void groupPermAdd(CommandSender sender, Group group, String permission) {
        group.addPermission(permission);
        sender.sendMessage(rankManager.getMacsPluginLoader().getMessage("permission-added"));
      }

      @Subcommand("delete|remove")
      @CommandCompletion("@groups")
      public void groupPermRemove(CommandSender sender, Group group, String permission) {
        group.removePermission(permission);
        sender.sendMessage(rankManager.getMacsPluginLoader().getMessage("permission-removed"));
      }
    }
  }


}
