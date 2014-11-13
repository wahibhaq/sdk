package com.vibewrite.socketiosampleproject;


public class EnumerationList
{
	
	public enum CommandTypeEnum {vibrate, led, handwritingsettings};
	public enum CommandEnum {command, params};
		 
	public enum VibrateEnum {duration, repeat};
	public enum LedEnum {color, duration};
	public enum HandWritingSettingsEnum {key, value};
	public enum AvailableHandWritingEnum {dictionary, writer};
	 
	public enum EventEnum {letter, word, sensordata, disconnect, idle, gesture};
	public enum LetterNormalEnum {eventname, word, spelling_mistake, word_end};
	public enum LetterAdvancedEnum {eventname, word, word_current, letter, true_letter, spelling_mistake, word_end};

}