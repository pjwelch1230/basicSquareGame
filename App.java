
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Map;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.time.TimerAction;

import java.util.Random;

import static com.almasb.fxgl.dsl.FXGL.*;

public class App extends GameApplication {

    private TimerAction timerAct;
    
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(800);

        settings.setManualResizeEnabled(true);
    }

    public enum EntityType {
        PLAYER, WALLTOP, WALLBOTTOM, WALLLEFT, WALLRIGHT
    }

    @Override
    protected void initInput() {
        onKey(KeyCode.D, () -> {
            player.translateX(5); // move right 5 pixels
            //inc("pixelsMoved", +5);
        });

        onKey(KeyCode.A, () -> {
            player.translateX(-5); // move left 5 pixels
            //inc("pixelsMoved", -5);
        });

        onKey(KeyCode.W, () -> {
            player.translateY(-5); // move up 5 pixels
            //inc("pixelsMoved", +5);
        });

        onKey(KeyCode.S, () -> {
            player.translateY(5); // move down 5 pixels
            //inc("pixelsMoved", +5);
        });
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("timer", 20);
        vars.put("strTimer", "");
    }

    private Entity player;
    private Entity wallTop;
    private Entity wallBottom;
    private Entity wallLeft;
    private Entity wallRight;

    @Override
    protected void initGame() {
        Random color = new Random();
        Random size = new Random();

        int upperbound = 5;
        int colornum = color.nextInt(upperbound);
        int sizenum = size.nextInt(upperbound);

        double squareSize = 10;
        Paint squareColor = Color.WHITE;

        System.out.println(colornum +", "+ sizenum);

        switch(colornum) {
            case 0:
                squareColor = Color.WHITE;
                break;
            case 1:
                squareColor = Color.BLUE;
                break;
            case 2:
                squareColor = Color.RED;
                break;
            case 3:
                squareColor = Color.YELLOW;
                break;
            case 4:
                squareColor = Color.GREEN;
                break;
            case 5:
                squareColor = Color.BLACK;
                break;
        }

        switch(sizenum) {
            case 0:
                squareSize = 10;
                break;
            case 1:
                squareSize = 15;
                break;
            case 2:
                squareSize = 20;
                break;
            case 3:
                squareSize = 25;
                break;
            case 4:
                squareSize = 30;
                break;
            case 5:
                squareSize = 35;
                break;
        }

        Rectangle playerRect = new Rectangle(squareSize, squareSize, squareColor);
        playerRect.setStroke(Color.BLACK);
        
        player = entityBuilder()
                .type(EntityType.PLAYER)
                .at(400, 400)
                .viewWithBBox(playerRect)
                .with(new CollidableComponent(true))
                .buildAndAttach();


        // Boundaries
        Rectangle wall = new Rectangle(800,800);
        wall.setFill(Color.WHITE);
        Rectangle wall1 = new Rectangle(800,800);
        wall1.setFill(Color.WHITE);
        Rectangle wall2 = new Rectangle(800,800);
        wall2.setFill(Color.WHITE);
        Rectangle wall3 = new Rectangle(800,800);
        wall3.setFill(Color.WHITE);

        // TOP
        wallTop = entityBuilder()
                .type(EntityType.WALLTOP)
                .at(0, -798)
                .viewWithBBox(wall)
                .with(new CollidableComponent(true))
                .buildAndAttach();
        // BOTTOM
        wallBottom = entityBuilder()
                .type(EntityType.WALLBOTTOM)
                .at(0, 798)
                .viewWithBBox(wall1)
                .with(new CollidableComponent(true))
                .buildAndAttach();
        // LEFT
        wallLeft = entityBuilder()
                .type(EntityType.WALLLEFT)
                .at(-798, 0)
                .viewWithBBox(wall2)
                .with(new CollidableComponent(true))
                .buildAndAttach();
        // RIGHT
        wallRight = entityBuilder()
                .type(EntityType.WALLRIGHT)
                .at(798, 0)
                .viewWithBBox(wall3)
                .with(new CollidableComponent(true))
                .buildAndAttach();


        timerAct = FXGL.getGameTimer().runAtInterval(() -> {
            if (FXGL.geti("timer") == 0) {
                player.removeFromWorld();
            }
            FXGL.inc("timer", -1);
            FXGL.set("strTimer", secsToString(FXGL.geti("timer")));
        }, Duration.seconds(1));
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.WALLTOP) {
            @Override
            protected void onCollisionBegin(Entity player, Entity wallTop) {
                player.translateY(20);
            }
        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.WALLBOTTOM) {
            @Override
            protected void onCollisionBegin(Entity player, Entity wallBotom) {
                player.translateY(-20);
            }
        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.WALLLEFT) {
            @Override
            protected void onCollisionBegin(Entity player, Entity wallLeft) {
                player.translateX(20);
            }
        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.WALLRIGHT) {
            @Override
            protected void onCollisionBegin(Entity player, Entity wallRight) {
                player.translateX(-20);
            }
        });
    }

    @Override
    protected void initUI() {
        Label timerLabel = new Label();
        timerLabel.setTextFill(Color.RED);
        timerLabel.setFont(Font.font("verdana", FontWeight.EXTRA_BOLD, null, 32));
        timerLabel.textProperty().bind(FXGL.getsp("strTimer"));
        FXGL.addUINode(timerLabel, 195, 85);
    }

    private String secsToString (int time) {
        if (time < 0) {
            return String.format("Time Remaining: -%02d:%02d", -time / 60, -time % 60); 
        }
        return String.format("Time Remaining: %02d:%02d", time / 60, time % 60);
    }

    public static void main(String[] args) {
        launch(args);
    }
}