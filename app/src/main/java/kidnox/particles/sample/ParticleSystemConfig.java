package kidnox.particles.sample;

import android.graphics.Color;

public final class ParticleSystemConfig {

    int fps = 60;
    int particlesCount = 2000;

    String baseColor = "#44cc77";

    float initialOffset = 1f;
    float ringOffset = 0.25f;
    float ringSize = 0.5f;
    int pointSize = 9; //dp

    float minParticleRadius = 0.2f;
    float minInitParticleRadius = 0.4f;

    float velocityDivider = 48000;
    float velocityMultiplier = 4;

    float angleMultiplier = 18f;
    float rMultiplier = 0.994f;
    float rInitialMultiplier = 1f;

    float xDivider = 2.4f;
    float yDivider = 1.8f;

    float rColorOffset = 0.33f;
    float gColorOffset = 0.33f;
    float bColorOffset = 0.33f;
    float aColorOffset = 0.66f;

    float lifeMultiplier = 0.03f;

    //////////////////////////////////////////////////////////////
    //transition
    //////////////////////////////////////////////////////////////
    int transitionType = 0;//-2 (<xy), -1(<x y>), 0 , 1 (>x y>), 2 (>x y<)
    float transitionTime = 6;
    int periodic = 1;
    float transitionDelta = 0;
    float xTransition = 1;
    float yTransition = 1;

    public ParticleSystemConfig() {
    }

    public ParticleSystemConfig(ParticleSystemConfig other) {
        this.fps = other.fps;
        this.particlesCount = other.particlesCount;
        this.baseColor = other.baseColor;
        this.initialOffset = other.initialOffset;
        this.ringOffset = other.ringOffset;
        this.ringSize = other.ringSize;
        this.pointSize = other.pointSize;
        this.minParticleRadius = other.minParticleRadius;
        this.minInitParticleRadius = other.minInitParticleRadius;
        this.velocityDivider = other.velocityDivider;
        this.velocityMultiplier = other.velocityMultiplier;
        this.angleMultiplier = other.angleMultiplier;
        this.rMultiplier = other.rMultiplier;
        this.rInitialMultiplier = other.rInitialMultiplier;
        this.xDivider = other.xDivider;
        this.yDivider = other.yDivider;
        this.rColorOffset = other.rColorOffset;
        this.gColorOffset = other.gColorOffset;
        this.bColorOffset = other.bColorOffset;
        this.aColorOffset = other.aColorOffset;
        this.lifeMultiplier = other.lifeMultiplier;
        this.transitionType = other.transitionType;
        this.transitionTime = other.transitionTime;
        this.periodic = other.periodic;
        this.transitionDelta = other.transitionDelta;
        this.xTransition = other.xTransition;
        this.yTransition = other.yTransition;
    }


    public ParticleSystemConfig copy() {
        return new ParticleSystemConfig(this);
    }

    public int getColor() {
        return Color.parseColor(baseColor);
    }

    public float getXTransition(float frame) {
        if(xTransition == 1) return 1;
        return xTransition * getTransitionValue(frame);
    }

    public float getYTransition(float frame) {
        if(yTransition == 1) return 1;
        return yTransition * getTransitionValue(frame);
    }

    public float getXDivider(int frame) {
        if(transitionType == 0) return xDivider;
        if(transitionType > 0) {
            return xDivider * getTransitionValue(frame);
        } else {
            return xDivider * (1 - getTransitionValue(frame));
        }
    }

    public float getYDivider(int frame) {
        if(transitionType == 0) return yDivider;
        if(Math.abs(transitionType) == 1) {
            return yDivider * getTransitionValue(frame);
        } else {
            return yDivider * (1 - getTransitionValue(frame));
        }
    }

    private float getTransitionValue(float frame) {
        float totalFrames = fps * transitionTime;
        float t = frame % totalFrames;
        t /= totalFrames;
        if(periodic != 0) {
            if(t < 0.5f) {
                t *= 2;
            } else {
                t = (1 - t) * 2;
            }
        }
        return (t + transitionDelta)/ (1f + transitionDelta);
    }


    @Override public String toString() {
        return "ParticleSystemConfig{" +
                "baseColor='" + baseColor + '\'' +
                ", initialOffset=" + initialOffset +
                ", ringOffset=" + ringOffset +
                ", ringSize=" + ringSize +
                ", pointSize=" + pointSize +
                ", minParticleRadius=" + minParticleRadius +
                ", minInitParticleRadius=" + minInitParticleRadius +
                ", velocityDivider=" + velocityDivider +
                ", velocityMultiplier=" + velocityMultiplier +
                ", angleMultiplier=" + angleMultiplier +
                ", rMultiplier=" + rMultiplier +
                ", rInitialMultiplier=" + rInitialMultiplier +
                ", xDivider=" + xDivider +
                ", yDivider=" + yDivider +
                ", rColorOffset=" + rColorOffset +
                ", gColorOffset=" + gColorOffset +
                ", bColorOffset=" + bColorOffset +
                ", aColorOffset=" + aColorOffset +
                ", lifeMultiplier=" + lifeMultiplier +
                '}';
    }
}
