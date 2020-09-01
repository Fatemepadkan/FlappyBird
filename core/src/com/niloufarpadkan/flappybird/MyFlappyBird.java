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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;


import java.util.Random;

public class MyFlappyBird extends ApplicationAdapter {
    private Stage buttonStage, soundStage, exitStage;
    public static float vol = 1.0f;
    private ImageButton playAgainButton, soundButton, yesButton, noButton;
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
    float velocity = 0;
    float gravity = 2.1f;
    Circle birdCircle;
    Rectangle[] topTubeRectangle, bottomTubeRectangle;
    int gameState = 0;   // 1=started  2=game over  0=waiting for a tap
    float gap = 450;
    float maxTubeOffset, distanceBetweenTubes;
    Random rand;
    float tubeVelocity = 8;
    int flag = 0; //when the user games over it turns to 1
    int numberOfTubes = 4;
    float[] tubeX = new float[numberOfTubes];
    float[] tubeOffset = new float[numberOfTubes];
    int highscore;

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

    public void initSoundButton() {

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
        soundButton.setPosition(Gdx.graphics.getWidth() - soundOn.getWidth() / 2 - 150, Gdx.graphics.getHeight() - soundOn.getHeight() / 2 - 150);

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
    }

    public void initExitButton() {
        yesButton = new ImageButton(
                new TextureRegionDrawable(new TextureRegion(yes))
        );
        noButton = new ImageButton(
                new TextureRegionDrawable(new TextureRegion(no))
        );

        Gdx.input.setInputProcessor(exitStage);
        yesButton.setPosition(Gdx.graphics.getWidth() / 2 - yes.getWidth() / 2, Gdx.graphics.getHeight() / 2 - yes.getHeight() / 2 - 350);
        noButton.setPosition(Gdx.graphics.getWidth() / 2 + no.getWidth() / 2, Gdx.graphics.getHeight() / 2 - no.getHeight() / 2 - 350);

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

    }
    @Override
    public void create() {
        setAssets();
        buttonStage = new Stage();
        soundStage = new Stage();
        exitStage = new Stage();
        birdX = Gdx.graphics.getWidth() / 2 - birds[0].getWidth() / 2;
        roofY = Gdx.graphics.getHeight() - birds[0].getHeight();
        Gdx.input.setCatchBackKey(true);
        batch = new SpriteBatch();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font.TTF"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 80;
        font = generator.generateFont(parameter);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter2 = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter2.size = 50;
        parameter2.color = Color.ORANGE;
        font2 = generator.generateFont(parameter2);
        generator.dispose(); // don't forget to dispose to avoid memory leaks!
        highscoreTracker = Gdx.app.getPreferences("game preferences");
        highscore = highscoreTracker.getInteger("highscore");
        shapeRenderer = new ShapeRenderer();
        birdCircle = new Circle();
        maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;
        rand = new Random();
        distanceBetweenTubes = Gdx.graphics.getWidth() / 2;
        topTubeRectangle = new Rectangle[numberOfTubes];
        bottomTubeRectangle = new Rectangle[numberOfTubes];
        initSoundButton();
        initExitButton();
        startGame();
    }

    public void startGame() {
        birdY = Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / 2;

        for (int i = 0; i < numberOfTubes; i++) {
            tubeOffset[i] = (rand.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
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
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (gameState == 1 && exitConfirm != 1) {
            removeActor(buttonStage);
            removeActor(soundStage);
            removeActor(exitStage);
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
                velocity = -35;
            }
            for (int i = 0; i < numberOfTubes; i++) {
                if (tubeX[i] < -topTube.getWidth()) {
                    tubeX[i] += numberOfTubes * distanceBetweenTubes;
                    tubeOffset[i] = (rand.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

                } else {
                    tubeX[i] -= tubeVelocity;
                }
                batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i] / 2);
                batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i] / 2);

                topTubeRectangle[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap - 243 + tubeOffset[i] / 2, topTube.getWidth(), topTube.getHeight());
                bottomTubeRectangle[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap + 243 - bottomTube.getHeight() + tubeOffset[i] / 2, bottomTube.getWidth(), bottomTube.getHeight());

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
            batch.draw(start, Gdx.graphics.getWidth() / 2 - start.getWidth() / 2, Gdx.graphics.getHeight() / 2 - start.getHeight() / 2);
            if (Gdx.input.justTouched()) {
                gameState = 1;
            }

        } else if (gameState == 2 && exitConfirm != 1) {
            //retain tubes
            removeActor(exitStage);
            for (int i = 0; i < numberOfTubes; i++) {
                batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i] / 2);
                batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i] / 2);
            }
            if (flag == 0)
                die.play(vol);
            flag = 1;

            if (score > highscore) {
                highscoreTracker.putInteger("highscore", score);
                highscoreTracker.flush();
                highscore = highscoreTracker.getInteger("highscore");
            }

            if (exitConfirm != 1) {
                batch.draw(gameOver, Gdx.graphics.getWidth() / 2 - gameOver.getWidth() / 2, Gdx.graphics.getHeight() / 2 - gameOver.getHeight() / 2);

                InputMultiplexer multiplexer = new InputMultiplexer();
                multiplexer.addProcessor(soundStage); // set stage as first input processor
                multiplexer.addProcessor(buttonStage); // set stage as first input processor
                Gdx.input.setInputProcessor(multiplexer);
                playAgainButton = new ImageButton(
                        new TextureRegionDrawable(new TextureRegion(playAgainTexture))
                );
                buttonStage.addActor(playAgainButton);
                playAgainButton.setPosition(Gdx.graphics.getWidth() / 2 - playAgainTexture.getWidth() / 2, Gdx.graphics.getHeight() / 2 - playAgainTexture.getHeight() / 2 - 150);  //hikeButton is an ImageButton

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


            }
            soundStage.addActor(soundButton);
            soundStage.act();
            soundStage.draw();

        }
        if (exitConfirm == 1) {
            removeActor(buttonStage);
            batch.draw(exit, Gdx.graphics.getWidth() / 2 - exit.getWidth() / 2, Gdx.graphics.getHeight() / 2 - exit.getHeight() / 2);
            //adding yes no button
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
            batch.draw(birds[flatState], birdX, birdY);
        font.draw(batch, String.valueOf(score), 100, Gdx.graphics.getHeight() - soundOn.getHeight() / 2 - 150);
        font2.draw(batch, "Your highscore :" + highscore, 100, 200);
        birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[flatState].getHeight() / 2, birds[flatState].getWidth() / 2 - 5);


//
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

        if (Gdx.input.isKeyPressed(Input.Keys.BACK)) {
            exitConfirm = 1;

            // Gdx.app.exit();
        }
        System.out.println(exitConfirm + "exit");
        batch.end();

    }


}
