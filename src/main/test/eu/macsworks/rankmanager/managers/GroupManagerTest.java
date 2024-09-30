package eu.macsworks.rankmanager.managers;

import static org.junit.jupiter.api.Assertions.*;

import eu.macsworks.rankmanager.objects.Group;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GroupManagerTest {

  private GroupManager groupManager;

  @BeforeEach
  void setUp() {
    groupManager = new GroupManager();
  }

  @Test
  void addGroup() {
    Group group = new Group("testgroup");

    groupManager.addGroup(group);
  }

  @Test
  void removeGroup() {
    addGroup();

    groupManager.removeGroup(groupManager.getGroup("testgroup").get());
  }

  @Test
  void setPermanentGroupForPlayer() {
    Optional<Group> group = groupManager.getGroup("testgroup");
    if(group.isEmpty()) return;

    groupManager.setPermanentGroupForPlayer(UUID.randomUUID(), group.get());

    group.get().addPermanentMember(UUID.randomUUID());
  }

  @Test
  void setGroupForPlayer() {
    Optional<Group> group = groupManager.getGroup("testgroup");
    if(group.isEmpty()) return;

    groupManager.setGroupForPlayer(UUID.randomUUID(), group.get(), 10L);
    group.get().addMember(UUID.randomUUID(), 10L);
  }

  @Test
  void getGroupForPlayer() {
    Group g = groupManager.getGroupForPlayer(UUID.randomUUID());
  }

  @Test
  void getGroups() {
    groupManager.getGroups();
  }

  @Test
  void getDefaultGroup() {
    Group group = groupManager.getDefaultGroup();
  }

  @Test
  void setDefaultGroup() {
    groupManager.setDefaultGroup(new Group("a"));
  }
}