package eu.macsworks.rankmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import eu.macsworks.rankmanager.RankManager;
import eu.macsworks.rankmanager.objects.Group;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.bukkit.entity.Player;

@CommandAlias("rank")
public class RankCommand extends BaseCommand {

  @Dependency
  private RankManager rankManager;

  @Default
  public void rankDisplay(Player player){
    Group group = rankManager.getGroupManager().getGroupForPlayer(player.getUniqueId());
    long expiration = group.getExpirationDate(player.getUniqueId());

    player.sendMessage(rankManager.getMacsPluginLoader().getMessage("rank-info")
        .replace("%name", group.getName())
        .replace("%prefix", group.getPrefix())
        .replace("%expiration", expiration == Long.MAX_VALUE ? rankManager.getMacsPluginLoader().getMessage("infinite") : getTimeString(expiration)));
  }

  private String getTimeString(long epochSeconds){
    if(epochSeconds == 0) return rankManager.getMacsPluginLoader().getMessage("infinite");

    String output = "";
    Duration duration = Duration.ofSeconds(epochSeconds);
    if(duration.toDaysPart() > 0) {
      output += duration.toDays() + "d ";
      duration = duration.minus(duration.toDays(), ChronoUnit.DAYS);
    }

    if(duration.toHoursPart() > 0) {
      output += duration.toHours() + "h ";
      duration = duration.minus(duration.toHours(), ChronoUnit.HOURS);
    }
    if(duration.toMinutesPart() > 0) {
      output += duration.toMinutes() + "m ";
      duration = duration.minus(duration.toMinutes(), ChronoUnit.MINUTES);
    }

    if(duration.toSecondsPart() > 0) output += duration.toSeconds() + "s";
    return output;
  }

}
