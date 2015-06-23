package kidnox.particles;


public interface GLProgram {

    void onBegin(GLEngine glEngine);

    void drawFrame(GLEngine glEngine);

    void onEnd(GLEngine glEngine);

}
