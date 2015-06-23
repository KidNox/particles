package kidnox.particles;


public interface GLProgram {

    void onBegin(GLEngine glEngine);

    void drawFrame(GLEngine glEngine, long currentTime);

    void onEnd(GLEngine glEngine);

    void onSizeChanged(GLEngine glEngine);

}
