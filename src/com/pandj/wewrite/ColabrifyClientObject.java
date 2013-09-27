package com.pandj.wewrite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import edu.umich.imlc.collabrify.client.CollabrifyClient;
import edu.umich.imlc.collabrify.client.CollabrifyListener;
import edu.umich.imlc.collabrify.client.CollabrifyParticipant;
import edu.umich.imlc.collabrify.client.CollabrifySession;
import edu.umich.imlc.collabrify.client.exceptions.CollabrifyException;
import edu.umich.imlc.collabrify.client.exceptions.LeaveException;

public class ColabrifyClientObject implements CollabrifyListener
{
  private long sessionId;
  private Context context;
  private boolean createNewSession;
  private final List<String> tags = Arrays.asList("");

  
  public String sessionName;
  public CollabrifyClient myClient;
  public ColabrifyClientObject(Context input, boolean createSession, String email, String userName)
  {
	  context = input;
	  this.createNewSession = createSession;
	  try 
	  {
		myClient = new CollabrifyClient(context, email, userName, "411fall2013@umich.edu", "XY3721425NoScOpE", true, this);
	  } catch (CollabrifyException e) 
	  {
		// TODO Auto-generated catch block
		e.printStackTrace();
	  }
  }
  @Override
  public void onSessionCreated(long id)
  {
    sessionId = id;
    Log.i("CCO", "Session created.");
  }

  @Override
  public byte[] onBaseFileChunkRequested(long currentBaseFileSize)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void onBaseFileUploadComplete(long baseFileSize)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void onSessionJoined(long maxOrderId, long baseFileSize)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void onBaseFileChunkReceived(byte[] baseFileChunk)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void onDisconnect()
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void onReceiveEvent(long orderId, int submissionRegistrationId,
      String eventType, byte[] data)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void onReceiveSessionList(final List<CollabrifySession> sessionList)
  {
    if( sessionList.isEmpty())
    {
    	Log.i("CCO", "No Session Available using Tags: " + tags.get(0));
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
  }

  @Override
  public void onParticipantJoined(CollabrifyParticipant p)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void onParticipantLeft(CollabrifyParticipant p)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void onError(CollabrifyException e)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void onSessionEnd(long id)
  {
    // TODO Auto-generated method stub

  }
public void enterSession() 
{
  try
  {
    if(createNewSession)
    {
  	  Random rand = new Random();
  	  sessionName = "Test" + rand.nextInt();
  	  myClient.createSession(sessionName, tags, null, 10);
  	  Log.i("CCO", "Attempting to Create Session");
    }
    else
    {
  	  myClient.requestSessionList(tags);
    }
  }
  catch( CollabrifyException e )
  {
    e.printStackTrace();
  }	
}
public void disconnect() 
{
	if(myClient.inSession())
	{
		try 
		{
			myClient.leaveSession(false);
		} catch (LeaveException e) 
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

}
