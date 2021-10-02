package hello;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import org.springframework.core.style.ToStringCreator;

import hello.Application.ArenaUpdate;
import hello.Application.PlayerState;

class CellData {

  public Map<Direction, PlayerState> targets = new EnumMap<>(Direction.class);
  public Map<Direction, PlayerState> shooters = new EnumMap<>(Direction.class);
  public Set<Direction> space = EnumSet.noneOf(Direction.class);

  private Cell cell;
  private ArenaUpdate arenaUpdate;

  CellData(ArenaUpdate arenaUpdate, Cell cell) {
    this.arenaUpdate = arenaUpdate;
    this.cell = cell;
    analyseOponents();
    analyseSpace();
  }

  private void analyseOponents() {
    for (PlayerState nextPlayer: arenaUpdate.arena.state.values()) {
      if (nextPlayer.x == this.cell.x) {
        // same column, opponent can be N or S
        if (!analyseNextPlayer(nextPlayer, Direction.S)) {
          // opponent was not S
          analyseNextPlayer(nextPlayer, Direction.N);
        }
      } else if (nextPlayer.y == this.cell.y) {
        // same row, opponent may be E or W
        if (!analyseNextPlayer(nextPlayer, Direction.W)) {
          // opponent was not W
          analyseNextPlayer(nextPlayer, Direction.E);
        }
      }
    }
  }

  /**
   * @return true if the nextPlayer is in the directionFromUs, false otherwise.
   */
  private boolean analyseNextPlayer(PlayerState newPlayer, Direction directionFromUs) {
    int newDistance = directionFromUs.distance.fromTo(cell, newPlayer);
    if (newDistance <= 0) {
      // New player was on the other side (or it was actually us)
      return false;
    }

    if (newDistance < 4) {
      // New player in range.

      PlayerState previous = targets.get(directionFromUs);
      if (previous == null || newDistance < directionFromUs.distance.fromTo(cell, previous)) {
        // New target.
        targets.put(directionFromUs, newPlayer);

        // Check if they are a potential shooter (looking at us).
        if (newPlayer.direction == directionFromUs.opposite) {
          shooters.put(directionFromUs, newPlayer);
        } else {
          // the new target is closer, but is not looking at us,
          // so there is no shooter in this direction.
          shooters.remove(directionFromUs);
        }
      }
    }
    return true;
  }

  private void analyseSpace() {
    // assumes opponents analysed
    if (this.cell.y != 0) {
      checkSpace(Direction.N);
    }
    if (this.cell.y < arenaUpdate.arena.dims.get(1) - 1) {
      checkSpace(Direction.S);
    }
    if (this.cell.x != 0) {
      checkSpace(Direction.W);
    }
    if (this.cell.x < arenaUpdate.arena.dims.get(0) - 1) {
      checkSpace(Direction.E);
    }
  }

  private void checkSpace(Direction direction) {
    PlayerState target = targets.get(direction);
    Integer distance = target == null ? null : direction.distance.fromTo(cell, target);
    if (target == null || distance > 1) {
      space.add(direction);
    }
  }

  @Override
  public String toString() {
    return new ToStringCreator(this)
        .append("cell", this.cell)
        .append("targets", targets)
        .append("shooters", shooters)
        .append("space", space).toString();
  }

  boolean isPossible(Direction currentDirection, Action action) {
    Direction newDirection = currentDirection.getDirectionAfter(action);
    return space.contains(newDirection);
  }

}
