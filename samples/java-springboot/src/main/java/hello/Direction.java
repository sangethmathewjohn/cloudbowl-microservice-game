package hello;

enum Direction {

  N("W", "E", "S", (from, to) -> from.y - to.y),
  S("E", "W", "N", (from, to) -> to.y - from.y),
  E("N", "S", "W", (from, to) -> to.x - from.x),
  W("S", "N", "E", (from, to) -> from.x - to.x);

  public Direction left;
  public Direction right;
  public Direction opposite;
  public CellDistanceCalc distance;

  private String leftStr;
  private String rightStr;
  private String oppositeStr;

  static {
    for (Direction direction: Direction.values()) {
      direction.left = Direction.valueOf(direction.leftStr);
      direction.right = Direction.valueOf(direction.rightStr);
      direction.opposite = Direction.valueOf(direction.oppositeStr);
    }
  }

  private Direction(String left, String right, String opposite, CellDistanceCalc distanceCalc) {
    this.leftStr = left;
    this.rightStr = right;
    this.oppositeStr = opposite;
    this.distance = distanceCalc;
  }

  public Direction getDirectionAfter(Action action) {
    switch (action) {
    case L:
      return left;
    case R:
      return right;
    default:
      return this;
    }
  }

  public interface CellDistanceCalc {
    int fromTo(Cell p1, Cell p2);
  }

}
