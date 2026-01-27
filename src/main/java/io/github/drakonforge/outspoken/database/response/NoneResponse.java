package io.github.drakonforge.outspoken.database.response;

public class NoneResponse implements Response {
    NoneResponse() {}

    @Override
    public ResponseType getType() {
        return ResponseType.None;
    }
}
