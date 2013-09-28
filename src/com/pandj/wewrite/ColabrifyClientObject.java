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
  private long sessionId;
  private Context context;
  private boolean createNewSession;
  private final List<String> tags = Arrays.asList("umich");

  
  public String sessionName;
  public CollabrifyClient myClient;
  private CustomAdapter adapter;
  public ColabrifyClientObject(Context input, boolean createSession, String email, String userName, Activity parent)
  {
	  context = input;
	  this.createNewSession = createSession;
	  try 
	  {
		adapter = new CustomAdapter(parent);
		myClient = new CollabrifyClient(parent, email, userName, "441fall2013@umich.edu", "XY3721425NoScOpE", false, adapter);
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
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		} catch (CollabrifyException e) 
  		{
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
  	  }
  }
private class CustomAdapter extends CollabrifyAdapter
{
  private Activity parent;
  public CustomAdapter(Activity input)
  {
	  parent = input;
  }
  @Override
  public void onSessionCreated(long id)
  {
    sessionId = id;
    Log.i("CCO", "Session created.");
  }
  @Override
  public void onSessionJoined(long maxOrderId, long baseFileSize)
  {
    Log.i("CCO", "SessionJoinedCalled");
  }
  @Override
  public void onReceiveSessionList(final List<CollabrifySession> sessionList)
  {
    if( sessionList.isEmpty())
    {
    	Log.i("CCO", "No Session Available using Tags: " + tags.get(0));
    	parent.runOnUiThread(new Runnable()
    	{
    		@Override
    		public void run()
    		{
    	    	Toast.makeText(context, "No possible Sessions to Join", Toast.LENGTH_SHORT).show();
    	        parent.finish();
    		}
    	});
    	return;
    }
    List<String> sessionNames = new ArrayList<String>();
    for(CollabrifySession s : sessionList)
    {
    	sessionNames.add(s.name());
    }
    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setTitle("Choose a session").setItems(
    		sessionNames.toArray(new String[sessionList.size()]), 
    		new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					try
					{
						sessionId = sessionList.get(which).id();
						sessionName = sessionList.get(which).name();
						myClient.joinSession(sessionId, null);
					}
					catch( CollabrifyException e)
					{
						Log.i("CCO", "Join Session Failed", e);
					}
				}
			});
    parent.runOnUiThread(new Runnable()
    {
		@Override
		public void run() {
			builder.show();
		}
    });
  }
  @Override
  public void onReceiveEvent(long orderId, int submissionRegistrationId,
      String eventType, byte[] data)
  {
	  Log.i("CCO", "Event recieved");
  }

  @Override
  public void onDisconnect()
  {
    Log.i("CCO", "Disconnect Triggered in Listener");

  }
  @Override 
  public void onError(CollabrifyException e)
  {
	  e.printStackTrace();
  }

}
}