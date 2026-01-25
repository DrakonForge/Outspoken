package io.github.drakonforge.outspoken.response;

public class NoneResponse implements Response {
    NoneResponse() {}

    @Override
    public ResponseType getType() {
        return ResponseType.None;
    }
}
