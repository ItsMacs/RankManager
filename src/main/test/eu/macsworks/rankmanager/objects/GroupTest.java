package eu.macsworks.rankmanager.objects;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GroupTest {

  private Group group;

  @BeforeEach
  void setUp() {
    group = new Group("test");
  }

  @Test
  void isMember() {
    group.isMember(UUID.randomUUID());
  }

  @Test
  void addPermanentMember() {
    group.addPermanentMember(UUID.randomUUID());
  }

  @Test
  void addMember() {
    group.addMember(UUID.randomUUID(), 10L);
  }

  @Test
  void removeMember() {
    group.removeMember(UUID.randomUUID());
  }

  @Test
  void hasPermission() {
    group.hasPermission("testperm");
  }

  @Test
  void getExpirationDate() {
    group.getExpirationDate(UUID.randomUUID());
  }

  @Test
  void addPermission() {
    group.addPermission("testperm");
  }

  @Test
  void removePermission() {
    group.removePermission("testperm");
  }

  @Test
  void getMembersString() {
    System.out.println(group.getMembersString());
  }

  @Test
  void getPermissionsString() {
    System.out.println(group.getPermissionsString());
  }

  @Test
  void getName() {
    group.getName();
  }

  @Test
  void getPrefix() {
    group.getPrefix();
  }

  @Test
  void setPrefix() {
    group.setPrefix("test");
  }
}