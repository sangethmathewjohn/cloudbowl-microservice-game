package hello;

enum Action {
  F("F"),
  L("R"),
  R("L"),
  T("T");

  public Action opposite;
  private String oppositeStr;

  static {
    for (Action action: values()) {
      action.opposite = valueOf(action.oppositeStr);
    }
  }

  Action(String opposite) {
    this.oppositeStr = opposite;
  }
}
