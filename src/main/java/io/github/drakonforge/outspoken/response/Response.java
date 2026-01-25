package io.github.drakonforge.outspoken.response;

public interface Response {

    Response EMPTY = new NoneResponse();

    enum ResponseType {
        None, PlainText, Speech
    }

    ResponseType getType();
}

// TODO: Maybe rename
