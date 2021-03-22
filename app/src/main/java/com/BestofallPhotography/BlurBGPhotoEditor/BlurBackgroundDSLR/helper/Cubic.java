package com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.helper;

/**
 * Created by lenovo on 6/21/2017.
 */
public class Cubic {

    public static float easeOut( float time, float start, float end, float duration )
    {
        return end * ( ( time = time / duration - 1 ) * time * time + 1 ) + start;
    }
}
