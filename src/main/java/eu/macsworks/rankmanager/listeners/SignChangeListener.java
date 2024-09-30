package eu.macsworks.rankmanager.listeners;

import eu.macsworks.rankmanager.RankManager;
import eu.macsworks.rankmanager.objects.Group;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignChangeListener implements Listener {

  @EventHandler
  public void onSignChange(SignChangeEvent event) {
    if(!event.getLine(0).equalsIgnoreCase("[RankInfo]")) return;

    Group playerGroup = RankManager.getInstance().getGroupManager().getGroupForPlayer(event.getPlayer().getUniqueId());
    event.setLine(1, event.getPlayer().getName());
    event.setLine(2, playerGroup.getName());
    event.setLine(3, playerGroup.getPrefix());
  }


}
