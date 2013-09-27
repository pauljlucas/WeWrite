package com.pandj.wewrite;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import edu.umich.imlc.collabrify.client.CollabrifyClient;
import edu.umich.imlc.collabrify.client.CollabrifyListener;
import edu.umich.imlc.collabrify.client.CollabrifyParticipant;
import edu.umich.imlc.collabrify.client.CollabrifySession;
import edu.umich.imlc.collabrify.client.exceptions.CollabrifyException;

public class ColabrifyClientObject implements CollabrifyListener
{
  private long sessionId;
  private Context context;
  private boolean createSession;
  
  public String sessionName;
  public CollabrifyClient myClient;
  public ColabrifyClientObject(Context input, boolean createSession)
  {
	  context = input;
	  this.createSession = createSession;
  }
  @Override
  public void onSessionCreated(long id)
  {
    sessionId = id;
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
    	Log.i("CCO", "No Session Available");
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

}
