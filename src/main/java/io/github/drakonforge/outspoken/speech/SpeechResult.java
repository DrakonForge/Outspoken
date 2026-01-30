package io.github.drakonforge.outspoken.speech;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.Message;

public record SpeechResult(Message displayName, Message text, Vector3d origin, float timeToSpeakLine) {
}
