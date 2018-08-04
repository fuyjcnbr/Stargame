package ru.geekbrains.stargame.sprite;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.geekbrains.stargame.base.Ship;
import ru.geekbrains.stargame.math.Rect;
import ru.geekbrains.stargame.math.Rnd;
import ru.geekbrains.stargame.pools.BulletPool;
import ru.geekbrains.stargame.pools.ExplosionPool;


public class Enemy extends Ship {

    private enum State {DESCENT, FIGHT}

    private MainShip mainShip;

    private State state;

    private Vector2 v0 = new Vector2();
    private Vector2 descentV = new Vector2(0, -0.15f);

    public Enemy(BulletPool bulletPool, Rect worldBounds, ExplosionPool explosionPool, MainShip mainShip, Sound sound) {
        super(bulletPool, worldBounds, explosionPool, sound);
        this.v.set(v0);
        this.state = State.DESCENT;
        this.v.set(descentV);
        this.mainShip = mainShip;

//        setInitialPosSpeed();
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (isNearLeft()) {
            setSpeed(true);
        } else if (isNearRight()) {
            setSpeed(false);
        }
        pos.mulAdd(v, delta);
        switch (state) {
            case DESCENT:
                if (getTop() <= worldBounds.getTop()) {
//                    v.set(v0);
//                    setInitialPosSpeed();
                    state = State.FIGHT;
                }
                break;
            case FIGHT:
                reloadTimer += delta;
                if (reloadTimer >= reloadInterval) {
                    reloadTimer = 0f;
                    shoot();
                }
                if (getBottom() < worldBounds.getBottom()) {
                    mainShip.damage(bulletDamage);
                    boom();
                    destroy();
                }
                break;
        }
    }

    public void set(
            TextureRegion[] regions,
            Vector2 v0,
            TextureRegion bulletRegion,
            float bulletHeight,
            float bulletVY,
            int bulletDamage,
            float reloadInterval,
            float height,
            int hp
    ) {
        this.regions = regions;
        this.v0.set(v0);
        this.bulletRegion = bulletRegion;
        this.bulletHeight = bulletHeight;
        this.bulletV.set(0f, bulletVY);
        this.bulletDamage = bulletDamage;
        this.reloadInterval = reloadInterval;
        setHeightProportion(height);
        reloadTimer = reloadInterval;
        this.v.set(descentV);
        this.state = State.DESCENT;
        this.hp = hp;

//        setInitialPosSpeed();
    }


    public void setSpeed(boolean is_left) {
        float angle;
        if (is_left) {
            angle = Rnd.nextFloat(30, 70);
            v.rotate(angle);
        } else {
            angle = Rnd.nextFloat(-70, -30);
            v.rotate(angle);
        }
    }

    private boolean isNearLeft() {
        if (getLeft() <= worldBounds.getLeft()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isNearRight() {
        if (getRight() >= worldBounds.getRight()) {
            return true;
        } else {
            return false;
        }
    }


    private void setInitialPosSpeed() {
        float f = Rnd.nextFloat(0, 1);
        float angle;

        if (f <= 0.33) {
            this.pos.y = this.halfHeight;
            this.pos.x = Rnd.nextFloat(worldBounds.getLeft() + getHalfWidth(), worldBounds.getRight() - getHalfWidth());
        } else if (f <= 0.66) {
            this.pos.x = this.halfWidth;
            this.pos.y = Rnd.nextFloat(worldBounds.getTop(), worldBounds.getBottom() * (float)0.8);
            angle = Rnd.nextFloat(30, 70);
            descentV.rotate(angle);
        } else {
            this.pos.x = worldBounds.getRight() - this.halfWidth;
            this.pos.y = Rnd.nextFloat(worldBounds.getTop(), worldBounds.getBottom() * (float)0.8);
            angle = Rnd.nextFloat(-70, -30);
            descentV.rotate(angle);
        }
    }


    public boolean isBulletCollision(Rect bullet) {
        return !(bullet.getRight() < getLeft()
                || bullet.getLeft() > getRight()
                || bullet.getBottom() > getTop()
                || bullet.getTop() < pos.y);
    }
}
