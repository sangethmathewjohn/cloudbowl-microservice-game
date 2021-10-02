package hello;

public class Cell {

  public int x;
  public int y;

  public static Cell at(int x, int y) {
    Cell cell = new Cell();
    cell.x = x;
    cell.y = y;
    return cell;
  }

  @Override
  public String toString() {
    return "[" + x + "," + y + "]";
  }

}
