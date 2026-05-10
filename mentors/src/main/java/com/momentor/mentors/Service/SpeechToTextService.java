package com.momentor.mentors.Service;
import java.io.File;
public interface SpeechToTextService { //It Act As a Socket in Microphone as Speech- Text Conversion(It is Must For Speech To Text Conversion.
    String transcribe(File audioFile); //transcribe is expected outcome as Text
}
