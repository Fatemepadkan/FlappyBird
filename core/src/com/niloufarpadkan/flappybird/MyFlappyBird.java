package com.niloufarpadkan.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;


import java.util.Random;

public class MyFlappyBird extends ApplicationAdapter {
    private Stage buttonStage, soundStage, exitStage, startStage;
    public static float vol = 1.0f;
    private ImageButton playAgainButton, soundButton, yesButton, noButton, startButton;
    Preferences highscoreTracker;
    Sound point, die;
    Texture start, background, gameOver, topTube, bottomTube, playAgainTexture, soundOn, soundOff, exit, yes, no;
    SpriteBatch batch;
    ShapeRenderer shapeRenderer;
    Texture[] birds;
    int soundEnabled = 1;
    int exitConfirm = 0;
    int flatState = 0;
    int pause = 0;
    int score = 0;
    int scoringTube = 0;
    BitmapFont font, font2;
    float birdY, birdX, roofY;
    float scale, tubeWidth, tubeHeight, birdWidth, birdHeight, topTubeY, bottomTubeY;
    float velocity = 0;
    float gravity;
    Circle birdCircle;
    Rectangle[] topTubeRectangle, bottomTubeRectangle;
    int gameState = 0;   // 1=started  2=game over  0=waiting for a tap
    float gap = 450;
    float distanceBetweenTubes;
    Random rand;
    float tubeVelocity;
    int flag = 0; //when the user games over it turns to 1
    int numberOfTubes = 3;
    float[] tubeX = new float[numberOfTubes];
    float[] tubeOffset = new float[numberOfTubes];
    int highscore;
    InputMultiplexer multiplexer;
    public void setAssets() { //setting assets randomly
        soundOn = new Texture("soundon.png");
        soundOff = new Texture("soundoff.png");
        playAgainTexture = new Texture(Gdx.files.internal("playagain.png"));
        start = new Texture("start.png");
        gameOver = new Texture("gameover.png");
        exit = new Texture("exitConfirm.png");
        yes = new Texture("yes.png");
        no = new Texture("no.png");
        point = Gdx.audio.newSound(Gdx.files.internal("point.mp3"));
        die = Gdx.audio.newSound(Gdx.files.internal("die.mp3"));
        birds = new Texture[2];
        rand = new Random();
        int randomAsset = rand.nextInt(2);
        if (randomAsset % 2 == 0) {
            background = new Texture("bg1.png");
            birds[0] = new Texture("bird.png");
            birds[1] = new Texture("bird2.png");
            topTube = new Texture("toptube.png");
            bottomTube = new Texture("bottomtube.png");
        } else {
            background = new Texture("bg.png");
            birds[0] = new Texture("bird3.png");
            birds[1] = new Texture("bird4.png");
            topTube = new Texture("redpipeup.png");
            bottomTube = new Texture("redpipedown.png");
        }
    }
    public void initButtons() {
        if (vol > 0) {
            soundButton = new ImageButton(
                    new TextureRegionDrawable(new TextureRegion(soundOn))
            );
            soundButton.getStyle().imageChecked = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("soundoff.png"))));
            soundButton.getStyle().imageUp = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("soundon.png"))));
            soundEnabled = 1;
        } else {
            soundButton = new ImageButton(
                    new TextureRegionDrawable(new TextureRegion(soundOff))
            );
            soundButton.getStyle().imageChecked = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("soundon.png"))));
            soundButton.getStyle().imageUp = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("soundoff.png"))));
            soundEnabled = 0;
        }
        soundButton.setPosition(Gdx.graphics.getWidth() - (soundOn.getWidth() / 2 - 150) * scale, Gdx.graphics.getHeight() - (soundOn.getHeight() / 2 - 150) * scale);
        soundButton.setSize(soundButton.getWidth() * scale, soundButton.getHeight() * scale);
        soundButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (soundEnabled == 1) {
                    vol = 0;
                    soundEnabled = 0;
                } else {
                    vol = 1.0f;
                    soundEnabled = 1;
                }
            }
        });
        playAgainButton = new ImageButton(
                new TextureRegionDrawable(new TextureRegion(playAgainTexture))
        );
        playAgainButton.setPosition(Gdx.graphics.getWidth() / 2 - (playAgainTexture.getWidth() / 2 * scale), Gdx.graphics.getHeight() / 2 - playAgainTexture.getHeight() / 2 - 150 * scale);
        playAgainButton.setSize(playAgainButton.getWidth() * scale, playAgainButton.getHeight() * scale);
        playAgainButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                gameState = 1;
                startGame();
                score = 0;
                scoringTube = 0;
                velocity = 0;
                flag = 0;
                setAssets();
            }
        });
        yesButton = new ImageButton(
                new TextureRegionDrawable(new TextureRegion(yes))
        );
        noButton = new ImageButton(
                new TextureRegionDrawable(new TextureRegion(no))
        );
        yesButton.setPosition(4 * (Gdx.graphics.getWidth() / 5) - yes.getWidth() / 2 * scale, Gdx.graphics.getHeight() / 2 - (yes.getHeight() / 2 + 350) * scale);
        yesButton.setSize(yesButton.getWidth() * scale, yesButton.getHeight() * scale);
        noButton.setPosition(Gdx.graphics.getWidth() / 5 - no.getWidth() / 2 * scale, Gdx.graphics.getHeight() / 2 - (yes.getHeight() / 2 + 350) * scale);
        noButton.setSize(noButton.getWidth() * scale, noButton.getHeight() * scale);
        yesButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        noButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                exitConfirm = 0;
            }
        });

        startButton = new ImageButton(
                new TextureRegionDrawable(new TextureRegion(start))
        );
        startButton.setPosition(Gdx.graphics.getWidth() / 2 - start.getWidth() / 2 * scale, Gdx.graphics.getHeight() / 2 - start.getHeight() / 2 * scale);
        startButton.setSize(startButton.getWidth() * scale, startButton.getHeight() * scale);
        startButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                gameState = 1;
            }
        });
    }

    @Override
    public void create() {
        setAssets();
        scale = Gdx.graphics.getWidth() / 1080f;
        tubeHeight = topTube.getHeight() * scale;
        tubeWidth = topTube.getWidth() * scale;
        birdHeight = birds[0].getHeight() * scale;
        birdWidth = birds[0].getWidth() * scale;
        topTubeY = (Gdx.graphics.getHeight() + gap * scale) / 2;
        bottomTubeY = (-bottomTube.getHeight() * scale + ((Gdx.graphics.getHeight() - gap * scale) / 2));
        System.out.println(topTubeY + " - " + bottomTubeY);
        gravity = 2.5f * scale;
        tubeVelocity = 6 * scale;
        buttonStage = new Stage();
        soundStage = new Stage();
        exitStage = new Stage();
        startStage = new Stage();

        birdX = Gdx.graphics.getWidth() / 2 - birds[0].getWidth() * scale / 2;
        roofY = Gdx.graphics.getHeight() - birds[0].getHeight();
        Gdx.input.setCatchBackKey(true);
        batch = new SpriteBatch();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font.TTF"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = MathUtils.ceil(80 * scale);
        font = generator.generateFont(parameter);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter2 = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter2.size = MathUtils.ceil(50 * scale);
        parameter2.color = Color.ORANGE;
        font2 = generator.generateFont(parameter2);
        generator.dispose(); // don't forget to dispose to avoid memory leaks!
        highscoreTracker = Gdx.app.getPreferences("game preferences");
        highscore = highscoreTracker.getInteger("highscore");
        shapeRenderer = new ShapeRenderer();
        birdCircle = new Circle();
        rand = new Random();
        distanceBetweenTubes = Gdx.graphics.getWidth() * 5 / 8;
        topTubeRectangle = new Rectangle[numberOfTubes];
        bottomTubeRectangle = new Rectangle[numberOfTubes];
        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(soundStage);
        multiplexer.addProcessor(startStage);
        multiplexer.addProcessor(buttonStage);

        initButtons();
        startGame();

    }
    public void startGame() {
        birdY = Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / 2;
        for (int i = 0; i < numberOfTubes; i++) {
            tubeOffset[i] = (rand.nextFloat() * 2 - 1) * bottomTubeY / 2;
            tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenTubes;
            topTubeRectangle[i] = new Rectangle();
            bottomTubeRectangle[i] = new Rectangle();
        }
    }
    public void removeActor(Stage stage) {
        for (Actor actor : stage.getActors()) {
            actor.remove();
        }
    }
    @Override
    public void render() {
        batch.begin();
        Gdx.input.setInputProcessor(multiplexer);
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        if (gameState == 1 && exitConfirm != 1) {
            removeActor(buttonStage);
            removeActor(soundStage);
            removeActor(exitStage);
            removeActor(startStage);
            if (tubeX[scoringTube] < Gdx.graphics.getWidth() / 2) {
                score++;
                point.play(vol);
                if (scoringTube < numberOfTubes - 1) {
                    scoringTube++;
                } else {
                    scoringTube = 0;
                }
            }
            if (Gdx.input.justTouched()) {
                velocity = -40f * scale;
            }

            //Tube drawing
            for (int i = 0; i < numberOfTubes; i++) {
                if (tubeX[i] < -topTube.getWidth() * scale) {
                    tubeX[i] += numberOfTubes * distanceBetweenTubes;
                    tubeOffset[i] = (rand.nextFloat() * 2 - 1) * bottomTubeY / 2;
                } else {
                    tubeX[i] -= tubeVelocity;
                }
                batch.draw(topTube, tubeX[i], topTubeY + tubeOffset[i], tubeWidth, tubeHeight);
                batch.draw(bottomTube, tubeX[i], bottomTubeY + tubeOffset[i], tubeWidth, tubeHeight);

                topTubeRectangle[i] = new Rectangle(tubeX[i], topTubeY + tubeOffset[i], tubeWidth, tubeHeight);
                bottomTubeRectangle[i] = new Rectangle(tubeX[i], bottomTubeY + tubeOffset[i], tubeWidth, tubeHeight);

            }
            if (birdY > 0) {
                velocity = velocity + gravity;
                birdY = birdY - velocity / 2;
                if (birdY >= roofY) {
                    birdY = roofY;
                    velocity = 0;
                }
            } else {
                gameState = 2;
            }
        } else if (gameState == 0 && exitConfirm != 1) {
            removeActor(buttonStage);
            removeActor(exitStage);
            soundStage.addActor(soundButton);
            startStage.addActor(startButton);
        } else if (gameState == 2 && exitConfirm != 1) {
            //retain tubes
            removeActor(exitStage);
            removeActor(startStage);
            for (int i = 0; i < numberOfTubes; i++) {
                batch.draw(topTube, tubeX[i], topTubeY + tubeOffset[i], tubeWidth, tubeHeight);
                batch.draw(bottomTube, tubeX[i], bottomTubeY + tubeOffset[i], tubeWidth, tubeHeight);
            }
            if (flag == 0)
                die.play(vol);
            flag = 1;
            if (score > highscore) {
                highscoreTracker.putInteger("highscore", score);
                highscoreTracker.flush();
                highscore = highscoreTracker.getInteger("highscore");
            }
            batch.draw(gameOver, Gdx.graphics.getWidth() / 2 - gameOver.getWidth() / 2 * scale, Gdx.graphics.getHeight() / 2 - gameOver.getHeight() / 2 * scale
                    , gameOver.getWidth() * scale, gameOver.getHeight() * scale);


            buttonStage.addActor(playAgainButton);

            soundStage.addActor(soundButton);

        }
        if (exitConfirm == 1) {
            removeActor(buttonStage);
            removeActor(startStage);

            batch.draw(exit, Gdx.graphics.getWidth() / 2 - exit.getWidth() / 2 * scale, Gdx.graphics.getHeight() / 2 - exit.getHeight() / 2 * scale
                    , exit.getWidth() * scale, exit.getHeight() * scale);
            exitStage.addActor(yesButton);
            exitStage.addActor(noButton);
            Gdx.input.setInputProcessor(exitStage);
            exitStage.act();
            exitStage.draw();
        }

        if (flatState == 0) {
            if (pause < 8) {
                pause++;
            } else {
                flatState = 1;
                pause = 0;
            }
        } else {
            if (pause < 8) {
                pause++;
            } else {
                pause = 0;
                flatState = 0;
            }
        }
        if (gameState != 0)
            batch.draw(birds[flatState], birdX, birdY, birdWidth, birdHeight);
        font.draw(batch, String.valueOf(score), 100 * scale, Gdx.graphics.getHeight() - (soundOn.getHeight() + 100) * scale);
        font2.draw(batch, "Your highscore :" + highscore, 100 * scale, 300 * scale);
        birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[flatState].getHeight() * scale / 2, (birds[flatState].getWidth() / 2 - 12) * scale);
//		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//			shapeRenderer.setColor(Color.RED);
//		shapeRenderer.circle(birdCircle.x,birdCircle.y,birdCircle.radius);
        for (int i = 0; i < numberOfTubes; i++) {
//shapeRenderer.rect(tubeX[i],Gdx.graphics.getHeight() / 2 + gap-243  + tubeOffset[i] / 2,topTube.getWidth(),topTube.getHeight());
//shapeRenderer.rect(tubeX[i],Gdx.graphics.getHeight() / 2 - gap +243 - bottomTube.getHeight() + tubeOffset[i]/2 ,bottomTube.getWidth(),bottomTube.getHeight());
            if (Intersector.overlaps(birdCircle, bottomTubeRectangle[i]) || Intersector.overlaps(birdCircle, topTubeRectangle[i])) {
                gameState = 2;
            }
        }
//		shapeRenderer.end();
        buttonStage.act(); //Perform ui logic
        buttonStage.draw(); //Draw the uij
        startStage.act();
        startStage.draw();
        soundStage.act();
        soundStage.draw();
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            exitConfirm = 1;
        }
        batch.end();
    }


}
