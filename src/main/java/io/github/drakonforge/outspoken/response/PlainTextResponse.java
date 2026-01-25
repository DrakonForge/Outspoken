package io.github.drakonforge.outspoken.response;

import com.hypixel.hytale.math.util.MathUtil;
import javax.annotation.Nonnull;

public class PlainTextResponse implements Response {

    private final String[] options;

    public PlainTextResponse(@Nonnull String[] options) {
        this.options = options;
    }

    public String getRandomOption() {
        return options[MathUtil.floor(Math.random() * options.length)];
    }

    public String[] getOptions() {
        return options;
    }

    @Override
    public ResponseType getType() {
        return ResponseType.PlainText;
    }
}
