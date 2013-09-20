package com.pandj.wewrite;

import java.util.List;

import edu.umich.imlc.collabrify.client.CollabrifyClient;
import edu.umich.imlc.collabrify.client.CollabrifyListener;
import edu.umich.imlc.collabrify.client.CollabrifyParticipant;
import edu.umich.imlc.collabrify.client.CollabrifySession;
import edu.umich.imlc.collabrify.client.exceptions.CollabrifyException;

public class ColabrifyClientObject implements CollabrifyListener
{
  private CollabrifyClient myClient = null;
  public CollabrifyClient getMyClient()
  {
    return myClient;
  }
  //Make this a singleton
  private static ColabrifyClientObject instance = null;
  private ColabrifyClientObject() { };
  public static ColabrifyClientObject getInstance() 
  {
    if (instance == null) 
    {
      instance = new ColabrifyClientObject();
    }
    return instance;
  }
  
  //Deal with Credentials
  private String email = null;
  private String userName = null;
  public boolean changeCredentials(String email, String userName)
  {
    //Simple scenario, where this is the first user in this instance of the app
    if((email == null)&&(userName == null)&&(myClient == null))
    {
      this.email = email;
      this.userName = userName;
//      
//      try//Don't know what null in the context is going to do, this might have to be re-thought out.
//      {
//        //myClient = new CollabrifyClient(this, email, userName, "411fall2013@umich.edu", "XY3721425NoScOpE", true, this);
//      }
//     // catch(CollabrifyException e)
//      {
//        e.printStackTrace();
//      }
//     
    }
    else //More difficult problem
    {
      
    }
    return true;
  }
  
  @Override
  public void onSessionCreated(long id)
  {
    // TODO Auto-generated method stub

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
  public void onReceiveSessionList(List<CollabrifySession> sessionList)
  {
    // TODO Auto-generated method stub

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
