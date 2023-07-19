package org.traud.video;

import org.jcodec.common.model.Size;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public interface IFrameGrabber {
    enum Mode {
        PRECISE,
        SLOPPY
    };

    void setMode(Mode mode);

    static IFrameGrabber createFrameGrabber(File videoFile) throws IOException {
        String fileName = videoFile.getName().toLowerCase();
        if (fileName.endsWith(".wmv") || fileName.endsWith(".flv"))
        {
            return new FrameGrabberJavaCV(videoFile);
        }
        return new FrameGrabberJCodec(videoFile);
    }

    BufferedImage grab(double secondsOffset) throws IOException;

    BufferedImage grab() throws IOException;

    Size getDimensions();

    void close() throws IOException;

    int getTotalFrames();

    double getTotalDurationSeconds();

    double getFrameRate();
}
