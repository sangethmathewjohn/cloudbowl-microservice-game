package hello;

import java.util.List;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Application {

  static class Self {
    public String href;
  }

  static class Links {
    public Self self;
  }

  static class PlayerState extends Cell {
    public Direction direction;
    public boolean wasHit;
    public int score;
  }

  static class Arena {
    public List<Integer> dims;
    public Map<String, PlayerState> state;
  }

  static class ArenaUpdate {
    public Links _links;
    public Arena arena;
  }

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @InitBinder
  public void initBinder(WebDataBinder binder) {
    binder.initDirectFieldAccess();
  }

  @GetMapping("/")
  public String index() {
    return "Let the battle begin!";
  }

  @PostMapping("/**")
  public String index(@RequestBody ArenaUpdate arenaUpdate) {
    Action action = new Responder(arenaUpdate).respond();
    return action.name();
  }

  class Responder {
    CellData locationData;
    ArenaUpdate arenaUpdate;
    PlayerState self;

    Responder(ArenaUpdate arenaUpdate) {
      this.arenaUpdate = arenaUpdate;
      this.self = arenaUpdate.arena.state.get(arenaUpdate._links.self.href);
      this.locationData = new CellData(arenaUpdate, self);
    }

    Action respond() {
      if (self.wasHit || locationData.shooters.size() > 1) {
        if (locationData.space.contains(self.direction)
            && locationData.shooters.get(self.direction) == null) {
          return Action.F;
        }
        Action action = Action.L;
        if (locationData.isPossible(self.direction, action)) {
          return action;
        }
        return action.opposite;
      }
      if (locationData.targets.get(self.direction) != null) {
        return Action.T;
      }
      Action action = evaluateTarget(Action.L);
      if (action != null) {
        return action;
      }
      action = evaluateTarget(Action.R);
      if (action != null) {
        return action;
      }
      if (locationData.space.contains(self.direction)) {
        return Action.F;
      }
      action = Action.R;
      if (locationData.isPossible(self.direction, action)) {
        return action;
      }
      if (locationData.isPossible(self.direction, action.opposite)) {
        return action.opposite;
      }
      return Action.T;
    }

    private Action evaluateTarget(Action turn) {
      Direction newDirection = self.direction.getDirectionAfter(turn);
      if (locationData.targets.get(newDirection) == null) {
        // no target on this side
        return null;
      }
      if (locationData.shooters.get(newDirection) != null) {
        // aiming at us - run away
        if (locationData.space.contains(self.direction)) {
          return Action.F;
        } // can't runaway
      } // not a shooter
      // shootout
      return turn;
    }

  }

}
