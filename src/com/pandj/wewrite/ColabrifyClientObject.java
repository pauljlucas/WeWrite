package com.pandj.wewrite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import edu.umich.imlc.collabrify.client.CollabrifyAdapter;
import edu.umich.imlc.collabrify.client.CollabrifyClient;
import edu.umich.imlc.collabrify.client.CollabrifyListener;
import edu.umich.imlc.collabrify.client.CollabrifyParticipant;
import edu.umich.imlc.collabrify.client.CollabrifySession;
import edu.umich.imlc.collabrify.client.exceptions.CollabrifyException;
import edu.umich.imlc.collabrify.client.exceptions.LeaveException;

public class ColabrifyClientObject 
{
	//Note any overrided function in here defaults onto a background thread.
  private boolean createNewSession;

  public final List<String> tags = Arrays.asList("LAO");  
  public String sessionName;
  public CollabrifyClient myClient;
  public ColabrifyClientObject(Context context, boolean createSession, String email, String userName, CollabrifyListener inListen)
  {
	  this.createNewSession = createSession;
	  try 
	  {
		myClient = new CollabrifyClient(context, email, userName, "441fall2013@umich.edu", "XY3721425NoScOpE", true, inListen);
		if(myClient.inSession())
		{
			myClient.leaveSession(true);
		}
	  } catch (CollabrifyException e) 
	  {
		// TODO Auto-generated catch block
		e.printStackTrace();
	  }
  }
  
  public void enterSession() 
  {
    try
    {
      if(createNewSession)
      {
    	  Random rand = new Random();
    	  sessionName = "Jbarno" + rand.nextInt();
    	  myClient.createSession(sessionName, tags, null, 0);
    	  Log.i("CCO", "Attempting to Create Session");
      }
      else
      {
    	  myClient.requestSessionList(tags);
    	  Log.i("CCO", "Attempting to join Session");

      }
    }
    catch( CollabrifyException e )
    {
      e.printStackTrace();
    }	
  }

  public void destroy() 
  {
  	if(myClient.inSession())
  	  {
  		try 
  		{
  			//If we are the creator and leaving, we want the session destroyed
  			myClient.leaveSession(createNewSession);
  		} 
  		catch (LeaveException e) 
  		{
  			e.printStackTrace();
  		} 
  		catch (CollabrifyException e) 
  		{
  			e.printStackTrace();
  		}
  	  }
  }

}