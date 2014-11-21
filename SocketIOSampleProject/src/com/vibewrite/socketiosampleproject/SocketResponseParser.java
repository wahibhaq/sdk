package com.vibewrite.socketiosampleproject;

import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import com.vibewrite.socketiosampleproject.EnumerationList.*;




public class SocketResponseParser
{
	
public static Hashtable<String, Object> letterHashObj;
	 

public SocketResponseParser()
{
	letterHashObj = new Hashtable<>();

}

/**
 * This is to parse respone to "letter" event and extract all variables
 * 
 * @param jsonObj
 * @param mode
 * @return
 */
public Hashtable<String, Object> parseLetterParameters(JSONObject jsonObj, String mode)
{
		
		try {
		 
		if(jsonObj.length() > 0)
		 {
			if(mode.equals("Normal") && jsonObj.length() == LetterNormalEnum.values().length)
			{
			  
				  letterHashObj.put(LetterNormalEnum.eventname.toString(), jsonObj.getString(LetterNormalEnum.eventname.toString()));
				  letterHashObj.put(LetterNormalEnum.word.toString(), jsonObj.getString(LetterNormalEnum.word.toString()));
				  letterHashObj.put(LetterNormalEnum.spelling_mistake.toString(), jsonObj.getString(LetterNormalEnum.spelling_mistake.toString()));
				  letterHashObj.put(LetterNormalEnum.word_end.toString(), jsonObj.getString(LetterNormalEnum.word_end.toString()));
			}
			else if(mode.equals("Advanced") && jsonObj.length() == LetterAdvancedEnum.values().length)
			{
				  letterHashObj.put(LetterAdvancedEnum.eventname.toString(), jsonObj.getString(LetterAdvancedEnum.eventname.toString()));
				  letterHashObj.put(LetterAdvancedEnum.word.toString(), jsonObj.getString(LetterAdvancedEnum.word.toString()));
				  letterHashObj.put(LetterAdvancedEnum.word_current.toString(), jsonObj.getString(LetterAdvancedEnum.word_current.toString()));
				  letterHashObj.put(LetterAdvancedEnum.letter.toString(), jsonObj.getString(LetterAdvancedEnum.letter.toString()));
				  letterHashObj.put(LetterAdvancedEnum.true_letter.toString(), jsonObj.getString(LetterAdvancedEnum.true_letter.toString()));
				  letterHashObj.put(LetterAdvancedEnum.spelling_mistake.toString(), jsonObj.getString(LetterAdvancedEnum.spelling_mistake.toString()));
				  letterHashObj.put(LetterAdvancedEnum.word_end.toString(), jsonObj.getString(LetterAdvancedEnum.word_end.toString()));

			}
			  
		 }
		 else
		 {
				Log.i("SocketIOLayer", "SocketIOLayer Server said : letter object is blank");
				letterHashObj.clear();
		 }
		 
		 

	} catch (JSONException e) {
		// TODO Auto-generated catch block
		letterHashObj.clear();

		e.printStackTrace();
	}
	 
	 return letterHashObj;

	 
	
}


}